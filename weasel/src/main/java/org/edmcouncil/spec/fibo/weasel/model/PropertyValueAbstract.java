package org.edmcouncil.spec.fibo.weasel.model;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public abstract class PropertyValueAbstract<T> implements PropertyValue<T> {

  private PropertyType type;
  private T value;

  @Override
  public PropertyType getType() {
    return this.type;
  }

  @Override
  public void setType(PropertyType type) {
    this.type = type;
  }

  @Override
  public T getValue() {
    return this.value;
  }

  @Override
  public void setValue(T value) {
    this.value = value;
  }
}
