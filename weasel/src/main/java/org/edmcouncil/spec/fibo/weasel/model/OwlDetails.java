package org.edmcouncil.spec.fibo.weasel.model;

import org.edmcouncil.spec.fibo.weasel.model.property.OwlDetailsProperties;
import org.edmcouncil.spec.fibo.weasel.model.property.OwlAnnotationPropertyValue;
import java.util.List;
import java.util.Map;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyElementImpl;
import org.edmcouncil.spec.fibo.weasel.model.taxonomy.OwlTaxonomyImpl;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlDetails {

  private String label;
  private OwlDetailsProperties<PropertyValue> properties;
  private String type;
  private OwlTaxonomyImpl taxonomy;

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

  public Map<String, List<PropertyValue>> getProperties() {
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

  public void setTaxonomy(OwlTaxonomyImpl tax) {
    this.taxonomy = tax;
  }

  public OwlTaxonomy getTaxonomy() {
    return this.taxonomy;
  }

  public void sortProperties(List<String> priorityList) {
    properties.sort(priorityList);
  }

  public void addAllProperties(OwlDetailsProperties<PropertyValue> axioms) {
    axioms.getProperties().entrySet().forEach((entry) -> {
      entry.getValue().forEach((propertyValue) -> {
        properties.addProperty(entry.getKey(), propertyValue);
      });
    });
  }

}
