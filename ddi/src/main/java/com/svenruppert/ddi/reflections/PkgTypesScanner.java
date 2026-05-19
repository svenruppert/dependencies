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

import javassist.bytecode.ClassFile;
import org.reflections.scanners.Scanner;

import java.util.List;
import java.util.Map;

/**
 * Custom {@link Scanner} that records every scanned class against its package name.
 * The reflection store can be queried by package via
 * {@code reflections.getStore().get("PkgTypesScanner")}, which yields a
 * {@code Map<pkgName, Set<className>>}.
 *
 * <p>By default {@code java.lang.Object} is excluded so that the store does not
 * collapse direct {@code Object}-subtypes into one giant bucket — this matches
 * the previous {@code reflections8.scanners.AbstractScanner}-based implementation.
 */
public class PkgTypesScanner
    implements Scanner {

  private final boolean excludeObjectClass;

  public PkgTypesScanner() {
    this(true);
  }

  public PkgTypesScanner(boolean excludeObjectClass) {
    this.excludeObjectClass = excludeObjectClass;
  }

  @Override
  public List<Map.Entry<String, String>> scan(ClassFile classFile) {
    final String className = classFile.getName();
    final int dot = className.lastIndexOf('.');
    if (dot < 0) return List.of();
    if (excludeObjectClass && Object.class.getName().equals(className)) return List.of();
    final String pkgName = className.substring(0, dot);
    return List.of(entry(pkgName, className));
  }
}
