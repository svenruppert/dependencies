# Implementation Plan — DDI

Plan für die nächste Version des Moduls `ddi` auf Basis der Analyse vom 2026-05-18. Java-Target: 17+, Modul-Parent `06.01.01-SNAPSHOT`.

Branch-Strategie: alles auf `develop`. Phase 1 ist non-breaking und wird zuerst gemerged; Phase 2 ist die Major-Welle (API-Bruch, neue Major-Version) und wird in kleinen, lauffähigen Commits geliefert.

---

## Phase 1 — Bugfix-Welle (non-breaking)

Ziel: Korrektheits-, Thread-Safety- und JDK-Forward-Compatibility-Probleme beseitigen, ohne öffentliche APIs zu brechen.

### 1.1 `AccessController.doPrivileged` entfernt
- Datei: `src/main/java/com/svenruppert/ddi/DI.java` (`injectIntoField`).
- Problem: `AccessController.doPrivileged` ist seit Java 17 deprecated for removal. SecurityManager ist in modernen JDKs entkernt. Wrapper hatte keinen Effekt mehr.
- Fix: Direkt `field.setAccessible(true)` + `field.set(...)`, normales Exception-Handling. Imports `java.security.AccessController` und `PrivilegedAction` entfernt.

### 1.2 `PostConstruct`-Annotation sauber gehandhabt
- Datei: `DI.java` (`invokeMethodWithAnnotation`).
- Problem: Anonyme `PostConstruct`-Subklasse als Annotation-Instanz war Code-Smell, nur um an `getMethodsAnnotatedWith(...)` zu kommen.
- Fix: Neue Overload `ReflectionsModel.getMethodsAnnotatedWith(Class, Class<? extends Annotation>)` als primärer Pfad. Alte `(Class, Annotation)`-Overload bleibt als Delegator (kein API-Bruch).
- Bonus: `method.setAccessible(true)`-Restore entfällt (irrelevant für die Lebenszeit der `Method`-Instanzen aus dem Cache, und auch in JDK 9+ ohne SecurityManager).

### 1.3 Cache-Races behoben
- Dateien:
  - `producer/ProducerLocator.java` (`findProducersFor`)
  - `implresolver/ImplementingClassResolver.java` (`handleManySubTypes`)
  - `reflections/ReflectionsModel.java` (`getSubTypesOf`, `getSubTypesWithoutInterfacesAndGeneratedOf`, `getTypesAnnotatedWith(Class)`, `getMethodsAnnotatedWith(...)`)
  - `scopes/InjectionScopeManager.java` (`deRegisterClassForScope`)
- Problem: `containsKey(...) → get(...)` über `ConcurrentHashMap` ist nicht atomar. Wenn ein zweiter Thread zwischen den beiden Aufrufen `clearCache()` ausführt, kann `get(...)` `null` liefern. Die Aufrufer dereferenzieren oder leiten dieses `null` weiter.
- Fix: Durchgängig `computeIfAbsent(...)` für das Caching, `Map.get(...) + null-check` für reine Lese-Pfade. `deRegisterClassForScope` ruft jetzt direkt `remove(...)`.

### 1.4 Unreachable Branches schreien laut
- Dateien:
  - `implresolver/ImplementingClassResolver.handleOneSubType`: `return null;` als „kann eigentlich nicht passieren"-Fallback → `throw new IllegalStateException(...)` mit Kontext (`interf`).
  - `producer/InstanceCreator`: bare `RuntimeException("this point should never reached...")` → `DDIModelException` mit Kontext.

### 1.5 Toten Code entfernt
- `DI.java`: drei auskommentierte `activatePackages(boolean parallelExecutors, ...)`-Overloads entfernt.
- `ReflectionsModel.java`: `StaticMetricsProxyScanner`- und `StaticLoggingProxyScanner`-Blöcke (Scanner-Array und zwei Methoden) entfernt.

### 1.6 Tests
- Neuer `CacheConcurrencyTest` (`junit.com.svenruppert.ddi.concurrency`):
  - 16 Reader-Threads × 2 000 Iterationen rufen `DI.resolveImplementingClass(Service.class)` bzw. `ProducerLocator.findProducersFor(StandaloneService.class)` parallel auf, während ein Clearer-Thread durchgehend `ProducerLocator.clearCache()` zieht. Verifiziert non-null, typenkorrekte Resultate.
  - Vor 1.3 hätte dieser Test unter Last sporadisch NPE/Assertion-Fehler produzieren können.

### 1.7 README auf Stand
- `rapidpm`-Reste entfernt (Maven-Badges, Beispielcode `DI.activatePackages("org.rapidpm")`, fehlender `_docu/`-Bildlink).
- Maven-Koordinaten auf `com.svenruppert:ddi`.
- JDK-Note auf 17+.
- Resolver-Verhalten in Tabelle statt Bullet-Liste.
- Hinweis auf den festen `com.svenruppert`-Prefix-Filter in `ReflectionsModel`.

**Definition of Done Phase 1:** alle bestehenden Tests grün, neue Concurrency- und Mutation-Gap-Tests grün, `mvn -pl ddi verify` ohne Fehler. Tests: 100 (vorher 86), 0 Failures.

### 1.8 Mutation-Coverage angehoben
- Neuer `MutationGapTest` (`junit.com.svenruppert.ddi.mutation`, 12 Methoden) deckt gezielt PIT-Lücken ab: `PostConstruct` via `activateDI(Class)`, `isPkgPrefixActivated(Class)`-Overload, `resolveImplementingClass` auf konkreten Klassen, Fehlermeldung mit Impl-/Producer-Klassen im `DDIModelException`, `bootstrap()`-Rescan-Pfad, `setParallelExecutors`-Pfad, `getMethodsAnnotatedWith(Class, Annotation)`-Overload, Cache-Invalidierung bei `activatePackages(String, URL[])` / `(String, Collection<URL>)`, Scope-Caching für Producer und ProducerResolver.
- Kleine Refactorings als Beifang: tote `newSet`-Function in `ReflectionsModel` entfernt, redundanter Zweig in `InstanceCreator.putToScope` entfernt, unerreichbarer Branch in `InstanceCreator.createNewInstance` (Zeile 79) entfernt.
- PIT-Ergebnis vor/nach:
  - Vorher: 246 Mutationen, 217 killed (88 %), Line-Coverage 94 %, Test-Strength 92 %, 9 ohne Coverage.
  - Nachher: 240 Mutationen, 233 killed (97 %), Line-Coverage 95 %, Test-Strength 97 %, 0 ohne Coverage.
- Verbleibende 7 Überlebende sind unter der aktuellen Architektur als **äquivalent** zu betrachten (`bootstrap.clearCache` doppelt-aufgerufen via `clearReflectionModel`; Boundary in `createInstanceWithProducers:205` durch vorherigen Branch abgefangen; `setParallelExecutors` ohne externe Observation; `InjectionScopeManager.removeScope`-Pfad ist unter der statischen Fassade nicht erreichbar). Sie werden mit der Container-Refaktorierung in Phase 2.3 mit-adressiert.

---

## Phase 2 — Modernisierung (Major Release, breaking)

Empfehlung: eigene Major-Version (z. B. `07.00.00`). Aufgebaut in mehreren kleinen Commits, jeder Commit lässt das Modul kompilierbar.

### 2.1 Reflection-Schicht ablösen ✅
- Datei: `reflections/ReflectionsModel.java`, `reflections/PkgTypesScanner.java`, `reflections/DDIReflectionUtils.java`, `pom.xml`, plus 2 Tests.
- Umgesetzt 2026-05-18:
  - `pom.xml`: Dependency von `net.oneandone.reflections8:reflections8:0.11.7` auf `org.reflections:reflections:0.10.2` umgestellt.
  - Imports überall `org.reflections.*`.
  - `FilterBuilder.prefix(String)` ist in 0.10.x weg → ersetzt durch `FilterBuilder.includePackage(String)`.
  - `useParallelExecutor()` ist in 0.10.x weg → ersetzt durch `ConfigurationBuilder.setParallel(boolean)`.
  - `PkgTypesScanner` neu geschrieben gegen die neue funktionale `Scanner`-Schnittstelle (`List<Map.Entry<String, String>> scan(ClassFile)`); der alte `AbstractScanner`-Helper mit `getStore()`/`acceptResult()` ist weg.
  - `setExpandSuperTypes(false)` explizit, weil 0.10.x sonst Supertype-Links über den Filter hinaus in den Store zieht und die Filter-Semantik bricht (ein bestehender Test in `ReflectionModel004Test` hat das aufgedeckt).
- Filter-Prefix-Konfigurierbarkeit bleibt für 2.4 reserviert.
- Tests: 100/100 grün; PIT: 233/240 (97 %), Line-Coverage 95 %, Test-Strength 97 % — keine Regression gegenüber Phase 1.

### 2.2 `javax.inject` → `jakarta.inject` ✅
- Umgesetzt 2026-05-19.
- Parent-`pom.xml`: `javax.annotation:javax.annotation-api:1.3.2` → `jakarta.annotation:jakarta.annotation-api:2.1.1`; neu hinzu `jakarta.inject:jakarta.inject-api:2.0.1`.
- `ddi/pom.xml`: gleiche Umstellung im `<dependencies>`-Block plus neue `jakarta.inject-api`-Dependency.
- Vendored `ddi/src/main/java/javax/inject/Inject.java` gelöscht (samt jetzt leerem `javax/inject/`- und `javax/`-Ordner).
- Alle Imports: `javax.inject.Inject` → `jakarta.inject.Inject`, `javax.annotation.PostConstruct` → `jakarta.annotation.PostConstruct`. Betroffene Dateien: 1 Produktionsdatei (`DI.java`), 36 Testdateien.
- Migrationsleitfaden für Konsumenten: einmaliges Sed `s|javax\.inject\.|jakarta.inject.|g` und `s|javax\.annotation\.PostConstruct|jakarta.annotation.PostConstruct|g` über das eigene Test- und Produktionsverzeichnis; `jakarta.inject-api` + `jakarta.annotation-api` als Dependencies einbinden.
- Tests: 100/100 grün; PIT: 233/240 (97 %), Line-Coverage 95 %, Test-Strength 97 % — keine Regression.

### 2.3 Static Facade → optionale Container-Instanz ✅
- Umgesetzt 2026-05-19. Non-breaking für bestehende Konsumenten (statische API bleibt erhalten und routet auf den globalen Container).
- Neue Klasse `com.svenruppert.ddi.DIContainer` mit allen Operationen als Instanzmethoden plus `DIContainer.global()`-Singleton. Hält:
  - `ReflectionsModel reflectionsModel` (war static in `DI`).
  - `boolean bootstrapedNeeded` (war static in `DI`).
  - `Map<Class<?>, Class<? extends ClassResolver>> implResolverCache` (war static in `ImplementingClassResolver`).
  - `Map<Class<?>, Set<Class<?>>> producerCache` (war static in `ProducerLocator`).
  - `Map<String, String> classNameToScopeName` + `Map<String, InjectionScope> injectionScopeMap` (waren static in `InjectionScopeManager`).
- `DI.java` reduziert auf reinen statischen Delegator (1 Zeile pro Methode → `DIContainer.global().X(...)`).
- `InjectionScopeManager`, `ImplementingClassResolver`, `ProducerLocator` reduziert auf reine statische Delegatoren ohne eigenen Zustand — bestehende Tests, die direkt diese Klassen statisch ansprechen, laufen unverändert.
- `InstanceCreator`, `ProducerResolverLocator`, `ClassResolverCheck001`: neuer Konstruktor mit `DIContainer`-Parameter (plus parameterloser Default-Konstruktor für Backwards-Compat, der `DIContainer.global()` nutzt). Interne `DI.X(...)` / `InjectionScopeManager.X(...)`-Aufrufe ersetzt durch `container.X(...)`.
- Ein einziger Test (`ReflectionModel007Test`) griff per Reflection auf `DI.reflectionsModel` als statisches Feld zu — umgestellt auf `DIContainer.global().reflectionsModel()` (neuer öffentlicher Accessor).
- Neue Tests:
  - `ContainerIsolationTest` (4 Methoden): Paket-Aktivierung leakt nicht zwischen Containern, Scope-Registrierung leakt nicht, Within-Container-Caching funktioniert, Field-Injection routet durch den eigenen Container. Notiz im Code zur JVM-weiten `JVMSingletonInjectionScope`-Sharing-Semantik.
  - `StaticFacadeDelegationTest` (6 Methoden): pinnt jeden statischen Delegator auf den global-Container fest (ImplementingClassResolver, ProducerLocator, InjectionScopeManager, plus `DI.getSubTypesOf` / `getSubTypesWithoutInterfacesAndGeneratedOf`, plus Null-Safe-Check `isManagedScope(null)`).
- Tests: 110/110 grün (vorher 100); PIT: 265/277 (96 %), Line-Coverage 95 %, Test-Strength 96 %, 0 ohne Coverage. Die 12 Survivors sind unter der bestehenden Public-API beobachtungs-äquivalent (Cache-Clear-Pfade ohne externe Observation, `removeScope`-Lambdas im selten erreichten Cleanup-Pfad, `setParallelExecutors`-ThreadLocal-Set ohne Verhaltens-Diff, ConditionalsBoundary in einem schon vorher fallthrough-blockierten Pfad).

### 2.4 Konfigurierbarer Class-Path-Filter ✅
- Umgesetzt 2026-05-19. Non-breaking.
- `ReflectionsModel`: neue Konstante `DEFAULT_SCAN_PREFIX = "com.svenruppert"`. Neuer Konstruktor `ReflectionsModel(String scanPrefix)`; der parameterlose Konstruktor delegiert auf den Default. Neuer öffentlicher Accessor `scanPrefix()`. Das hartcodierte `includePackage("com.svenruppert")` ist durch `includePackage(this.scanPrefix)` ersetzt.
- `DIContainer`: neuer Konstruktor `DIContainer(String scanPrefix)`. Der parameterlose Konstruktor delegiert auf `DEFAULT_SCAN_PREFIX` für Backwards-Compatibility der globalen Fassade. Der Container merkt sich den Prefix; `clearReflectionModel()` baut den `ReflectionsModel` mit demselben Prefix neu auf — Tests, die den Modell-Reset auslösen, verlieren ihre Custom-Konfiguration nicht. Neuer öffentlicher Accessor `scanPrefix()`.
- Neue API: `DIContainer.builder().withScanPrefix(String).build()` (`DIContainer.Builder` als statische innere Klasse). Default-Prefix ist `DEFAULT_SCAN_PREFIX`. Bewusst ein Builder statt einer Konstruktor-Kette, damit zukünftige Optionen (z. B. `withScanners(...)`, `withInitialPackages(...)`) ohne Bruch der API ergänzbar sind.
- Neuer Test `ScanPrefixTest` (4 Methoden, `junit.com.svenruppert.ddi.container`): Default-Container scannt `com.svenruppert`; Builder respektiert den Custom-Prefix; ein nicht-existenter Prefix liefert leere Sub-Type-Sets; `clearReflectionModel()` erhält den konfigurierten Prefix.
- Tests: 114/114 grün (vorher 110); PIT: 270/282 (96 %), Line-Coverage 95 %, Test-Strength 96 %, 0 ohne Coverage.

### 2.5 Qualifier-Support ✅
- Umgesetzt 2026-05-19. Non-breaking — der vorhandene `ClassResolver`-Pfad bleibt unverändert; Qualifier wirken zusätzlich.
- `DIContainer`: neue Methode `resolveImplementingClass(Class, Set<Annotation>)`. Die alte parameterlose Variante delegiert mit leerem Set. Neuer privater Helper `narrowByQualifiers(Set candidates, Set<Annotation> required)` + `matchesAllQualifiers(Class, Set<Annotation>)`.
- Semantik der Narrow-Stufe: `qualifiers.isEmpty()` → keine Filterung; ein Match → genau diese Impl (`handleOneSubType`); mehrere Matches → bestehender `handleManySubTypes`-Pfad (ClassResolver/Producer); kein Match → `DDIModelException` mit Qualifier- und Kandidaten-Liste.
- `Named`-Annotation wird per `value()`-Vergleich gematcht; alle anderen Qualifier per `Annotation.equals(...)` (Standard-Annotation-Identität).
- `DIContainer.injectAttributesForClass` extrahiert Qualifier per `extractQualifiers(Field)` (sucht `@Named` und alle `@Qualifier`-meta-annotierten Markierungen) und reicht sie an `new InstanceCreator(this).instantiate(targetType, qualifiers)` durch.
- `InstanceCreator`: neue `instantiate(Class, Set<Annotation>)`-Overload; die parameterlose Variante delegiert mit leerem Set.
- Neuer Test `QualifierTest` (5 Methoden, `junit.com.svenruppert.ddi.qualifier`): `@Named("primary")` → richtige Impl; `@Named("secondary")` → andere Impl; `@Named("does-not-exist")` → `DDIModelException` mit ausagekräftiger Message; custom `@Qualifier`-meta-annotierte Annotation (`@Audited`) narrowt korrekt; `@Inject` ohne Qualifier fällt weiterhin auf `ClassResolver` durch (Regressions-Check).
- Tests: 119/119 grün (vorher 114); PIT: 287/300 (96 %), Line-Coverage 95 %, Test-Strength 96 %, 1 NO_COVERAGE (PIT-Mapping-Quirk auf `matchesAllQualifiers` — die `return false`-Pfade sind durch `unknownNameThrowsWithExplanatoryMessage` exerziert; PIT attribuiert die Mutation jedoch auf die Schließklammer einer Branch-Unterstruktur). Verhaltens-äquivalent.

---

## Phase 3 — Ergänzungen

### 3.1 Scope-Ökosystem
- Heute: einziger ausgelieferter Scope ist `JVMSingletonInjectionScope`.
- Plan: `ThreadScope` (`ThreadLocal`-Storage), `RequestScope` (jakarta.servlet-optional, in eigenem Untermodul), `EventLoopScope` (Vaadin/Reactor-kompatibel).
- Tests: jeder Scope mit Lifecycle-Tests (enter/leave, parallele Threads).

### 3.2 Constructor-Injection
- Heute: nur Field-Injection (`@Inject`-Felder) wird in `DI.injectAttributesForClass` ausgewertet.
- Plan: zusätzlicher Pfad in `InstanceCreator`: konstruktor-Argumente per `@Inject`-Konstruktor auflösen, bevor Default-Konstruktor verwendet wird.
- Vorteil: erlaubt `final`-Felder, vermeidet Reflection-`setAccessible` für State, ist die heute übliche DI-Idiomatik.

### 3.3 PIT-Mutation-Score halten
- Mutation-Run als Bestandteil der CI verankern, Ziel ≥ 80 % Mutation Score auf `com.svenruppert.ddi.*`.
- Surviving Mutants in `_docu/mutations/` (Markdown-Report) ablegen, pro Release aktualisieren.

---

## Reihenfolge der Commits

1. Phase 1.1 — `AccessController` entfernt.
2. Phase 1.2 — `PostConstruct`-Handling sauber.
3. Phase 1.3 — Cache-Races behoben.
4. Phase 1.4 — Unreachable-Branches.
5. Phase 1.5 — toten Code entfernt.
6. Phase 1.6 — `CacheConcurrencyTest`.
7. Phase 1.7 — README-Refresh.
8. Snapshot-Release nach Phase 1.
9. Phase 2.1 — Reflection-Schicht-Migration.
10. Phase 2.2 — `jakarta.inject`-Migration.
11. Phase 2.3 — `DIContainer`-Klasse.
12. Phase 2.4 — konfigurierbarer Scan-Prefix.
13. Phase 2.5 — Qualifier-Support.
14. Major-Release nach Phase 2.
15. Phase 3 — schrittweise.
