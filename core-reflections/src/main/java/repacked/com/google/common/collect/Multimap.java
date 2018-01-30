package repacked.com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Created by benjamin-bosch on 07.11.16.
 */
public interface Multimap<KEY, VALUES> {
  boolean put(@Nullable KEY key , @Nullable VALUES value);

  boolean isEmpty();

  Set<KEY> keySet();

  Iterable<? extends Map.Entry<KEY, VALUES>> entries();

  Collection<VALUES> get(KEY key);

  int size();

  Collection<VALUES> values();

  boolean putAll(Multimap<KEY, VALUES> multimap);

  boolean putAll(@Nullable KEY key , Iterable<? extends VALUES> values);

  Map<KEY, Collection<VALUES>> asMap();
}
