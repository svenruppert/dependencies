# Implementation Plan — Functional-Reactive

Plan für die nächste Version des Moduls `functional-reactive` auf Basis der Anforderungen vom 2026-05-15. Java-Target: 26 (Records, sealed types, pattern matching for switch verfügbar).

Branch-Strategie: alles auf `develop`. Phase 1 ist non-breaking und wird zuerst gemerged; Phase 2 ist die Major-Welle und wird in kleinen, lauffähigen Commits geliefert.

---

## Phase 1 — Bugfix-Welle (non-breaking)

Ziel: Korrektheits- und Thread-Safety-Probleme beseitigen, ohne öffentliche APIs zu brechen. Keine Major-Version nötig.

### 1.1 Memoizer Race-Condition
- Datei: `src/main/java/com/svenruppert/functional/memoizer/Memoizer.java`
- Problem: `doMemoize(Supplier)` (Zeile 130-140) prüft `if (value == null) value = function.get();` ohne Synchronisation. Konkurrenter Erstzugriff ruft `function.get()` mehrfach auf → bricht den Memoizer-Vertrag.
- Fix: `AtomicReference<T>` mit `compareAndSet`, oder synchronisierte Lazy-Init mit `volatile` + double-checked locking.
- Null-Semantik dokumentieren: `Function`-Variante via `computeIfAbsent` cached `null` nicht — entweder in Javadoc warnen oder Sentinel-Wert einführen (zunächst nur Javadoc).
- Tests ergänzen: `MemoizerConcurrencyTest` — paralleler Erstzugriff zählt Supplier-Aufrufe.

### 1.2 CompletableFutureQueue Immutability
- Datei: `src/main/java/com/svenruppert/functional/reactive/CompletableFutureQueue.java`
- Problem: `private Function<T, CompletableFuture<R>> resultFunction;` (Zeile 56) ist nicht `final`. `thenCombineAsyncFromArray` (Zeile 90-96) verwendet zudem Raw-Type `CompletableFutureQueue cfq = this;` und Reassignment in der Schleife.
- Fix: Feld `final` machen, `thenCombineAsyncFromArray` neu schreiben mit typisiertem Akkumulator (Stream/Reduce) oder direkter Schleifen-Komposition ohne Reassignment des Feldes.
- Zusatz: Executor-Defaultmechanismus in einem Feld vorbereiten (siehe 2.3); jetzt nicht öffentlich machen.

### 1.3 StringFunctions Locale-Bug
- Datei: `src/main/java/com/svenruppert/functional/StringFunctions.java`
- Problem: 17 Stellen mit `toLowerCase()` / `toUpperCase()` ohne Locale (Zeilen 94, 165, 244-245, 306, 330, 618, 662, 673, 747, 881, 1077, 1094, 1116, 1152, 1156, 1177, 1213). Bricht in türkischem Locale (dotless I).
- Fix: Überall `Locale.ROOT` mitgeben (alle Aufrufer sind technische String-Operationen wie `indexOf`, `startsWith`, `camelCase`-Stream-Hilfen — `ROOT` ist semantisch korrekt).

### 1.4 StringFunctions Typo `indexOfCoseSensitive`
- Datei: `src/main/java/com/svenruppert/functional/StringFunctions.java:614`
- Problem: Methodenname enthält Typo. 12 Test-Aufrufer in `StringFunctionsTest.java:576-592`.
- Fix: Neue Methode `indexOfCaseSensitive()` mit identischer Signatur einführen. Alte Methode bleibt mit `@Deprecated(since = "06.02.00", forRemoval = true)` und delegiert auf die neue. Tests bekommen einen zusätzlichen Block, der `indexOfCaseSensitive` aufruft; alte Test-Aufrufe bleiben bis zur Removal.

### 1.5 Result kleinere Korrekturen (non-breaking)
- Datei: `src/main/java/com/svenruppert/functional/model/Result.java`
- `getOrElse(Supplier<T>)` wirft NPE wenn Supplier `null` liefert (Zeile 217). Anpassung: `null` als Fallback zulassen, NPE nur wenn beides null und Aufrufer das ausdrücklich erwartet hatte. Konservativ: lassen wie ist, aber Javadoc-Hinweis ergänzen.
- `Boolean isPresent()` / `Boolean isAbsent()` (boxed): default-Methoden `default boolean isSuccess()` / `default boolean isFailure()` ergänzen, die auf `isPresent`/`isAbsent` delegieren. Boxed-Varianten bleiben für API-Kompatibilität.

### 1.6 Tests für kritische Pfade (deine Liste)
- `MemoizerConcurrencyTest` (siehe 1.1)
- `CompletableFutureQueueTest` mit Executor-Übergabe und Failure-Pfad (vorab gegen aktuelle API, vor 2.3)
- `ResultFailurePropagationTest` — Map/FlatMap/thenCombine erhalten Failure
- `StringFunctionsLocaleTest` — case-insensitive Ops mit türkischem Locale

**Definition of Done Phase 1:** Alle bestehenden Tests grün, neue Tests grün, `mvn -pl functional-reactive verify` ohne Fehler, keine Signaturänderung an public API.

---

## Phase 2 — API-Modernisierung (Major Release, breaking)

Empfehlung: Eigene Version (z. B. `07.00.00`). Wird in mehreren Commits aufgebaut; jeder Commit lässt das Modul übersetzbar.

### 2.1 Result<T, E> als sealed interface mit records
- Neue Datei: `model/Result.java` (Ersatz der bisherigen).
- API:
  ```java
  public sealed interface Result<T, E> permits Result.Success, Result.Failure {
      record Success<T, E>(T value) implements Result<T, E> { ... }
      record Failure<T, E>(E error)  implements Result<T, E> { ... }

      boolean isSuccess();
      boolean isFailure();
      T getOrThrow();
      T getOrElse(T fallback);
      T getOrElse(Function<E, T> fromError);
      <R> R fold(Function<T, R> onSuccess, Function<E, R> onFailure);
      Result<T, E> peek(Consumer<T> c);
      Result<T, E> peekFailure(Consumer<E> c);
      Result<T, E> recover(Function<E, T> recovery);
      Result<T, E> recoverWith(Function<E, Result<T, E>> recovery);
      <U> Result<U, E> map(Function<? super T, ? extends U> f);
      <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> f);
      <F> Result<T, F> mapError(Function<E, F> f);
  }
  ```
- Klare Trennung von Empty und Failure: `success(null)` ist verboten. Für „kann leer sein" verwendet der Aufrufer `Result<Optional<T>, E>`.
- Convenience-Variante `Try<T> extends Result<T, Throwable>` mit `Try.of(CheckedSupplier<T>)` fängt Exceptions und behält Cause + Stacktrace.

### 2.2 Migration der internen Aufrufer
- `StringFunctions.java` nutzt `Result.success`/`Result.failure` an vielen Stellen → Signaturen umstellen auf `Result<String, String>` (oder generischer `Result<T, String>`).
- `Case.java` ggf. anpassen.
- Migrationsleitfaden in Release-Notes mit Vorher/Nachher-Snippets pro Bruch.

### 2.3 Async-API mit Executor-Übergabe
- `mapAsync(Function, Executor)`, `flatMapAsync(Function, Executor)` auf `Result`.
- `thenCombineAsync` Overload mit `Executor`-Parameter.
- `CompletableFutureQueue`: Executor-Feld + Builder-Methoden `withExecutor(Executor)`.
- Doku-Hinweis auf `Executors.newVirtualThreadPerTaskExecutor()` für IO-lastige Workloads.

### 2.4 Tuple-Modelle auf Records umstellen
- Datei: `model/DataRecords.java` (925 Zeilen) → Records.
- Hart breaking: alte Getter `getT1()` entfallen, neuer Accessor `t1()` (Record-Konvention).
- `DataRecordsSerializable`: konsistent `implements Serializable` + explizite `serialVersionUID`.

---

## Phase 3 — Erweiterungen

### 3.1 Result Collection-/Stream-Hilfen
- `Result.sequence(List<Result<T, E>>) → Result<List<T>, E>` (fail-fast).
- `Result.traverse(List<T>, Function<T, Result<U, E>>) → Result<List<U>, E>`.
- `Collector<Result<T, E>, ?, Result<List<T>, E>> toListResult()`.
- `Pair<List<T>, List<E>> partition(Stream<Result<T, E>>)`.

### 3.2 Validation-API (applicative)
- Neuer Typ `Validation<T, E>` mit `Valid`/`Invalid` (sealed, records).
- `combine(Validation<U, E>, BiFunction<T, U, R>) → Validation<R, E>` aggregiert Fehler.
- Brücken: `validation.toResult(combineErrors)`, `result.toValidation()`.
- Doku-Beispiel: DTO-Validierung mit mehreren Feldfehlern.

### 3.3 String-Utilities Refactoring
- Datei aufsplitten: `StringPredicates`, `StringCasing`, `StringSearch`, `StringFormatting`.
- Alle public Methoden mit `@NonNull`-Kontrakten (entweder Annotation oder klare Javadoc-Konvention).
- Wo `null` erlaubt sein soll: explizite `*Nullable`-Variante oder `Optional<String>`-Parameter.

---

## Reihenfolge der Commits

1. Phase 1.1 — Memoizer fix + Tests
2. Phase 1.2 — CompletableFutureQueue immutable
3. Phase 1.3 — StringFunctions Locale (alle Stellen in einem Commit, da Pattern uniform)
4. Phase 1.4 — `indexOfCaseSensitive` + Deprecation-Stub
5. Phase 1.5 — Result default-Methoden `isSuccess`/`isFailure`
6. Phase 1.6 — Test-Welle (kann teilweise in 1.1–1.4 enthalten sein)
7. Tag oder Snapshot-Release nach Phase 1.
8. Phase 2.1 — neuer `Result<T, E>` als Parallel-Typ in eigenem Package, alt bleibt vorerst.
9. Phase 2.2 — Migration interner Aufrufer.
10. Phase 2.3 — Async-Overloads.
11. Phase 2.4 — Tuple-Records.
12. Alte API entfernen, Major-Release.
13. Phase 3 — schrittweise.
