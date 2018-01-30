/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package junit.org.rapidpm.microservice.persistence.jdbc.dao.v001;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.rapidpm.microservice.persistence.jdbc.JDBCConnectionPool;
import junit.org.rapidpm.microservice.persistence.jdbc.dao.v001.UserDAO.User;

public class UserDAOTest001 extends UserDAOBaseTest {


  @Test
  public void test001() throws Exception {
    final Optional<JDBCConnectionPool> connectionPoolOptional = pools().getPool(poolname());
    final JDBCConnectionPool connectionPool = connectionPoolOptional.get();
    final UserDAO userDAO = new UserDAO().workOnPool(connectionPool);

    final User user = new User(001 , "jon" , "doe" , "jon.d@yahooo.com");
    userDAO.writeUser(user);

    final Optional<User> resultUser = userDAO.readUser(001);
    assertNotNull(resultUser);
    assertTrue(resultUser.isPresent());
    assertEquals(user.getCustomerID() , resultUser.get().getCustomerID());
    assertEquals(user.getFirstname() , resultUser.get().getFirstname());

    final User user02 = new User(002 , "jane" , "doe" , "jane.d@yahooo.com");
    userDAO.writeUser(user02);

    final Optional<String> resultMail = userDAO.readMailAddress(002);
    assertNotNull(resultMail);
    assertTrue(resultMail.isPresent());
    assertEquals("jane.d@yahooo.com" , resultMail.get());
  }


}
