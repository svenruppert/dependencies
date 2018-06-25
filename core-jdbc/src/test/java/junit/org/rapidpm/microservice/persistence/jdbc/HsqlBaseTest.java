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
package junit.org.rapidpm.microservice.persistence.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.rapidpm.dependencies.core.reflections.NewInstances;
import org.rapidpm.microservice.persistence.jdbc.JDBCConnectionPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public abstract class HsqlBaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HsqlBaseTest.class);

  private final String[] scripts = createSQLInitScriptArray();

  public abstract JDBCConnectionPools pools();

  public abstract String[] createSQLInitScriptArray();


  //TODO could be more dynamic
  public void initSchema(final String poolname) throws Exception {

//    DI.clearReflectionModel();
//    DI.activatePackages("org.rapidpm");
//    activateDI4Packages();
//    DI.activatePackages(this.getClass());
//    DI.activateDI(this);

    for (final String script : scripts) {
      final Class<? extends HsqlBaseTest> aClass = baseTestClass();
      System.out.println(aClass.getName());

      final URL resource = aClass.getResource(script);
      LOGGER.debug("resource.toExternalForm() = " + resource.toExternalForm());
      executeSqlScript(poolname, resource.getPath());
    }

    final URL testSqlResource = getClass().getResource(getClass().getSimpleName() + ".sql");
    if (testSqlResource != null) {
      final String testSqlPath = testSqlResource.getPath();
      executeSqlScript(poolname, testSqlPath);
    } else {
      LOGGER.debug("No SQL for " + getClass().getSimpleName());
    }
  }

  public abstract Class baseTestClass();

  private void executeSqlScript(final String poolname, final String filePath) {
    LOGGER.debug("executeSqlScript-poolname = " + poolname);
    LOGGER.debug("executeSqlScript-filePath = " + filePath);

    final HikariDataSource dataSource = pools().getDataSource(poolname);
    try (
        final BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        final Connection connection = dataSource.getConnection();
        final Statement statement = connection.createStatement()
    ) {

      final String sql = buffer.lines().collect(Collectors.joining("\n"));
      statement.executeUpdate(sql);

      connection.commit();
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  protected void startPoolsAndConnect(final String poolname, final String url) {
    startPoolsAndConnect(poolname, url, username(), password());
  }

  protected void startPoolsAndConnect(final String poolname, final String url, final String username, final String passwd) {
    pools()
        .addJDBCConnectionPool(poolname)
        .withJdbcURL(url)
        .withUsername(username)
        .withPasswd(passwd)
        .withTimeout(2000)
        .withAutoCommit(true)
        .done();
    pools().connectPool(poolname);
  }

  public String username() {
    return "SA";
  }

  public String password() {
    return "";
  }

  public String poolname() {
    return this.getClass().getName();
  }


}
