package org.edmcouncil.spec.fibo.weasel.model;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public interface PropertyValue<T> {

  PropertyType getType();

  void setType(PropertyType type);

  T getValue();

  void setValue(T value);
}
