package org.edmcouncil.spec.fibo.weasel.ontology;

import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.edmcouncil.spec.fibo.weasel.ontology.data.OwlDataHandler;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  
  @Autowired
  private OwlDataHandler dataHandler;

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

  public List<OwlDetails> getDetailsByIri(String iriString) {
    IRI iri = IRI.create(iriString);
    List<OwlDetails> result = new LinkedList<>();

    if (ontology.containsClassInSignature(iri)) {
      LOGGER.debug("Handle class data.");
      OwlDetails wd = dataHandler.handleParticularClass(iri, ontology);
      result.add(wd);
    }
    if (ontology.containsDataPropertyInSignature(iri)) {
      LOGGER.info("Handle data property.");
      OwlDetails wd = dataHandler.handleParticularDataProperty(iri, ontology);;
      result.add(wd);
    }
    if (ontology.containsObjectPropertyInSignature(iri)) {
      LOGGER.info("Handle object property.");
      OwlDetails wd = dataHandler.handleParticularObjectProperty(iri, ontology);;
      result.add(wd);
    }
    if (ontology.containsIndividualInSignature(iri)) {
      LOGGER.info("Handle individual data.");
      OwlDetails wd = dataHandler.handleParticularIndividual(iri, ontology);
      result.add(wd);
    }
    return result;
  }

  
}
