package org.edmcouncil.spec.fibo.weasel.ontology;

import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WeaselOntologyManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(WeaselOntologyManager.class);
  private OWLOntology ontology;
  //TODO: move load this elements to config module.
  private Map<IRI, String> properties;
  private Set<IRI> ignoredProperties;
  private List<String> prioritySortList;
  //--

  @PostConstruct
  public void init() {
    loadOntologyFromFile();

    //ignored properties
    ignoredProperties = new HashSet<>();

    //propeties required to find
    properties = new HashMap<>();

    //sort priority order
    prioritySortList = new LinkedList<>();
  }

  @PreDestroy
  public void destroy() {
    //
  }

  private void loadOntologyFromFile() {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    FileSystemManager fsm = new FileSystemManager();
    try {
      ontology = manager.loadOntologyFromOntologyDocument(fsm.getPathToOntologyFile().toFile());
    } catch (IOException | OWLOntologyCreationException ex) {
      LOGGER.error(ex.getMessage());
    }
  }

  public OWLOntology getOntology() {
    return ontology;
  }

  public OwlDetails getDetailsByIri(String iriString) {
    OwlDetails wd = null;
    IRI iri = IRI.create(iriString);

    if (ontology.containsClassInSignature(iri)) {
      LOGGER.info("Handle class data.");
      wd = handleDataForOwlClass(iri);
    }
    if (ontology.containsDataPropertyInSignature(iri)) {
      LOGGER.info("Handle data property.");
      //wd = handleDataForOwlClass(iri);
    }
    if (ontology.containsIndividualInSignature(iri)) {
      LOGGER.info("Handle individual data.");
      wd = handleDataForIndividual(iri);
    }
    return wd;
  }

  private OwlDetails handleDataForOwlClass(IRI iri) {
    OwlDetails wd = new OwlDetails();
    Iterator<OWLClass> classesIterator = ontology.classesInSignature().iterator();

    while (classesIterator.hasNext()) {
      OWLClass clazz = classesIterator.next();

      if (clazz.getIRI().equals(iri)) {
        OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

        wd.setLabel(clazz.getIRI().getShortForm());

        handleAxioms(clazz, rendering, wd);

        handleAnnotations(clazz.getIRI(), wd, rendering);
      }
    }
    wd.sortProperties(prioritySortList);
    return wd;
  }

  private OwlDetails handleDataForIndividual(IRI iri) {
    OwlDetails wd = new OwlDetails();
    Iterator<OWLNamedIndividual> individualIterator = ontology.individualsInSignature().iterator();

    while (individualIterator.hasNext()) {
      OWLNamedIndividual individual = individualIterator.next();

      if (individual.getIRI().equals(iri)) {
        OWLObjectRenderer rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

        wd.setLabel(individual.getIRI().getShortForm());

        handleAxioms(individual, rendering, wd);

        handleAnnotations(individual.getIRI(), wd, rendering);
      }
    }
    wd.sortProperties(prioritySortList);
    return wd;
  }

  private void handleAnnotations(IRI iri, OwlDetails wd, OWLObjectRenderer rendering) {
    Iterator<OWLAnnotationAssertionAxiom> annotationAssertionAxiom
        = ontology.annotationAssertionAxioms(iri).iterator();
    while (annotationAssertionAxiom.hasNext()) {
      OWLAnnotationAssertionAxiom next = annotationAssertionAxiom.next();
      //LOGGER.info("**s "+next.getSubject());
      //LOGGER.info("**p "+rendering.render(next.getProperty()));
      //LOGGER.info("**v "+next.getValue());
      wd.addProperty(rendering.render(next.getProperty()), next.getValue().toString());
    }
  }

  private void handleAxioms(OWLNamedIndividual obj, OWLObjectRenderer rendering, OwlDetails wd) {
    Iterator<OWLIndividualAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    while (axiomsIterator.hasNext()) {
      OWLIndividualAxiom axiom = axiomsIterator.next();
      String val = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      wd.addProperty(key, val);
    }
  }

  private void handleAxioms(OWLClass obj, OWLObjectRenderer rendering, OwlDetails wd) {
    Iterator<OWLClassAxiom> axiomsIterator = ontology.axioms(obj).iterator();
    while (axiomsIterator.hasNext()) {
      OWLClassAxiom axiom = axiomsIterator.next();
      String val = rendering.render(axiom);
      String key = axiom.getAxiomType().getName();
      wd.addProperty(key, val);
    }
  }

  //TODO: 
  private void handleClassesInSignature(OWLObject individual, OwlDetails wd, OWLObjectRenderer rendering) {
    Iterator<OWLClass> classesInSignatureterator = individual.classesInSignature().iterator();
    while (classesInSignatureterator.hasNext()) {
      OWLClass next = classesInSignatureterator.next();
      //next.get
      //wd.setLabel(rendering.render(next));
    }
  }
}
