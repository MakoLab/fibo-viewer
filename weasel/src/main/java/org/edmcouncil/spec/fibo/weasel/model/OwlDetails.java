package org.edmcouncil.spec.fibo.weasel.model;

import org.edmcouncil.spec.fibo.weasel.model.impl.OwlAnnotationPropertyValue;
import java.util.List;
import java.util.Map;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlDetails {

  private String label;
  private OwlDetailsProperties<OwlAnnotationPropertyValue> properties;
  private String type;

  public OwlDetails() {
    if (properties == null) {
      properties = new OwlDetailsProperties<>();
    }
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Map<String, List<OwlAnnotationPropertyValue>> getProperties() {
    return properties.getProperties();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void addProperty(String key, OwlAnnotationPropertyValue property) {
    properties.addProperty(key, property);
  }

  public void sortProperties(List<String> priorityList) {
    properties.sort(priorityList);
  }

  public void addAllProperties(OwlDetailsProperties<OwlAnnotationPropertyValue> axioms) {
    axioms.getProperties().entrySet().forEach((entry) -> {
      entry.getValue().forEach((propertyValue) -> {
        properties.addProperty(entry.getKey(), propertyValue);
      });
    });
  }

}
