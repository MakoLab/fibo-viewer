package org.edmcouncil.spec.fibo.config.utils.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Create by Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class FileSystemManager {

  private static final String WEASEL_HOME_DIR_NAME = ".weasel";
  private static final String WEASEL_ONTOLOGY_FILE_NAME = "fibo.ttl";

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemManager.class);

  public Path getWeaselHomeDir() {
    Path userHomeDir;
    String userHomeProperty = System.getProperty("user.home");
    userHomeDir = Paths.get(userHomeProperty);
    LOGGER.trace("User home dir is '{}'.", userHomeDir);
    return userHomeDir.resolve(WEASEL_HOME_DIR_NAME);
  }

  private Path createDirIfNotExists(Path dirToCreate) throws IOException {
    if (Files.notExists(dirToCreate)) {
      createDir(dirToCreate);
    }
    return dirToCreate;
  }

  public void createDir(Path dirPath) throws IOException {
    try {
      Files.createDirectory(dirPath);
    } catch (IOException ex) {
      String msg = String.format("Unable to create a dir '%s'.", dirPath);
      throw new IOException(msg, ex);
    }
  }

  public Path getPathToOntologyFile() throws IOException {
    Path homeDir = getWeaselHomeDir();
    return createDirIfNotExists(homeDir).resolve(WEASEL_ONTOLOGY_FILE_NAME);
  }
}