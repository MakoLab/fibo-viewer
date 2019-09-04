package org.edmcouncil.spec.fibo.weasel.model.property;

import org.edmcouncil.spec.fibo.config.configuration.model.PairImpl;
import org.edmcouncil.spec.fibo.weasel.model.PropertyValue;
import org.edmcouncil.spec.fibo.weasel.model.WeaselOwlType;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class OwlFiboModuleProperty implements PropertyValue<PairImpl<String, String>> {

  private WeaselOwlType type;
  private PairImpl<String, String> value;

  @Override
  public WeaselOwlType getType() {
    return this.type;
  }

  @Override
  public void setType(WeaselOwlType type) {
    this.type = type;
  }

  @Override
  public PairImpl<String, String> getValue() {
    return this.value;
  }

  @Override
  public void setValue(PairImpl<String, String> value) {
    this.value = value;
  }

}
