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
package com.svenruppert.dependencies.core.fs;

/*-
 * #%L
 * SRU - Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2026 Sven Ruppert
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */



import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>PathUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PathUtils {

  private PathUtils() {
  }

  /**
   * <p>createUrwxGrwxArwx.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwxGrwxArwx() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    perms.add(PosixFilePermission.OWNER_EXECUTE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    perms.add(PosixFilePermission.GROUP_EXECUTE);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    perms.add(PosixFilePermission.OTHERS_EXECUTE);
    return perms;
  }

  /**
   * <p>createUrwxGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwxGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    perms.add(PosixFilePermission.OWNER_EXECUTE);
    //add group permissions
    //add others permissions
    return perms;
  }

  /**
   * <p>createUoooGrwxAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUoooGrwxAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    perms.add(PosixFilePermission.GROUP_EXECUTE);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUoooGoooArwx.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUoooGoooArwx() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    //add group permissions
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    perms.add(PosixFilePermission.OTHERS_EXECUTE);
    return perms;
  }

  /**
   * <p>createUrwoGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    //add others permissions
    return perms;
  }


  /**
   * <p>createUrwoGrwoAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGrwoAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUrwoGrwoArwo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrwoGrwoArwo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    perms.add(PosixFilePermission.OWNER_WRITE);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    perms.add(PosixFilePermission.GROUP_WRITE);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    perms.add(PosixFilePermission.OTHERS_WRITE);
    return perms;
  }

  /**
   * <p>createUrooGoooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGoooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    //add others permissions
    return perms;
  }


  /**
   * <p>createUrooGrooAooo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGrooAooo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    //add others permissions
    return perms;
  }

  /**
   * <p>createUrooGrooAroo.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public static Set<PosixFilePermission> createUrooGrooAroo() {
    final Set<PosixFilePermission> perms = new HashSet<>();
    perms.add(PosixFilePermission.OWNER_READ);
    //add group permissions
    perms.add(PosixFilePermission.GROUP_READ);
    //add others permissions
    perms.add(PosixFilePermission.OTHERS_READ);
    return perms;
  }


}
