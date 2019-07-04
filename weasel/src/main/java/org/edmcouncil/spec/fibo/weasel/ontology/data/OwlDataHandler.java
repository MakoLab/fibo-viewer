package org.edmcouncil.spec.fibo.weasel.ontology.data;

import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.impl.OwlAnnotationPropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.PropertyType;
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
import java.util.stream.Collectors;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.impl.OwlAxiomPropertyValue;
import org.edmcouncil.spec.fibo.weasel.ontology.visitor.WeaselOntologyVisitors;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
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
      if (opv.getType().equals(PropertyType.ANY_URI)) {
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

  private < T extends OWLAxiom> OwlDetailsProperties<PropertyValue> handleAxioms(
      Iterator<T> axiomsIterator) {
    OwlDetailsProperties<PropertyValue> result = new OwlDetailsProperties<>();

    while (axiomsIterator.hasNext()) {
      T axiom = axiomsIterator.next();
      String value = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      OwlAxiomPropertyValue opv = new OwlAxiomPropertyValue();
      opv.setValue(value);
      //TODO: Use it correctly
      opv.setType(PropertyType.AXIOM);
      result.addProperty(key, opv);
      LOGGER.debug("Find Axiom \"{}\" with type \"{}\"", value, key);
      Boolean isRestriction = isRestriction(axiom);

      if (!isRestriction && axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
        LOGGER.debug("Find non restriction SubClassOf");
      }

      Iterator<OWLEntity> iterator = axiom.signature().iterator();
      while (iterator.hasNext()) {
        OWLEntity next = iterator.next();
        String eSignature = rendering.render(next);
        String eIri = next.getIRI().toString();
        opv.addEntityValues(eSignature, eIri);
      }
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

}
