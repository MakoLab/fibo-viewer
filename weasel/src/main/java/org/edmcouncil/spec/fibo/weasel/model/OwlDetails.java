package org.edmcouncil.spec.fibo.weasel.model;

import java.util.List;
import java.util.Map;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlDetails {

  private String label;
  private OwlDetailsProperties properties;
  private String type;

  public OwlDetails() {
  if (properties == null) {
      properties = new OwlDetailsProperties();
    }
  }
  
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Map<String, List<String>> getProperties() {
    return properties.getProperties();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void addProperty(String key, String property) {
    properties.addProperty(key, property);
  }

  public void sortProperties(List<String> priorityList) {
    properties.sort(priorityList);
  }

}
