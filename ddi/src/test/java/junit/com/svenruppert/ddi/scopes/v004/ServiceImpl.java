/*
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the EUPL, Version 1.2 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package junit.com.svenruppert.ddi.scopes.v004;

import org.junit.jupiter.api.Assertions;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ServiceImpl implements Service {
  public ServiceImpl() {
  }

  @Inject
  private SingleResource singleResource;

  @PostConstruct
  private void postConstruct() {
    //HikariDataSource dataSource = ((this.pools == null) ? this.pools = new JDBCConnectionPools() : this.pools).getDataSource(poolname);
    if(singleResource == null){
      Assertions.fail("too bad..");
    }
  }

  @Override
  public long value() {
    return singleResource.value;
  }
}
