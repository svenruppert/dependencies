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

import com.svenruppert.ddi.DDIModelException;
import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.producerresolver.ProducerResolver;
import com.svenruppert.ddi.producerresolver.ProducerResolverLocator;
import com.svenruppert.ddi.scopes.InjectionScopeManager;
import com.svenruppert.dependencies.core.logger.HasLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static com.svenruppert.ddi.producer.ProducerLocator.findProducersFor;

public class InstanceCreator
    implements HasLogger {

  public <T> T instantiate(Class<T> clazz) {

    T newInstance;
    if (clazz.isInterface()) {
      final Class<? extends T> resolve = DI.resolveImplementingClass(clazz);
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
    //explicite all combinations
    if (classOrInterf.isInterface() && !clazz.isInterface()) {
      resolverTarget = clazz;
    } else if (!classOrInterf.isInterface() && clazz.isInterface()) {
      resolverTarget = classOrInterf;
    } else if (!classOrInterf.isInterface()) {
      resolverTarget = classOrInterf;
    } else { //both are interfaces
      resolverTarget = null; // Fallback case
    }

    final Set<Class<?>> producerClasses = findProducersFor(classOrInterf);
    if (producerClasses.isEmpty() && resolverTarget == null) {
      logger().warn("no producer found for {} and {}", classOrInterf, clazz);
    }

    //Check Scopes..
    final boolean managedByMeTarget = InjectionScopeManager.isManagedByMe(classOrInterf);
    final boolean managedByMeImpl = InjectionScopeManager.isManagedByMe(resolverTarget);

    if (managedByMeTarget) {
      final T cast = (T) InjectionScopeManager.getInstance(classOrInterf);
      if (cast != null) {
        return cast;
      }
    } else if (managedByMeImpl) {
      final T cast = (T) InjectionScopeManager.getInstance(resolverTarget);
      if (cast != null) {
        return cast;
      }
    }


    if (producerClasses.size() == 1) {
      final Class cls = (Class) producerClasses.toArray()[0];
      final T result = createInstanceWithThisProducer(cls);
      putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
      return result;
    } else if (producerClasses.size() > 1) {
      return createInstanceWithProducers(classOrInterf, clazz, resolverTarget, managedByMeTarget, managedByMeImpl, producerClasses);
    } else if (producerClasses.isEmpty()) {

      if (clazz.isInterface()) {
        throw new DDIModelException(" only interfaces found for " + classOrInterf);
      } else {
//        final Set<Class<?>> producersForImpl = new ProducerLocator().findProducersFor(clazz);
//        return createInstanceWithProducers(classOrInterf, clazz, resolverTarget, managedByMeTarget, managedByMeImpl, producersForImpl);

        final T result;
        try {

          //find Producer for Impl
          final Set<Class<?>> producersForImpl = findProducersFor(clazz);
          if (producersForImpl.isEmpty()) {
            result = (T) clazz.getDeclaredConstructor().newInstance();
//            DI.activateDI(result);
//            putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
          } else if (producersForImpl.size() > 1) {
            //TODO find ProducerResolver
            final Set<Class<? extends ProducerResolver>> producerResolverClasses
                = new ProducerResolverLocator().findProducersResolverFor(resolverTarget);

            if (producerResolverClasses.size() > 1) {
              throw new DDIModelException("to many producersResolver for Impl " + clazz + " - > " + producerResolverClasses);
            } else if (producerResolverClasses.isEmpty()) {
              throw new DDIModelException("no producersResolver for Impl " + clazz + " and n Producers - > " + producersForImpl);
            } else {
              Class<? extends ProducerResolver> producerResolverClass = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
              final ProducerResolver producerResolver = producerResolverClass.getDeclaredConstructor().newInstance();
              final Class<Producer<T>> producerClass = producerResolver.resolve(clazz);
              final Producer<T> tProducer = producerClass.getDeclaredConstructor().newInstance();
              DI.activateDI(tProducer);
              result = tProducer.create();
//              DI.activateDI(result);
            }
//            throw new DDIModelException("to many producers for Impl " + clazz + " - > " + producersForImpl);
          } else {
            final Class<Producer<T>> producerClass = (Class<Producer<T>>) producersForImpl.toArray()[0];
            final Producer<T> tProducer = producerClass.getDeclaredConstructor().newInstance();
            DI.activateDI(tProducer);
            result = tProducer.create();
//            DI.activateDI(result);
          }
          putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
          return result;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {

          throw new DDIModelException(e);
        }
      }
    }

    throw new RuntimeException("this point should never reached...");
  }

  private <T> T createInstanceWithThisProducer(final Class cls) {
    try {
      Producer<T> producer = (Producer<T>) cls.getDeclaredConstructor().newInstance();
      DI.activateDI(producer);
      final T instance = producer.create();
//      return DI.activateDI(instance);
      return instance;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger().warn("could not create instance ", e);
      throw new DDIModelException(e);
    }
  }

  private <T> void putToScope(final Class classOrInterf, final Class clazz, final boolean managedByMeTarget, final boolean managedByMeImpl, final T result) {
    if (managedByMeTarget && managedByMeImpl) {
      InjectionScopeManager.manageInstance(classOrInterf, result);
    } else if (managedByMeTarget) {
      InjectionScopeManager.manageInstance(classOrInterf, result);
    } else if (managedByMeImpl) InjectionScopeManager.manageInstance(clazz, result);
  }

  private <T> T createInstanceWithProducers(final Class classOrInterf, final Class clazz, final Class resolverTarget, final boolean managedByMeTarget, final boolean managedByMeImpl, final Set<Class<?>> producerClassses) {
    final Set<Class<? extends ProducerResolver>> producerResolverClasses
        = new ProducerResolverLocator().findProducersResolverFor(resolverTarget);
    if (producerResolverClasses.size() == 1) {
      final Class<? extends ProducerResolver> producerResolverClass
          = (Class<? extends ProducerResolver>) producerResolverClasses.toArray()[0];
      try {
        final ProducerResolver producerResolver = producerResolverClass.getDeclaredConstructor().newInstance();
        DI.activateDI(producerResolver);
        final T result = createInstanceWithThisProducer(producerResolver.resolve(resolverTarget));
        putToScope(classOrInterf, clazz, managedByMeTarget, managedByMeImpl, result);
        return result;
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        throw new DDIModelException(e);
      }
    } else if (producerResolverClasses.size() > 1) {
      throw new DDIModelException("toooo many ProducerResolver for interface/class " + resolverTarget + " - " + producerResolverClasses);
    } else { // empty
      throw new DDIModelException(" to many Producer and no ProducerResolver found for " + classOrInterf + " - " + producerClassses);
    }
  }


}