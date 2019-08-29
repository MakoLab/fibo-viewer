package org.edmcouncil.spec.fibo.config.configuration.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.edmcouncil.spec.fibo.config.configuration.loader.saxparser.WeaselConfigurationHandler;
import org.edmcouncil.spec.fibo.config.configuration.model.Configuration;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.WeaselConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
public class ConfigLoader {
  
  private static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
  

  public Configuration loadWeaselConfiguration(Path weaselConfigFile) {
    Configuration configuration = new WeaselConfiguration();
    
    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    try {
        File configFile= weaselConfigFile.toFile();
        if(!configFile.exists()){
          logger.debug("Configuration file not exist, use default empty configuration.");
          return configuration;
        }
        SAXParser saxParser = saxParserFactory.newSAXParser();
        WeaselConfigurationHandler handler = new WeaselConfigurationHandler();
        saxParser.parse(configFile, handler);
        
        configuration = handler.getConfiguration();
       
        
    } catch (ParserConfigurationException | SAXException | IOException e) {
        logger.error("Exception while loading configuration: {}", e.getMessage());
    }
      

    return configuration;
  }
}
