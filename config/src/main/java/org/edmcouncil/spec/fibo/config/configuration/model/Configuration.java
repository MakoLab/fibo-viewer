package org.edmcouncil.spec.fibo.config.configuration.model;

import java.util.Map;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public interface Configuration<T> {

  Map<String, T> getConfiguration();
  
  T getConfigVal(String cfName);
}
