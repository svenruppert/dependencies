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
package com.svenruppert.ddi.bootstrap;

/*-
 * #%L
 * SRU - DDI
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
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
 * #L%
 */

import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DIContainer;
import com.svenruppert.ddi.ResponsibleFor;
import com.svenruppert.ddi.implresolver.ClassResolver;

import java.util.Set;

public class ClassResolverCheck001 {

  private final DIContainer container;

  public ClassResolverCheck001() {
    this(DIContainer.global());
  }

  public ClassResolverCheck001(DIContainer container) {
    this.container = container;
  }

  public void execute() {
    final Set<Class<? extends ClassResolver>> subTypes = container.getSubTypesOf(ClassResolver.class);
    for (final Class<? extends ClassResolver> aClassResolver : subTypes) {
      if (!aClassResolver.isAnnotationPresent(ResponsibleFor.class)) {
        throw new DDIModelException("Found ClassResolver without @ResponsibleFor annotation= " + aClassResolver);
      }
    }
  }
}
