package org.rapidpm.dependencies.core.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.rapidpm.dependencies.core.logger.HasLogger;

/**
 * Creates a {@link Properties} object merged from different sources.
 */
public class PropertiesResolver implements HasLogger {
  public static final String CONFIG_LOCATION_PROPERTY = "rapidpm.configlocation";
  private static final String PROPERIES_EXTENSION = ".properties";

  private String createFileName(String name) {
    return name + PROPERIES_EXTENSION;
  }

  /**
   * Creates a {@link Properties} object from different sources. The sources are:
   * 
   * <ol>
   * <li>the root of the classpath</li>
   * <li>the current working directory</li>
   * <li>the home directory of the current user</li>
   * <li>a directory specified by the system property <code>rapidm.configlocation</code></li>
   * <ol>
   * <br>
   * Properties defined in the higher sources override the ones defined in lower sources.
   * 
   * @param name the name of the properties file - without the <code>.properties</code> file
   *        extension
   * @return the merged {@link Properties} object
   */
  public Properties get(String name) {
    Properties propertiesFromResource = loadFromResource(name);
    Properties propertiesFromWorkingDir = loadFromWorkingDir(name);
    Properties propertiesFromHomeDir = loadFromHomeDir(name);
    Properties propertiesFromEnviromentSource = loadFromEnviromentSrouce(name);
    return merge(propertiesFromResource, propertiesFromWorkingDir, propertiesFromHomeDir,
        propertiesFromEnviromentSource);
  }

  private Properties loadFromEnviromentSrouce(String name) {
    String configSource = System.getProperty(CONFIG_LOCATION_PROPERTY);
    Properties properties;
    if (configSource != null) {
      String fileName = createFileName(name);
      File file = new File(configSource, fileName);
      properties = loadFromFile(file);
    } else {
      properties = new Properties();
    }
    return properties;
  }

  private Properties loadFromFile(File file) {
    Properties properties = new Properties();
    try (InputStream in = new FileInputStream(file)) {
      logger().info("Load properties from file: " + file);
      properties.load(in);
    } catch (FileNotFoundException e) {
      logger().fine("No properties file " + file + " found.");
    } catch (IOException e) {
      logger().severe("Failure loading properties from file: " + file, e);
    }

    return properties;
  }

  private Properties loadFromHomeDir(String name) {

    String fileName = createFileName(name);
    String homeDir = System.getProperty("user.home");
    File file = new File(homeDir, fileName);
    return loadFromFile(file);
  }

  private Properties loadFromResource(String name) {
    Properties properties = new Properties();

    String resourceName = "/" + createFileName(name);
    logger().fine("Resource name: " + resourceName);
    try (InputStream in = getClass().getResourceAsStream(resourceName);) {
      if (in != null) {
        logger().info("Load properties from resource: " + resourceName);
        properties.load(in);
      }
    } catch (IOException e) {
      logger().severe("Failure loading properteis from resource: " + resourceName, e);
    }
    return properties;
  }

  private Properties loadFromWorkingDir(String name) {
    String fileName = createFileName(name);
    File file = new File(fileName);
    return loadFromFile(file);
  }

  private Properties merge(Properties... properties) {
    Properties result = new Properties();
    for (Properties toAdd : properties) {
      for (Object key : toAdd.keySet()) {
        result.setProperty(key.toString(), toAdd.getProperty(key.toString()));
      }
    }
    return result;
  }

}
