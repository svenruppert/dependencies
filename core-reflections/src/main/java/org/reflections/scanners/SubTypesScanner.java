package org.reflections.scanners;

import java.util.List;

import org.reflections.util.FilterBuilder;


public class SubTypesScanner extends AbstractScanner {


  public SubTypesScanner() {
    this(true); //exclude direct Object subtypes by default
  }


  public SubTypesScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings({"unchecked"})
  public void scan(final Object cls) {
    String className = getMetadataAdapter().getClassName(cls);
    String superclass = getMetadataAdapter().getSuperclassName(cls);

    if (acceptResult(superclass)) {
      getStore().put(superclass , className);
    }

    for (String anInterface : (List<String>) getMetadataAdapter().getInterfacesNames(cls)) {
      if (acceptResult(anInterface)) {
        getStore().put(anInterface , className);
      }
    }
  }
}
