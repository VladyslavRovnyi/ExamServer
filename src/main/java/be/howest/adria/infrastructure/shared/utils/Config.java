package be.howest.adria.infrastructure.shared.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;

public class Config {
  private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
  private final Properties properties = new Properties();

  public Config (String path) {
    String externalPropertiesFile = System.getProperty("configFile");
    if (externalPropertiesFile != null) {
      LOGGER.log(Level.INFO, "Using external properties file: {0}", externalPropertiesFile);
      path = externalPropertiesFile;
      loadExternalProperties(path);
    } else 
      loadInternalProperties(path);
  }

  public String readSetting(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public String readSetting(String key) {
    return readSetting(key,"");
  }

  public Map<String, String> readSettingsStartingWith(String prefix) {
    Map<String, String> settings = new HashMap<>();
    properties.stringPropertyNames().stream()
        .filter(key -> key.startsWith(prefix))
        .forEach(key -> settings.put(key, readSetting(key)));
    return settings;
  }

  private void loadInternalProperties(String path) {
    try (InputStream is = getClass().getResourceAsStream(path)) {
      properties.load(is);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Unable to read config from properties file.", ex);
      throw new IllegalStateException("Unable to load application configuration.");
    }
  }

  private void loadExternalProperties(String path) {
    try (FileInputStream fis = new FileInputStream(path)) {
      properties.load(fis);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Unable to read config from properties file.", ex);
      throw new IllegalStateException("Unable to load application configuration.");
    }
  }
}
