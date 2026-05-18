# SRU — Dynamic Dependency Injection (DDI)

Statisches, reflection-basiertes Dependency-Injection-Framework. Bootstrap über Klassenpfad-Scans, Injektion über `@Inject`, optional `@Produces`, `ClassResolver`, `ProducerResolver` und scope-bezogenes Instance-Caching.

## Lizenz und Modul

Lizenz: EUPL 1.2 — siehe `pom.xml` und Header der Quelldateien. Teil des Multi-Module-Builds `com.svenruppert:dependencies`. Artifact-Koordinaten:

```xml
<dependency>
    <groupId>com.svenruppert</groupId>
    <artifactId>ddi</artifactId>
</dependency>
```

## JDK

Aktive Entwicklung läuft auf JDK 17+. Das Modul ist Teil des Parent-Builds und folgt dessen Toolchain.

## Wie DDI eine Implementierung auflöst

Einstiegspunkt ist die statische Fassade `DI` (Paket `com.svenruppert.ddi`). Der Standardablauf:

```java
DI.activatePackages("com.svenruppert");          // klassenpfad-scan auf prefix
SomeService s = DI.activateDI(SomeService.class); // erzeugen + felder injizieren
```

Der `ReflectionsModel`-Scan ist auf das Prefix `com.svenruppert` voreingestellt (`ReflectionsModel.java`, `filterInputsBy`). Nutzerinnen mit anderem Root-Package müssen das Ziel-Prefix explizit via `DI.activatePackages(...)` aktivieren, bevor `activateDI(...)` aufgerufen wird.

### Auflösungs-Matrix für `@Inject Interface`

| Setup                                                                                        | Resultat                                                  |
|----------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| Keine Impl                                                                                   | Exception                                                 |
| Keine Impl, 1 Producer fürs Interface                                                        | Producer fürs Interface                                   |
| 1 Impl                                                                                       | Impl                                                      |
| 1 Impl, 1 Producer für Impl                                                                  | Producer für Impl                                         |
| 1 Impl, 1 Producer fürs Interface                                                            | Producer fürs Interface                                   |
| 1 Impl, n Producer für Impl                                                                  | Exception                                                 |
| 1 Impl, n Producer fürs Interface                                                            | Exception                                                 |
| 1 Impl, 1 Producer fürs Interface + 1 Producer für Impl                                      | Exception                                                 |
| n Impl                                                                                       | Exception                                                 |
| n Impl, 1 verantwortlicher `ClassResolver`                                                   | Ergebnis des `ClassResolver`                              |
| n Impl, 1 Producer fürs Interface                                                            | Producer fürs Interface                                   |
| n Impl, n Producer für Impl                                                                  | Exception                                                 |
| n Impl, 1 verantwortlicher `ClassResolver`, 1 Producer fürs Interface                        | Producer fürs Interface                                   |
| n Impl, 1 verantwortlicher `ClassResolver`, n Producer für Impl                              | gewählte Impl bzw. zugehöriger Producer                   |
| n Impl, n verantwortliche `ClassResolver`                                                    | Exception                                                 |
| 1 Impl, n Producer für Impl, 1 `ProducerResolver` für Impl                                   | vom `ProducerResolver` gewählter Producer                 |
| n Impl, 1 `ClassResolver`, je 0–n Producer pro Impl, 1 `ProducerResolver` pro Impl           | Producer (vom `ProducerResolver`) für die gewählte Impl   |

Für `@Inject KonkreteKlasse` gilt:

* keine Producer → Default-Konstruktor.
* 1 Producer → dieser Producer.

## Mocking-Strategien

DDI lässt sich für Mocks ohne externe Frameworks nutzen. Pro `Service` wird ein zweiter Impl pfad-spezifisch ins Reflection-Modell aktiviert, und ein `ClassResolver` entscheidet pro Test:

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

Alternativ kann ein `@Produces`-annotierter `Producer<T>` einen Mock liefern; das ist besonders nützlich, wenn das gemockte Objekt tief im Graph liegt und mit einem klassischen Mocking-Framework eine Vollhierarchie aufgebaut werden müsste.

Für `ClassResolver`-Implementierungen muss `@ResponsibleFor(InterfaceClass.class)` gesetzt sein — `ClassResolverCheck001` schlägt sonst beim Bootstrap fest.

## Konfiguration über packages-Datei

`DI.bootstrap()` lädt zusätzlich Paket-Prefixe aus einer Resource oder Datei, deren Pfad in der System-Property `com.svenruppert.ddi.packagesfile` steht. Jede Zeile wird als Prefix an `ReflectionsModel.rescan(...)` übergeben.

## Aktueller Entwicklungsstand

Das Modul wurde in der Phase-1-Bugfix-Welle stabilisiert (Race-Conditions im Cache, Entfernung deprecierter `AccessController`-Pfade, Aufräumen von totem Code). Siehe `IMPLEMENTATION_PLAN.md` für die geplanten Folge-Phasen (Modernisierung der Reflection-Schicht, Migration auf `jakarta.inject`, optionale instanzbasierte Container-API).
