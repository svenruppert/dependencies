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
package com.svenruppert.ddi.producer;

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
import com.svenruppert.ddi.producerresolver.ProducerResolver;
import com.svenruppert.ddi.producerresolver.ProducerResolverLocator;
import com.svenruppert.dependencies.core.logger.HasLogger;
import jakarta.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InstanceCreator
    implements HasLogger {

  private final DIContainer container;

  public InstanceCreator() {
    this(DIContainer.global());
  }

  public InstanceCreator(DIContainer container) {
    this.container = container;
  }

  public <T> T instantiate(Class<T> clazz) {
    return instantiate(clazz, Set.of());
  }

  public <T> T instantiate(Class<T> clazz, Set<Annotation> qualifiers) {
    T newInstance;
    if (clazz.isInterface()) {
      final Class<? extends T> resolve = container.resolveImplementingClass(clazz, qualifiers);
      logger().info("resolveImplementingClass {} to {}", clazz, resolve);
      newInstance = createNewInstance(clazz, resolve);
    } else {
      newInstance = createNewInstance(clazz, clazz);
    }
    return newInstance;
  }

  private <T> T createNewInstance(final Class classOrInterf, final Class clazz) {
    logger().info("creating new instance for {} With clazz {}", classOrInterf, clazz);
    final Class resolverTarget;
    if (classOrInterf.isInterface() && !clazz.isInterface()) {
      resolverTarget = clazz;
    } else if (!classOrInterf.isInterface()) {
      resolverTarget = classOrInterf;
    } else { // both are interfaces — no resolvable impl
      resolverTarget = null;
    }

    final Set<Class<?>> producerClasses = container.findProducersFor(classOrInterf);
    if (producerClasses.isEmpty() && resolverTarget == null) {
      logger().warn("no producer found for {} and {}", classOrInterf, clazz);
    }

    final boolean managedByMeTarget = container.isManagedScope(classOrInterf);
    final boolean managedByMeImpl = container.isManagedScope(resolverTarget);

    if (managedByMeTarget) {
      final T cast = (T) container.getScopedInstance(classOrInterf);
      if (cast != null) return cast;
    } else if (managedByMeImpl) {
      final T cast = (T) container.getScopedInstance(resolverTarget);
      if (cast != null) return cast;
    }

    if (producerClasses.size() == 1) {
      final Class cls = (Class) producerClasses.toArray()[0];
      final T result = createInstanceWithThisProducer(cls);
      putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
      return result;
    } else if (producerClasses.size() > 1) {
      return createInstanceWithProducers(classOrInterf, clazz, resolverTarget, managedByMeTarget, managedByMeImpl, producerClasses);
    } else {
      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + classOrInterf);
      }
      final T result;
      try {
        final Set<Class<?>> producersForImpl = container.findProducersFor(clazz);
        if (producersForImpl.isEmpty()) {
          result = (T) newInstanceFromConstructor(clazz);
        } else if (producersForImpl.size() > 1) {
          final Set<Class<? extends ProducerResolver>> producerResolverClasses
              = new ProducerResolverLocator(container).findProducersResolverFor(resolverTarget);

          if (producerResolverClasses.size() > 1) {
            throw new DDIModelException("to many producersResolver for Impl " + clazz + " - > " + producerResolverClasses);
          } else if (producerResolverClasses.isEmpty()) {
            throw new DDIModelException("no producersResolver for Impl " + clazz + " and n Producers - > " + producersForImpl);
          } else {
            Class<? extends ProducerResolver> producerResolverClass = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
            final ProducerResolver producerResolver = producerResolverClass.getDeclaredConstructor().newInstance();
            final Class<Producer<T>> producerClass = producerResolver.resolve(clazz);
            final Producer<T> tProducer = producerClass.getDeclaredConstructor().newInstance();
            container.activateDI(tProducer);
            result = tProducer.create();
          }
        } else {
          final Class<Producer<T>> producerClass = (Class<Producer<T>>) producersForImpl.toArray()[0];
          final Producer<T> tProducer = producerClass.getDeclaredConstructor().newInstance();
          container.activateDI(tProducer);
          result = tProducer.create();
        }
        putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
        return result;
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        throw new DDIModelException(e);
      }
    }
  }

  private <T> T createInstanceWithThisProducer(final Class cls) {
    try {
      Producer<T> producer = (Producer<T>) cls.getDeclaredConstructor().newInstance();
      container.activateDI(producer);
      return producer.create();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger().warn("could not create instance ", e);
      throw new DDIModelException(e);
    }
  }

  private <T> void putToScope(final Class classOrInterf, final Class clazz, final boolean managedByMeTarget, final boolean managedByMeImpl, final T result) {
    if (managedByMeTarget) {
      container.storeScopedInstance(classOrInterf, result);
    } else if (managedByMeImpl) {
      container.storeScopedInstance(clazz, result);
    }
  }

  private <T> T createInstanceWithProducers(final Class classOrInterf, final Class clazz, final Class resolverTarget, final boolean managedByMeTarget, final boolean managedByMeImpl, final Set<Class<?>> producerClassses) {
    final Set<Class<? extends ProducerResolver>> producerResolverClasses
        = new ProducerResolverLocator(container).findProducersResolverFor(resolverTarget);
    if (producerResolverClasses.size() == 1) {
      final Class<? extends ProducerResolver> producerResolverClass
          = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
      try {
        final ProducerResolver producerResolver = producerResolverClass.getDeclaredConstructor().newInstance();
        container.activateDI(producerResolver);
        final T result = createInstanceWithThisProducer(producerResolver.resolve(resolverTarget));
        putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
        return result;
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        throw new DDIModelException(e);
      }
    } else if (producerResolverClasses.size() > 1) {
      throw new DDIModelException("toooo many ProducerResolver for interface/class " + resolverTarget + " - " + producerResolverClasses);
    } else {
      throw new DDIModelException(" to many Producer and no ProducerResolver found for " + classOrInterf + " - " + producerClassses);
    }
  }

  /**
   * Instantiates {@code clazz}, preferring an {@code @Inject}-annotated constructor
   * over the default no-arg constructor. Constructor parameters are resolved
   * recursively through the owning {@link DIContainer}; each parameter's
   * {@code @Named} / {@code @Qualifier} annotations narrow its candidate
   * implementations the same way field injection does.
   *
   * <ul>
   *   <li>Zero {@code @Inject} constructors → fall back to the default constructor.</li>
   *   <li>Exactly one {@code @Inject} constructor → use it; arguments are activated
   *       via {@code container.activateDI(paramType, qualifiers)} which also runs
   *       field injection and {@code @PostConstruct} on each argument.</li>
   *   <li>More than one {@code @Inject} constructor → {@link DDIModelException}.</li>
   * </ul>
   */
  private <T> T newInstanceFromConstructor(final Class<T> clazz)
      throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    final List<Constructor<?>> injectCtors = Arrays.stream(clazz.getDeclaredConstructors())
        .filter(c -> c.isAnnotationPresent(Inject.class))
        .collect(Collectors.toList());
    if (injectCtors.size() > 1) {
      throw new DDIModelException("multiple @Inject constructors on " + clazz + ": " + injectCtors);
    }
    if (injectCtors.isEmpty()) {
      return clazz.getDeclaredConstructor().newInstance();
    }
    final Constructor<?> ctor = injectCtors.get(0);
    ctor.setAccessible(true);
    final Object[] args = resolveConstructorArguments(ctor);
    return (T) ctor.newInstance(args);
  }

  private Object[] resolveConstructorArguments(final Constructor<?> ctor) {
    final Parameter[] params = ctor.getParameters();
    final Object[] args = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      final Parameter p = params[i];
      final Set<Annotation> qualifiers = DIContainer.extractQualifiers(p);
      args[i] = container.activateDI(p.getType(), qualifiers);
    }
    return args;
  }
}
