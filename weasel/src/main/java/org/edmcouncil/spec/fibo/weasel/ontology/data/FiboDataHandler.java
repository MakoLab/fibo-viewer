package org.edmcouncil.spec.fibo.weasel.ontology.data;

import java.util.Iterator;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This data handler working with fibo ontology.
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class FiboDataHandler {

  private static final String DOMAIN_POSTFIX = "Domain";
  private static final String MODULE_POSTFIX = "Module";

  private static final Logger LOGGER = LoggerFactory.getLogger(FiboDataHandler.class);

  static OwlDetailsProperties<PropertyValue> handleFiboModulesData(OWLOntology ontology, OWLEntity entity) {

    OWLDataFactory df = OWLManager.getOWLDataFactory();
    // ontology, df.getRDFSIsDefinedBy())) 

    Iterator<OWLAnnotation> iterator = EntitySearcher
        .getAnnotations(entity, ontology, df.getRDFSIsDefinedBy())
        .iterator();

    while (iterator.hasNext()) {
      OWLAnnotation annotation = iterator.next();

      String isDefinedBy = annotation.annotationValue().toString();

      String[] splitedStr = isDefinedBy.split("/");
      int length = splitedStr.length;
      String domain = splitedStr[length - 3];
      //domain = domain.concat(DOMAIN_POSTFIX);
      String module = splitedStr[length - 2];
      //module = module.concat(MODULE_POSTFIX);
      String onto = splitedStr[length - 1];
      
      StringBuilder sb = new StringBuilder();
      
      for (String fragment : splitedStr) {
        if(fragment.equals("http:") || fragment.equals("https:")){
          sb.append(fragment).append("//");
        }else {
          sb.append(fragment).append("/");
          if(fragment.equals("ontology")){
            break;
          }         
          
        }
      }
     

      LOGGER.debug("[Fibo Data Handler] domain: {}; module: {}; ontology: {}, array: {} ", domain, module, onto, String.join(" ", splitedStr));

    }

    return null;
  }

}
