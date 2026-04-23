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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * <p>DirectoryUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class DirectoryUtils {

  /**
   * <p>deleteIndexDirectory.</p>
   *
   * @param directoryName a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean deleteIndexDirectory(final String directoryName) {
    final Path indexDirectory = Paths.get(directoryName);
    return delete(indexDirectory);
  }

  /**
   * <p>deleteIndexDirectory.</p>
   *
   * @param directoryPath a {@link java.nio.file.Path} object.
   * @return a boolean.
   */
  public boolean deleteIndexDirectory(final Path directoryPath) {
    return delete(directoryPath);
  }



  private boolean delete(final Path indexDirectory) {
    if (Files.exists(indexDirectory)) {
      try {
        Files.walkFileTree(indexDirectory, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    return false;
  }


}
