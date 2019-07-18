package org.edmcouncil.spec.fibo.weasel.ontology;

import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.edmcouncil.spec.fibo.config.configuration.model.AppConfiguration;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigElement;
import org.edmcouncil.spec.fibo.config.configuration.model.WeaselConfigKeys;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ConfigGroupsElement;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.WeaselConfiguration;
import org.edmcouncil.spec.fibo.weasel.model.OwlGroupedDetails;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
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
  private static final String DEFAULT_GROUP_NAME = "other";
  private OWLOntology ontology;

  @Autowired
  private OwlDataHandler dataHandler;
  @Autowired
  private AppConfiguration config;

  @PostConstruct
  public void init() {
    loadOntologyFromFile();
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

  public Collection getDetailsByIri(String iriString) {
    IRI iri = IRI.create(iriString);
    List<OwlDetails> result = new LinkedList<>();

    if (ontology.containsClassInSignature(iri)) {
      LOGGER.debug("Handle class data.");
      OwlDetails wd = dataHandler.handleParticularClass(iri, ontology);
      if (!result.contains(wd)) {
        result.add(wd);
      }
    }
    if (ontology.containsDataPropertyInSignature(iri)) {
      LOGGER.info("Handle data property.");
      OwlDetails wd = dataHandler.handleParticularDataProperty(iri, ontology);
      if (!result.contains(wd)) {
        result.add(wd);
      }
    }
    if (ontology.containsObjectPropertyInSignature(iri)) {
      LOGGER.info("Handle object property.");
      OwlDetails wd = dataHandler.handleParticularObjectProperty(iri, ontology);
      if (!result.contains(wd)) {
        result.add(wd);
      }
    }
    if (ontology.containsIndividualInSignature(iri)) {
      LOGGER.info("Handle individual data.");
      OwlDetails wd = dataHandler.handleParticularIndividual(iri, ontology);
      if (!result.contains(wd)) {
        result.add(wd);
      }
    }
    if (!config.getWeaselConfig().isEmpty()) {
      WeaselConfiguration cfg = (WeaselConfiguration) config.getWeaselConfig();
      if (cfg.isGrouped()) {
        List<OwlGroupedDetails> newResult = groupDetails(result, cfg);
        return newResult;
      } else {
        sortResults(result);
      }
    }
    return result;
  }

  private List<OwlGroupedDetails> groupDetails(List<OwlDetails> result, WeaselConfiguration cfg) {
    List<OwlGroupedDetails> newResult = new LinkedList<>();
    for (OwlDetails owlDetails : result) {
      OwlGroupedDetails groupedDetails = new OwlGroupedDetails();
      Set<ConfigElement> groups = cfg.getConfiguration().get(WeaselConfigKeys.GROUPS);
      
      for (Map.Entry<String, List<PropertyValue>> entry : owlDetails.getProperties().entrySet()) {
        String propertyKey = entry.getKey();
        String groupName = null;

        groupName = getGroupName(groups, propertyKey);

        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        for (PropertyValue property : entry.getValue()) {
          groupedDetails.addProperty(groupName, propertyKey, property);
        }
      }
 

      //groupedDetails.
      groupedDetails.setTaxonomy(owlDetails.getTaxonomy());
      groupedDetails.setLabel(owlDetails.getLabel());
      groupedDetails.sortProperties(groups);
      newResult.add(groupedDetails);
    }
    return newResult;
  }

  private String getGroupName(Set<ConfigElement> groups, String propertyKey) {
    String result = null;
    for (ConfigElement g : groups) {
      ConfigGroupsElement group = (ConfigGroupsElement) g;
      if (group.contains(propertyKey)) {
        return group.getName();
      }
    }
    return result;
  }

  private void sortResults(List<OwlDetails> result) {
    Set set = (Set) config.getWeaselConfig()
        .getConfigVal(WeaselConfigKeys.PRIORITY_LIST);
    if (set == null) {
      return;
    }
    List prioritySortList = new LinkedList();
    result.forEach((owlDetails) -> {
      owlDetails.sortProperties(prioritySortList);
    });
  }

}
