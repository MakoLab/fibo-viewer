package org.edmcouncil.spec.fibo.weasel.ontology;

import java.io.File;
import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.edmcouncil.spec.fibo.weasel.model.OwlDetails;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
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
import uk.ac.manchester.cs.owl.owlapi.OWLImportsDeclarationImpl;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 * @author Patrycja Miazek (patrycja.miazek@makolab.com)
 */
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
  public void init() throws IOException {
    try {
      loadOntologyFromFile();
    } catch (OWLOntologyCreationException ex) {
      LOGGER.error("[ERROR]: Error when creating ontology.");
    }
  }

  @PreDestroy
  public void destroy() {

  }

  private void loadOntologyFromFile() throws IOException, OWLOntologyCreationException {
   
    FileSystemManager fsm = new FileSystemManager();
    File inputOntologyFile = fsm.getPathToOntologyFile().toFile();

    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology o = m.loadOntologyFromOntologyDocument(inputOntologyFile);

    IRI fiboIRI = IRI.create("https://spec.edmcouncil.org/fibo/ontologyAboutF/IBOProd/");

    m.makeLoadImportRequest(new OWLImportsDeclarationImpl(m.getOntologyDocumentIRI(o)));
    Stream<OWLOntology> directImports = m.imports(o);
    o = m.createOntology(fiboIRI, directImports, false);
    ontology = o;

  }

  private Set<OWLOntology> openOntologiesFromDirectory(File ontologiesDir, OWLOntologyManager manager) throws OWLOntologyCreationException {
    Set<OWLOntology> result = new HashSet<>();
    for (File file : ontologiesDir.listFiles()) {
      LOGGER.debug("isFile : {}, name: {}", file.isFile(), file.getName());
      if (file.isFile()) {
        if (getFileExtension(file).equalsIgnoreCase("rdf") && !file.getName().contains("Metadata")) {

          OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(file);
          result.add(newOntology);
        }
      } else if (file.isDirectory()) {
        Set<OWLOntology> tmp = openOntologiesFromDirectory(file, manager);
        result.addAll(tmp);
      }

    }
    return result;
  }

  private static String getFileExtension(File file) {
    String fileName = file.getName();
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }

  public OWLOntology getOntology() {
    return ontology;
  }

  public Collection getDetailsByIri(String iriString) {
    IRI iri = IRI.create(iriString);
    // TODO: change result type from list to single OwlDetails element.
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
    
    for (OwlDetails owlDetails : result) {
      owlDetails.setIri(iriString);
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
      groupedDetails.setTaxonomy(owlDetails.getTaxonomy());
      groupedDetails.setLabel(owlDetails.getLabel());
      groupedDetails.setIri(owlDetails.getIri());
      groupedDetails.sortProperties(groups);

      newResult.add(groupedDetails);
    }
    return newResult;
  }

  private String getGroupName(Set<ConfigElement> groups, String propertyKey) {
    String result = null;
    for (ConfigElement g : groups) {
      ConfigGroupsElement group = (ConfigGroupsElement) g;
      if (group.getElements() != null && group.getElements().size() > 0) {
        if (group.contains(propertyKey)) {
          return group.getName();
        }
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
