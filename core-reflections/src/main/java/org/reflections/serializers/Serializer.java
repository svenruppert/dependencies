package org.reflections.serializers;

import java.io.File;
import java.io.InputStream;

import org.reflections.Reflections;


public interface Serializer {

  Reflections read(InputStream inputStream);


  File save(Reflections reflections , String filename);


  String toString(Reflections reflections);
}
