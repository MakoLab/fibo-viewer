package org.edmcouncil.spec.fibo.config.configuration.model.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigElementAbstract;
import org.edmcouncil.spec.fibo.config.configuration.model.ConfigElementType;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class ConfigGroupsElement extends ConfigElementAbstract {

  private String name;
  private Set<ConfigStringElement> elements;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<ConfigStringElement> getElements() {
    return elements;
  }

  public void setElements(Set<ConfigStringElement> elements) {
    this.elements = elements;
  }
  
  public ConfigGroupsElement() {
  }

  public ConfigGroupsElement(ConfigElementType type) {
    super(type);
  }
  
  public void addElement(ConfigStringElement el){
    if(elements == null) {
      elements = new LinkedHashSet<>();
    }
    elements.add(el);
  }
  
  public boolean contains(String val){
    return elements.stream()
        .map(ConfigStringElement::toString)
        .filter(val::equals)
        .findFirst()
        .isPresent();
  }

  @Override
  public String toString() {
    return "ConfigGroups{" + "name:" + name + ", elements:" + elements + '}';
  }
  
}
