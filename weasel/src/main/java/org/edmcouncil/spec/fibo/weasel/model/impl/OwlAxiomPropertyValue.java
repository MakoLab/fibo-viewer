package org.edmcouncil.spec.fibo.weasel.model.impl;

import java.util.HashMap;
import java.util.Map;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValueAbstract;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlAxiomPropertyValue extends PropertyValueAbstract<String> {

  private final Map<String, String> entityMaping;

  public OwlAxiomPropertyValue() {
    super();
    entityMaping = new HashMap<>();
  }

  public void addEntityValues(String key, String valIri) {
    entityMaping.put(key, valIri);
  }

  public Map<String, String> getEntityMaping() {
    return this.entityMaping;
  }

}
