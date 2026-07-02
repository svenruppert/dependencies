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
package junit.com.svenruppert.ddi.concurrency;

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

import com.svenruppert.ddi.DI;
import com.svenruppert.ddi.Produces;
import com.svenruppert.ddi.producer.Producer;
import com.svenruppert.ddi.producer.ProducerLocator;
import junit.com.svenruppert.ddi.DDIBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stresses the static caches in {@link com.svenruppert.ddi.producer.ProducerLocator}
 * and {@link com.svenruppert.ddi.implresolver.ImplementingClassResolver} under
 * concurrent read access combined with concurrent cache invalidation.
 *
 * Prior to the Phase-1 bugfix wave both caches used a {@code containsKey} / {@code get}
 * pattern that could observe {@code null} when another thread cleared the cache between
 * the two calls. The {@code computeIfAbsent}-based implementation must produce non-null,
 * type-correct results under contention.
 */
public class CacheConcurrencyTest
    extends DDIBaseTest {

  private static final int READER_THREADS = 16;
  private static final int ITERATIONS = 2_000;

  public interface Service {
    String work();
  }

  public static class ServiceImpl
      implements Service {
    @Override
    public String work() {
      return "impl";
    }
  }

  @Produces(StandaloneService.class)
  public static class StandaloneServiceProducer
      implements Producer<StandaloneService> {
    @Override
    public StandaloneService create() {
      return new StandaloneService();
    }
  }

  public static class StandaloneService {
    public String work() {
      return "standalone";
    }
  }

  @Test
  public void resolveUnderConcurrentCacheClear() throws Exception {
    runWithCacheClearer(() -> {
      for (int n = 0; n < ITERATIONS; n++) {
        final Class<? extends Service> resolved = DI.resolveImplementingClass(Service.class);
        Assertions.assertNotNull(resolved, "resolve returned null");
        Assertions.assertEquals(ServiceImpl.class, resolved);
      }
      return null;
    });
  }

  @Test
  public void findProducersForUnderConcurrentClear() throws Exception {
    runWithCacheClearer(() -> {
      for (int n = 0; n < ITERATIONS; n++) {
        final var producers = ProducerLocator.findProducersFor(StandaloneService.class);
        Assertions.assertNotNull(producers);
        Assertions.assertEquals(1, producers.size(), "expected exactly one producer");
        Assertions.assertEquals(StandaloneServiceProducer.class, producers.iterator().next());
      }
      return null;
    });
  }

  private void runWithCacheClearer(Callable<Void> reader) throws Exception {
    final ExecutorService readers = Executors.newFixedThreadPool(READER_THREADS);
    final ExecutorService clearer = Executors.newSingleThreadExecutor();
    final CountDownLatch start = new CountDownLatch(1);
    final AtomicBoolean stop = new AtomicBoolean(false);
    final ConcurrentLinkedQueue<Throwable> failures = new ConcurrentLinkedQueue<>();

    final List<Future<?>> readerFutures = new ArrayList<>();
    try {
      for (int i = 0; i < READER_THREADS; i++) {
        readerFutures.add(readers.submit(() -> {
          try {
            start.await();
            reader.call();
          } catch (Throwable t) {
            failures.add(t);
          }
          return null;
        }));
      }
      clearer.submit(() -> {
        try {
          start.await();
          while (!stop.get()) {
            ProducerLocator.clearCache();
          }
        } catch (Throwable t) {
          failures.add(t);
        }
      });

      start.countDown();

      for (Future<?> f : readerFutures) {
        f.get(30, TimeUnit.SECONDS);
      }
    } finally {
      stop.set(true);
      readers.shutdownNow();
      clearer.shutdownNow();
      readers.awaitTermination(5, TimeUnit.SECONDS);
      clearer.awaitTermination(5, TimeUnit.SECONDS);
    }

    Assertions.assertTrue(failures.isEmpty(),
                          () -> "concurrent failures: " + List.copyOf(failures));
  }
}
