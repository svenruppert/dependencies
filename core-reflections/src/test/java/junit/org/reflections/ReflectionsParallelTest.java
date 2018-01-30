package junit.org.reflections;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeAll;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/** */
public class ReflectionsParallelTest extends ReflectionsTest {

  @BeforeAll
  public static void init() {
    reflections = new Reflections(new ConfigurationBuilder()
                                      .setUrls(singletonList(ClasspathHelper.forClass(TestModel.class)))
                                      .filterInputsBy(TestModelFilter)
                                      .setScanners(
                                          new SubTypesScanner(false) ,
                                          new TypeAnnotationsScanner() ,
                                          new FieldAnnotationsScanner() ,
                                          new MethodAnnotationsScanner() ,
                                          new MethodParameterScanner() ,
                                          new MethodParameterNamesScanner() ,
                                          new MemberUsageScanner())
                                      .useParallelExecutor());
  }
}
