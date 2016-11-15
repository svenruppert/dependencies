package org.rapidpm.dependencies.core.basepattern.multiple;

import java.util.Objects;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by RapidPM - Team on 15.11.16.
 */
public class Tripel<T1, T2, T3> {

  private T1 first;
  private T2 second;
  private T3 third;

  public Tripel(final T1 first, final T2 second, final T3 third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public T1 getFirst() {
    return first;
  }

  public T2 getSecond() {
    return second;
  }

  public T3 getThird() {
    return third;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof Tripel)) return false;
    final Tripel<?, ?, ?> tripel = (Tripel<?, ?, ?>) o;
    return Objects.equals(first, tripel.first) &&
        Objects.equals(second, tripel.second) &&
        Objects.equals(third, tripel.third);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second, third);
  }

  @Override
  public String toString() {
    return "Tripel{" +
        "first=" + first +
        ", second=" + second +
        ", third=" + third +
        '}';
  }


}
