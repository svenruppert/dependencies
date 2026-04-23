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
package com.svenruppert.ddi.reflections;

/*-
 * #%L
 * SRU - DDI
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */

import org.reflections8.scanners.AbstractScanner;
import org.reflections8.util.FilterBuilder;
import org.reflections8.util.SetMultimap;


public class PkgTypesScanner
    extends AbstractScanner {

  /**
   * created new SubTypesScanner. will exclude direct Object subtypes
   */
  public PkgTypesScanner() {
    this(true); //exclude direct Object subtypes by default
  }

  /**
   * created new SubTypesScanner.
   *
   * @param excludeObjectClass if false, include direct {@link Object} subtypes in results.
   */
  public PkgTypesScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings({"unchecked"})
  public void scan(final Object cls) {
    String className = getMetadataAdapter().getClassName(cls);

    int index = className.lastIndexOf(".");
    if (index != -1) {

      final String pkgName = className.substring(0, index);
      if (acceptResult(className)) {
        final SetMultimap<String, String> store = getStore();
        store.putSingle(pkgName, className);
      }
    }
  }
}