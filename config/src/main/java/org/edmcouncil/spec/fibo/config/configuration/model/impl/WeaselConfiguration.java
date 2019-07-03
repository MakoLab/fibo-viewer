package org.edmcouncil.spec.fibo.config.configuration.model.impl;

import java.util.List;
import java.util.Map;
import org.edmcouncil.spec.fibo.config.configuration.model.Configuration;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
public class WeaselConfiguration implements Configuration<List<String>>{

  private Map<String, List<String>> configuration;
  
  @Override
  public Map<String, List<String>> getConfiguration() {
    return this.configuration;
  }

  @Override
  public List<String> getConfigVal(String cfName) {
    return this.configuration.get(cfName);
  }

}
