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

        OwlDetailsProperties<OwlAnnotationPropertyValue> axioms = handleAxioms(clazz, ontology);
        OwlDetailsProperties<OwlAnnotationPropertyValue> annotations
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

        OwlDetailsProperties<OwlAnnotationPropertyValue> axioms = handleAxioms(individual, ontology);

        OwlDetailsProperties<OwlAnnotationPropertyValue> annotations
            = handleAnnotations(individual.getIRI(), ontology);

        resultDetails.addAllProperties(axioms);
        resultDetails.addAllProperties(annotations);
      }
    }
    resultDetails.sortProperties(new LinkedList<>());
    //wd.sortProperties(prioritySortList);
    return resultDetails;
  }

  private OwlDetailsProperties<OwlAnnotationPropertyValue> handleAnnotations(IRI iri, OWLOntology ontology) {
    OwlDetailsProperties<OwlAnnotationPropertyValue> result = new OwlDetailsProperties<>();

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

  private OwlDetailsProperties<OwlAnnotationPropertyValue> handleAxioms(
      OWLNamedIndividual obj,
      OWLOntology ontology) {
    OwlDetailsProperties<OwlAnnotationPropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    while (axiomsIterator.hasNext()) {
      OWLIndividualAxiom axiom = axiomsIterator.next();
      String value = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      OwlAnnotationPropertyValue opv = new OwlAnnotationPropertyValue();
      opv.setValue(value);
      //TODO: Use it correctly
      opv.setType(PropertyType.STRING);
      result.addProperty(key, opv);
      LOGGER.debug("Fing axiom \"{}\" with type \"{}\"", value, key);
    }
    return result;
  }

  private OwlDetailsProperties<OwlAnnotationPropertyValue> handleAxioms(
      OWLClass obj,
      OWLOntology ontology) {
    OwlDetailsProperties<OwlAnnotationPropertyValue> result = new OwlDetailsProperties<>();

    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    while (axiomsIterator.hasNext()) {
      OWLClassAxiom axiom = axiomsIterator.next();
      String value = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      OwlAnnotationPropertyValue opv = new OwlAnnotationPropertyValue();
      opv.setValue(value);
      //TODO: Use it correctly
      opv.setType(PropertyType.STRING);
      result.addProperty(key, opv);
      LOGGER.debug("Fing axiom \"{}\" with type \"{}\"", value, key);
    }
    return result;
  }

}
