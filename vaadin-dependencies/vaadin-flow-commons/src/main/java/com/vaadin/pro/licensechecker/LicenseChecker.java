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
package com.vaadin.pro.licensechecker;

import java.util.logging.Logger;

public class LicenseChecker {

  public interface Callback {

    public void ok();

    public void failed(Exception e);
  }

  public static void checkLicenseFromStaticBlock(String productName,
                                                 String productVersion) {

  }

  public static void checkLicense(String productName, String productVersion) {
  }

  public static void checkLicenseAsync(String productName,
                                       String productVersion, Callback callback) {

  }

  private static void checkLicense(Product product) {

  }

  public static Logger getLogger() {
    return Logger.getLogger(LicenseChecker.class.getName());
  }

}
