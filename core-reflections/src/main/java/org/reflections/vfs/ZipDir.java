package org.reflections.vfs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.Reflections;
import repacked.com.google.common.collect.AbstractIterator;


public class ZipDir implements Vfs.Dir {
  final java.util.zip.ZipFile jarFile;

  public ZipDir(JarFile jarFile) {
    this.jarFile = jarFile;
  }

  public String getPath() {
    return jarFile.getName();
  }

  public Iterable<Vfs.File> getFiles() {
    return () -> new AbstractIterator<Vfs.File>() {
      final Enumeration<? extends ZipEntry> entries = jarFile.entries();

      protected Vfs.File computeNext() {
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (! entry.isDirectory()) {
            return new ZipFile(ZipDir.this , entry);
          }
        }

        return endOfData();
      }
    };
  }

  public void close() {
    try {
      jarFile.close();
    } catch (IOException e) {
      final LoggingService log = Logger.getLogger(Reflections.class);
      if (log != null) {
        log.warning("Could not close JarFile" , e);
      }
    }
  }

  @Override
  public String toString() {
    return jarFile.getName();
  }
}
