package org.edmcouncil.spec.fibo.config.configuration.model;

import java.util.List;
import javax.annotation.PostConstruct;
import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class AppConfiguration {
  
  @Autowired
  private FileSystemManager fileSystemManager;
  
  private Configuration<List<String>> weaselConfig;
  //other configuration if nedded
  
  @PostConstruct
  public void init(){
    
  }
  
}
