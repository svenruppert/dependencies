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
package com.svenruppert.ddi.producerresolver;

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

import com.svenruppert.ddi.DIContainer;
import com.svenruppert.ddi.ResponsibleFor;

import java.util.Set;
import java.util.stream.Collectors;

public class ProducerResolverLocator {

  private final DIContainer container;

  public ProducerResolverLocator() {
    this(DIContainer.global());
  }

  public ProducerResolverLocator(DIContainer container) {
    this.container = container;
  }

  public Set<Class<? extends ProducerResolver>> findProducersResolverFor(final Class clazzOrInterf) {
    return container
        .getSubTypesOf(ProducerResolver.class)
        .stream()
        .filter(c -> c.isAnnotationPresent(ResponsibleFor.class))
        .filter(c -> {
          final ResponsibleFor responsibleFor = c.getAnnotation(ResponsibleFor.class);
          final Class<? extends ResponsibleFor> responsibleForClass = responsibleFor.value();
          return responsibleForClass.equals(clazzOrInterf);
        })
        .collect(Collectors.toSet());
  }
}
