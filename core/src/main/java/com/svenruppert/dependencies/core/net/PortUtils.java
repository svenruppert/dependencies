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
package com.svenruppert.dependencies.core.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>PortUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PortUtils {

  /**
   * <p>defaultRestPort.</p>
   *
   * @return a int.
   */
  public int defaultRestPort() {
    return 7081;
  }

  /**
   * <p>defaultServletPort.</p>
   *
   * @return a int.
   */
  public int defaultServletPort() {
    return 7080;
  }

  //TODO static ??
  /**
   * <p>nextFreePortForTest.</p>
   *
   * @return a int.
   */
  @SuppressFBWarnings(
      value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "The creation is don eon purpose"
  )
  public int nextFreePortForTest() {
    int counter = 0;
    final Random random = new Random();
    while (counter < 1_00) {
      try {
        final int port = 1024 + (random.nextInt(65535 - 2048));
        new ServerSocket(port).close();
        return port;
      } catch (IOException ex) {
        counter = counter + 1;
      }
    }
    // if the program gets here, no port in the range was found
    throw new RuntimeException("no free port found");
  }

  /**
   * <p>isPortAvailable.</p>
   *
   * @param port a int.
   * @return a boolean.
   */
  public boolean isPortAvailable(int port) {
    try {
      new ServerSocket(port).close();
    } catch (IOException e) {
      // port is used
      return false;
    }
    // port is unused
    return true;
  }

}
