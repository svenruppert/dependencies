# SRU — Dynamic Dependency Injection (DDI)

Project page: **[ddi.svenruppert.com](https://ddi.svenruppert.com)**

Static, reflection-based dependency-injection framework. Bootstraps via classpath scans, injects through `@Inject`, optionally backed by `@Produces`, `ClassResolver`, `ProducerResolver`, and scope-bound instance caching.

## Licence and module

Licence: EUPL 1.2 — see `pom.xml` and the header of every source file. Part of the multi-module build `com.svenruppert:dependencies`. Artefact coordinates:

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>ddi</artifactId>
</dependency>
```

## JDK

Active development runs on JDK 17+. The module is part of the parent build and follows its toolchain.

## How DDI resolves an implementation

The entry point is the static facade `DI` (package `com.svenruppert.ddi`). The standard flow:

```java
DI.activatePackages("com.svenruppert");           // classpath scan by prefix
SomeService s = DI.activateDI(SomeService.class); // instantiate + inject fields
```

The `ReflectionsModel` scan is configured for the prefix `com.svenruppert` by default (`ReflectionsModel.java`, `filterInputsBy`). Consumers with a different root package must activate the target prefix explicitly via `DI.activatePackages(...)` before calling `activateDI(...)`.

### Resolution matrix for `@Inject Interface`

| Setup                                                                                       | Result                                                  |
|---------------------------------------------------------------------------------------------|---------------------------------------------------------|
| No impl                                                                                     | Exception                                               |
| No impl, 1 producer for the interface                                                       | Producer for the interface                              |
| 1 impl                                                                                      | Impl                                                    |
| 1 impl, 1 producer for the impl                                                             | Producer for the impl                                   |
| 1 impl, 1 producer for the interface                                                        | Producer for the interface                              |
| 1 impl, n producers for the impl                                                            | Exception                                               |
| 1 impl, n producers for the interface                                                       | Exception                                               |
| 1 impl, 1 producer for the interface + 1 producer for the impl                              | Exception                                               |
| n impls                                                                                     | Exception                                               |
| n impls, 1 responsible `ClassResolver`                                                      | Result of the `ClassResolver`                           |
| n impls, 1 producer for the interface                                                       | Producer for the interface                              |
| n impls, n producers for the impl                                                           | Exception                                               |
| n impls, 1 responsible `ClassResolver`, 1 producer for the interface                        | Producer for the interface                              |
| n impls, 1 responsible `ClassResolver`, n producers for the impl                            | Selected impl or its matching producer                  |
| n impls, n responsible `ClassResolver`s                                                     | Exception                                               |
| 1 impl, n producers for the impl, 1 `ProducerResolver` for the impl                         | Producer chosen by the `ProducerResolver`               |
| n impls, 1 `ClassResolver`, 0–n producers per impl, 1 `ProducerResolver` per impl           | Producer (via `ProducerResolver`) for the chosen impl   |

For `@Inject ConcreteClass`:

* No producer, no `@Inject` constructor → default constructor.
* No producer, exactly one `@Inject` constructor → that constructor, with its parameters resolved by the container (see [Constructor injection](#constructor-injection)).
* 1 producer → that producer.

## Constructor injection

In addition to field injection, DDI honours an `@Inject`-annotated constructor when it instantiates a concrete class. The two styles compose: the `@Inject` constructor populates the immutable `final` slots, then field injection fills in any remaining `@Inject` fields, then `@PostConstruct` runs.

```java
public class OrderService {
    private final Repository repo;
    private final AuditLog audit;

    @Inject
    public OrderService(Repository repo, AuditLog audit) {
        this.repo = repo;
        this.audit = audit;
    }
}

OrderService service = DI.activateDI(OrderService.class);
```

Rules:

* Exactly one `@Inject` constructor → that constructor is used.
* No `@Inject` constructor → the default no-arg constructor is used (existing behaviour, unchanged).
* More than one `@Inject` constructor on the same class → `DDIModelException` whose message names the offending class.
* Non-public constructors (package-private, `protected`, `private`) work — DDI calls `setAccessible(true)` on the chosen constructor.

Each constructor parameter is resolved recursively through the owning container with full activation — field injection and `@PostConstruct` run on every argument before it is passed in.

Parameter qualifiers narrow the candidate implementations exactly as they do on `@Inject` fields. `@Named` and any annotation meta-annotated with `@Qualifier` are recognised:

```java
public class OrderService {
    @Inject
    public OrderService(@Named("primary") Repository repo) {
        // resolves to the @Named("primary") Repository implementation
    }
}
```

The same set of resolution rules from the table above applies to each parameter independently — qualifiers narrow first, the `ClassResolver` / producer flow takes over from there.

## Mocking strategies

DDI can be used for mocking without any external framework. For each `Service` a second impl is activated into the reflection model alongside the production one, and a `ClassResolver` decides per test which one to use:

```java
@BeforeEach
void setUp() {
  DI.clearReflectionModel();
  DI.activatePackages("com.svenruppert");
  DI.activatePackages(this.getClass());
}

public interface Service {
  String doWork(String txt);
}

public static class ServiceA implements Service {
  public String doWork(String txt) { return txt + "A"; }
}

public static class ServiceB implements Service {
  public String doWork(String txt) { return txt + "B"; }
}

@ResponsibleFor(Service.class)
public static class ServiceClassResolver implements ClassResolver<Service> {
  @Override public Class<? extends Service> resolve(Class<Service> i) {
    return ServiceB.class;
  }
}

@Test
void usesMock() {
  assertEquals(ServiceB.class, DI.activateDI(Service.class).getClass());
}
```

Alternatively, a `@Produces`-annotated `Producer<T>` can return a mock; this is especially handy when the object being mocked sits deep inside the graph and would otherwise require a classic mocking framework to wire up its entire hierarchy.

Every `ClassResolver` implementation must carry `@ResponsibleFor(InterfaceClass.class)` — otherwise `ClassResolverCheck001` fails at bootstrap.

## Hermetic setups with `DIContainer`

The static `DI` facade is a thin delegator to a process-wide singleton, `DIContainer.global()`. For most application code that is exactly what you want. Tests, embedded scenarios, and any code that needs to run several parallel resolution graphs side-by-side can instantiate a `DIContainer` directly — own `ReflectionsModel`, own resolver / producer caches, own scope registrations.

### Default container

```java
DIContainer container = new DIContainer();
container.activatePackages("com.example");
MyService s = container.activateDI(MyService.class);
```

The default constructor uses `ReflectionsModel.DEFAULT_SCAN_PREFIX` (`com.svenruppert`). Anything outside that prefix has to be brought in explicitly via `activatePackages(...)` — same contract as the static `DI` facade.

### Container with a custom scan prefix

```java
DIContainer container = new DIContainer("com.example");
```

The prefix is remembered on the container; a `container.clearReflectionModel()` rebuilds the underlying `ReflectionsModel` with exactly that prefix instead of falling back to the default. Accessor: `container.scanPrefix()`.

### Builder

For the same effect with a fluent API (and room for further options as they land in later releases):

```java
DIContainer container = DIContainer.builder()
    .withScanPrefix("com.example")
    .build();
```

### Isolation guarantees

The following pieces of state are **per container** and do not leak:

* The `ReflectionsModel` and the set of activated package prefixes.
* The resolver and producer caches (`ImplementingClassResolver`, `ProducerLocator`).
* The class-to-scope-name mapping (`registerClassForScope` / `deRegisterClassForScope`).
* The list of known `InjectionScope` instances (rebuilt from the container's reflection model).

The following piece of state is **shared** by all containers in the JVM:

* The instances stored inside `JVMSingletonInjectionScope` — its `SINGLETONS` map is a `private static`, so it is JVM-wide by design. Non-shared scopes (`ThreadScope`, `RequestScope`, …) are on the roadmap (see `IMPLEMENTATION_PLAN.md` §3.1); until they land, build your hermetic tests around classes that are not registered into the singleton scope, or clear the scope between cases.

### Worked example: two parallel containers in one test

```java
@Test
void independentContainers() {
    DIContainer a = DIContainer.builder().withScanPrefix("com.example.a").build();
    DIContainer b = DIContainer.builder().withScanPrefix("com.example.b").build();

    a.activatePackages("com.example.a");
    b.activatePackages("com.example.b");

    assertTrue(a.isPkgPrefixActivated("com.example.a"));
    assertFalse(b.isPkgPrefixActivated("com.example.a"),
                "activation on container a must not leak into container b");
}
```

For day-to-day tests `DI.clearReflectionModel()` on the global container is still the lightest path; reach for `new DIContainer(...)` when two resolution graphs really need to coexist.

## Configuration via a packages file

`DI.bootstrap()` can additionally load package prefixes from a resource or file whose path is given in the system property `com.svenruppert.ddi.packagesfile`. Each line is passed as a prefix to `ReflectionsModel.rescan(...)`.

## Current development status

Release 06.02.00 closed out phases 1 and 2 of `IMPLEMENTATION_PLAN.md`:

* Phase 1 — bugfix wave (cache races, deprecated `AccessController` paths gone, dead-code tidy-up).
* Phase 2.1 — reflection library migrated to `org.reflections:reflections:0.10.2`.
* Phase 2.2 — `javax.inject` / `javax.annotation` → `jakarta.*` (breaking; migration guide in `RELEASE-NOTES-06.02.00.md`).
* Phase 2.3 — static facade lifted into an instance-based `DIContainer`.
* Phase 2.4 — configurable scan prefix via `DIContainer.builder().withScanPrefix(...)`.
* Phase 2.5 — optional `@Named` / `@Qualifier` narrowing on the resolver path.

PIT mutation coverage stands at 96 % (287/300), zero SpotBugs findings, 119/119 tests green.

The open phase-3 work is tracked in `IMPLEMENTATION_PLAN.md`:

* 3.1 — scope ecosystem (`ThreadScope`, `RequestScope`, `EventLoopScope`); enables non-JVM-wide isolation between `DIContainer` instances.
* 3.2 — constructor injection (an `@Inject`-annotated constructor takes precedence over the default one; enables `final` fields).
* 3.3 — PIT mutation run wired into CI, with per-release survivor reports under `_docu/mutations/`.
