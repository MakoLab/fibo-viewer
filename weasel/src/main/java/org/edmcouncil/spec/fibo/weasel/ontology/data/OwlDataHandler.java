package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.ArrayList;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.OwlTaxonomy;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.visitor.WeaselOntologyVisitors;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.springframework.stereotype.Component;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class OwlDataHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwlDataHandler.class);

  private final OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();
  @Autowired
  private OwlDataExtractor dataExtractor;

  public OwlDetails handleParticularClass(IRI iri, OWLOntology ontology) {
    OwlDetails resultDetails = new OwlDetails();
    Iterator<OWLClass> classesIterator = ontology.classesInSignature().iterator();

    while (classesIterator.hasNext()) {
      OWLClass clazz = classesIterator.next();

      if (clazz.getIRI().equals(iri)) {
        LOGGER.debug("Find owl class wih iri: {}", iri.toString());

        resultDetails.setLabel(clazz.getIRI().getShortForm());

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(clazz, ontology);

        List<PropertyValue> subclasses = axioms.getProperties().getOrDefault(AxiomType.SUBCLASS_OF.getName(), new ArrayList<>(0));

        OwlTaxonomyImpl tax = extractTaxonomy(subclasses, iri, ontology);
        tax.sort();
        LOGGER.debug(tax.toString());

        resultDetails.setTaxonomy(tax);
        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(clazz.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    resultDetails.sortProperties(new LinkedList<>());
    //wd.sortProperties(prioritySortList);
    return resultDetails;
  }

  public OwlDetails handleParticularIndividual(IRI iri, OWLOntology ontology) {
    OwlDetails resultDetails = new OwlDetails();
    Iterator<OWLNamedIndividual> individualIterator = ontology.individualsInSignature().iterator();

    while (individualIterator.hasNext()) {
      OWLNamedIndividual individual = individualIterator.next();

      if (individual.getIRI().equals(iri)) {
        LOGGER.debug("Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(individual.getIRI().getShortForm());

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(individual, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(individual.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    resultDetails.sortProperties(new LinkedList<>());
    //wd.sortProperties(prioritySortList);
    return resultDetails;
  }

  private OwlDetailsProperties<PropertyValue> handleAnnotations(IRI iri, OWLOntology ontology) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLAnnotationAssertionAxiom> annotationAssertionAxiom
        = ontology.annotationAssertionAxioms(iri).iterator();
    while (annotationAssertionAxiom.hasNext()) {
      OWLAnnotationAssertionAxiom next = annotationAssertionAxiom.next();
      String property = rendering.render(next.getProperty());
      String value = next.getValue().toString();
      //LOGGER.debug("Subject {}", next.getSubject());
      LOGGER.debug("Find annotation, value: \"{}\", property: \"{}\" ", value, property);

      OwlAnnotationPropertyValue opv = new OwlAnnotationPropertyValue();

      opv.setType(dataExtractor.extractAnnotationType(next));
      if (opv.getType().equals(WeaselOwlType.ANY_URI)) {
        opv.setValue(dataExtractor.extractAnyUriToString(value));
      } else {
        opv.setValue(value);
      }
      result.addProperty(property, opv);
    }
    return result;
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLNamedIndividual obj,
      OWLOntology ontology) {

    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator);
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLObjectProperty obj,
      OWLOntology ontology) {

    Iterator<OWLObjectPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator);
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLDataProperty obj,
      OWLOntology ontology) {

    Iterator<OWLDataPropertyAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator);
  }

  private OwlDetailsProperties<PropertyValue> handleAxioms(
      OWLClass obj,
      OWLOntology ontology) {

    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    return handleAxioms(axiomsIterator);
  }

  private OwlTaxonomyImpl extractTaxonomy(List<PropertyValue> subclasses, IRI objIri, OWLOntology ontology) {
    OwlTaxonomyImpl taxonomy = new OwlTaxonomyImpl();
    if (subclasses.size() > 0) {

      for (PropertyValue property : subclasses) {
        if (property.getType().equals(WeaselOwlType.TAXONOMY)) {
          OwlAxiomPropertyValue axiomProperty = (OwlAxiomPropertyValue) property;
          //taxProp.g
          IRI sci = extractSubclassIri(axiomProperty.getEntityMaping(), objIri);
          OWLEntity clazz = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(sci);
          LOGGER.debug(">> {} subClassOf {}", objIri.getShortForm(), clazz.getIRI().getShortForm());
          List<PropertyValue> subclassesTax = getSubclasses(clazz, ontology);

          OwlTaxonomyImpl subCLassTax = extractTaxonomy(subclassesTax, clazz.getIRI(), ontology);

          OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl();
          OwlTaxonomyValue val1 = new OwlTaxonomyValue();
          val1.setType(WeaselOwlType.STRING);
          val1.setValue(objIri.getFragment());//<---!
          OwlTaxonomyValue val2 = new OwlTaxonomyValue();
          val2.setType(WeaselOwlType.IRI);
          val2.setValue(objIri.getIRIString());//<---!
          taxEl.setValueA(val1);
          taxEl.setValueB(val2);

          if (subCLassTax.getValue().size() > 0) {
            taxonomy.addTaxonomy(subCLassTax, taxEl);
          } else {
            List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();
            currentTax.add(taxEl);
            taxonomy.addTaxonomy(currentTax);
          }

        }
      }

    } else {

      LOGGER.info("taxonomy leaf on {}", objIri.getFragment());
      OwlTaxonomyElementImpl taxEl = new OwlTaxonomyElementImpl();
      OwlTaxonomyValue val1 = new OwlTaxonomyValue();
      val1.setType(WeaselOwlType.STRING);
      val1.setValue(objIri.getFragment());//<---!
      OwlTaxonomyValue val2 = new OwlTaxonomyValue();
      val2.setType(WeaselOwlType.IRI);
      val2.setValue(objIri.getIRIString());//<---!
      taxEl.setValueA(val1);
      taxEl.setValueB(val2);
      List<OwlTaxonomyElementImpl> currentTax = new LinkedList<>();
      currentTax.add(taxEl);
      taxonomy.addTaxonomy(currentTax);
    }

    return taxonomy;
  }

  private < T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
      Iterator<T> axiomsIterator) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();
      String value = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      opv.setValue(value);

      opv.setType(WeaselOwlType.AXIOM);
      LOGGER.debug("Find Axiom \"{}\" with type \"{}\"", value, key);
      Boolean isRestriction = isRestriction(axiom);

      if (!isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        LOGGER.debug("Find non restriction SubClassOf");
        opv.setType(WeaselOwlType.TAXONOMY);
      }

      Iterator<OWLEntity> iterator = axiom.signature().iterator();
      while (iterator.hasNext()) {
        OWLEntity next = iterator.next();
        String eSignature = rendering.render(next);
        String eIri = next.getIRI().toString();
        opv.addEntityValues(eSignature, eIri);
      }
      result.addProperty(key, opv);
    }
    return result;
  }

  private static <T extends OWLAxiom> Boolean isRestriction(T axiom) {
    Boolean isRestriction = axiom.accept(WeaselOntologyVisitors.isRestrictionVisitor);
    if (isRestriction == null) {
      isRestriction = Boolean.FALSE;
    }
    return isRestriction;
  }

  public OwlDetails handleParticularDataProperty(IRI iri, OWLOntology ontology) {
    OwlDetails resultDetails = new OwlDetails();
    Iterator<OWLDataProperty> dataPropertyIt = ontology.dataPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLDataProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
        LOGGER.debug("Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(dataProperty.getIRI().getShortForm());

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    resultDetails.sortProperties(new LinkedList<>());
    //wd.sortProperties(prioritySortList);
    return resultDetails;

  }

  public OwlDetails handleParticularObjectProperty(IRI iri, OWLOntology ontology) {
    OwlDetails resultDetails = new OwlDetails();
    Iterator<OWLObjectProperty> dataPropertyIt = ontology.objectPropertiesInSignature().iterator();

    while (dataPropertyIt.hasNext()) {
      OWLObjectProperty dataProperty = dataPropertyIt.next();

      if (dataProperty.getIRI().equals(iri)) {
        LOGGER.debug("Find owl named individual wih iri: {}", iri.toString());

        resultDetails.setLabel(dataProperty.getIRI().getShortForm());

        OwlDetailsProperties<PropertyValue> axioms = handleAxioms(dataProperty, ontology);

        OwlDetailsProperties<PropertyValue> annotations
            = handleAnnotations(dataProperty.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    resultDetails.sortProperties(new LinkedList<>());
    return resultDetails;
  }

  private IRI extractSubclassIri(Map<String, String> entityMaping, IRI objIri) {

    for (Map.Entry<String, String> entry : entityMaping.entrySet()) {
      if (!entry.getValue().equals(objIri.getIRIString())) {
        return IRI.create(entry.getValue());
      }
    }

    return null;
  }

  private List<PropertyValue> getSubclasses(OWLEntity clazz, OWLOntology ontology) {
    List<PropertyValue> result = new LinkedList<>();

    //make entriess mapping
    for (final OWLSubClassOfAxiom subClasse : ontology.axioms(AxiomType.SUBCLASS_OF).collect(Collectors.toList())) {
      if (subClasse.getSuperClass() instanceof OWLClass && subClasse.getSubClass() instanceof OWLClass) {
        OWLClass superClazz = (OWLClass) subClasse.getSuperClass();
        OWLClass subClazz = (OWLClass) subClasse.getSubClass();
        if (subClazz.getIRI().equals(clazz.getIRI())) {
          IRI subClazzIri = subClazz.getIRI();
          IRI superClazzIri = superClazz.getIRI();
          //LOGGER.debug("\t> {} extends {}", subClazzIri.getFragment(), superClazzIri.getFragment());

          OwlAxiomPropertyValue pv = new OwlAxiomPropertyValue();
          pv.setType(WeaselOwlType.TAXONOMY);
          pv.addEntityValues(subClazzIri.getFragment(), superClazzIri.getIRIString());
          pv.setValue(rendering.render(subClasse));
          result.add(pv);

        }

      }
    }
    return result;
  }

}
