/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.svenruppert.dependencies.core.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import com.svenruppert.dependencies.core.logger.HasLogger;

/**
 * Creates a {@link Properties} object merged from different sources.
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PropertiesResolver implements HasLogger {
  /** Constant <code>CONFIG_LOCATION_PROPERTY="rapidpm.configlocation"</code> */
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
   * </ol>
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
    Properties propertiesFromEnvironmentSource = loadFromEnviromentSource(name);
    return merge(propertiesFromResource, propertiesFromWorkingDir, propertiesFromHomeDir,
        propertiesFromEnvironmentSource);
  }

  private Properties loadFromEnviromentSource(String name) {
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
    Arrays.stream(properties)
          .forEach(toAdd -> toAdd.keySet()
                                 .forEach(
                                     key -> result.setProperty(key.toString(), toAdd.getProperty(key.toString()))));
    return result;
  }
}
