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
package com.svenruppert.functional.model;

/*-
 * #%L
 * SRU - Functional
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

import java.io.Serializable;
import java.util.Objects;

public interface DataRecordsSerializable {
  /**
   * Copyright (C) 2010 RapidPM
   * Licensed under the EUPL, Version 1.2 (the "Licence");
   * you may not use this file except in compliance with the Licence.
   * You may obtain a copy of the Licence at:
   * <a href="https://joinup.ec.europa.eu/software/page/eupl">EUPL 1.2</a>
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the Licence is distributed on an "AS IS" basis,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the Licence for the specific language governing permissions and
   * limitations under the Licence.
   * <p>
   * Created by RapidPM - Team on 10.12.16.
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Pair<T1 extends Serializable, T2 extends Serializable>
      implements Serializable {
    private T1 t1;
    private T2 t2;

    /**
     * <p>Constructor for Pair.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     */
    public Pair(final T1 t1, final T2 t2) {
      this.t1 = t1;
      this.t2 = t2;
    }

    /**
     * <p>next.</p>
     *
     * @param a    a T1 object.
     * @param b    a T2 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @return a {@link Pair} object.
     */
    public static <T1 extends Serializable, T2 extends Serializable> Pair<T1, T2> next(T1 a, T2 b) {
      return new Pair<>(a, b);
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Pair{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Pair)) return false;
      final Pair<?, ?> pair = (Pair<?, ?>) o;
      return Objects.equals(t1, pair.t1) &&
          Objects.equals(t2, pair.t2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return Objects.hash(t1, t2);
    }
  }

  /**
   * Copyright (C) 2010 RapidPM
   * Licensed under the EUPL, Version 1.2 (the "Licence");
   * you may not use this file except in compliance with the Licence.
   * You may obtain a copy of the Licence at:
   * https://joinup.ec.europa.eu/software/page/eupl
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the Licence is distributed on an "AS IS" basis,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the Licence for the specific language governing permissions and
   * limitations under the Licence.
   * <p>
   * Created by RapidPM - Team on 10.12.16.
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Quad<T1 extends Serializable, T2 extends Serializable, T3 extends Serializable, T4 extends Serializable> {

    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;

    /**
     * <p>Constructor for Quad.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     * @param t3 a T3 object.
     * @param t4 a T4 object.
     */
    public Quad(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
      this.t1 = t1;
      this.t2 = t2;
      this.t3 = t3;
      this.t4 = t4;
    }

    /**
     * <p>next.</p>
     *
     * @param t1   a T1 object.
     * @param t2   a T2 object.
     * @param t3   a T3 object.
     * @param t4   a T4 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @param <T3> a T3 object.
     * @param <T4> a T4 object.
     * @return a {@link Quad} object.
     */
    public static <T1 extends Serializable,
        T2 extends Serializable,
        T3 extends Serializable,
        T4 extends Serializable> Quad<T1, T2, T3, T4> next(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
      return new Quad<>(t1, t2, t3, t4);
    }

    /**
     * <p>Getter for the field <code>t4</code>.</p>
     *
     * @return a T4 object.
     */
    public T4 getT4() {
      return t4;
    }

    /**
     * <p>Getter for the field <code>t3</code>.</p>
     *
     * @return a T3 object.
     */
    public T3 getT3() {
      return t3;
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Quad)) return false;

      Quad<?, ?, ?, ?> quad = (Quad<?, ?, ?, ?>) o;

      if (t1 != null ? !t1.equals(quad.t1) : quad.t1 != null) return false;
      if (t2 != null ? !t2.equals(quad.t2) : quad.t2 != null) return false;
      if (t3 != null ? !t3.equals(quad.t3) : quad.t3 != null) return false;
      return t4 != null ? t4.equals(quad.t4) : quad.t4 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = t1 != null ? t1.hashCode() : 0;
      result = 31 * result + (t2 != null ? t2.hashCode() : 0);
      result = 31 * result + (t3 != null ? t3.hashCode() : 0);
      result = 31 * result + (t4 != null ? t4.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Quad{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          ", t3=" + t3 +
          ", t4=" + t4 +
          '}';
    }
  }

  /**
   * Copyright (C) 2010 RapidPM
   * Licensed under the EUPL, Version 1.2 (the "Licence");
   * you may not use this file except in compliance with the Licence.
   * You may obtain a copy of the Licence at:
   * https://joinup.ec.europa.eu/software/page/eupl
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the Licence is distributed on an "AS IS" basis,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the Licence for the specific language governing permissions and
   * limitations under the Licence.
   * <p>
   * Created by RapidPM - Team on 10.12.16.
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Quint<T1 extends Serializable,
      T2 extends Serializable,
      T3 extends Serializable,
      T4 extends Serializable,
      T5 extends Serializable> {

    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;

    /**
     * <p>Constructor for Quint.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     * @param t3 a T3 object.
     * @param t4 a T4 object.
     * @param t5 a T5 object.
     */
    public Quint(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
      this.t1 = t1;
      this.t2 = t2;
      this.t3 = t3;
      this.t4 = t4;
      this.t5 = t5;
    }

    /**
     * <p>next.</p>
     *
     * @param t1   a T1 object.
     * @param t2   a T2 object.
     * @param t3   a T3 object.
     * @param t4   a T4 object.
     * @param t5   a T5 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @param <T3> a T3 object.
     * @param <T4> a T4 object.
     * @param <T5> a T5 object.
     * @return a {@link Quint} object.
     */
    public static <T1 extends Serializable,
        T2 extends Serializable,
        T3 extends Serializable,
        T4 extends Serializable,
        T5 extends Serializable> Quint<T1, T2, T3, T4, T5> next(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
      return new Quint<>(t1, t2, t3, t4, t5);
    }

    /**
     * <p>Getter for the field <code>t5</code>.</p>
     *
     * @return a T5 object.
     */
    public T5 getT5() {
      return t5;
    }

    /**
     * <p>Getter for the field <code>t4</code>.</p>
     *
     * @return a T4 object.
     */
    public T4 getT4() {
      return t4;
    }

    /**
     * <p>Getter for the field <code>t3</code>.</p>
     *
     * @return a T3 object.
     */
    public T3 getT3() {
      return t3;
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Quint)) return false;

      Quint<?, ?, ?, ?, ?> quint = (Quint<?, ?, ?, ?, ?>) o;

      if (t1 != null ? !t1.equals(quint.t1) : quint.t1 != null) return false;
      if (t2 != null ? !t2.equals(quint.t2) : quint.t2 != null) return false;
      if (t3 != null ? !t3.equals(quint.t3) : quint.t3 != null) return false;
      if (t4 != null ? !t4.equals(quint.t4) : quint.t4 != null) return false;
      return t5 != null ? t5.equals(quint.t5) : quint.t5 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = t1 != null ? t1.hashCode() : 0;
      result = 31 * result + (t2 != null ? t2.hashCode() : 0);
      result = 31 * result + (t3 != null ? t3.hashCode() : 0);
      result = 31 * result + (t4 != null ? t4.hashCode() : 0);
      result = 31 * result + (t5 != null ? t5.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Quint{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          ", t3=" + t3 +
          ", t4=" + t4 +
          ", t5=" + t5 +
          '}';
    }
  }

  /**
   * <p>Sept class.</p>
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Sept<T1 extends Serializable, T2 extends Serializable, T3 extends Serializable,
      T4 extends Serializable, T5 extends Serializable, T6 extends Serializable, T7 extends Serializable> {

    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;
    private T6 t6;
    private T7 t7;

    /**
     * <p>Constructor for Sept.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     * @param t3 a T3 object.
     * @param t4 a T4 object.
     * @param t5 a T5 object.
     * @param t6 a T6 object.
     * @param t7 a T7 object.
     */
    public Sept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
      this.t1 = t1;
      this.t2 = t2;
      this.t3 = t3;
      this.t4 = t4;
      this.t5 = t5;
      this.t6 = t6;
      this.t7 = t7;
    }

    /**
     * <p>next.</p>
     *
     * @param t1   a T1 object.
     * @param t2   a T2 object.
     * @param t3   a T3 object.
     * @param t4   a T4 object.
     * @param t5   a T5 object.
     * @param t6   a T6 object.
     * @param t7   a T7 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @param <T3> a T3 object.
     * @param <T4> a T4 object.
     * @param <T5> a T5 object.
     * @param <T6> a T6 object.
     * @param <T7> a T7 object.
     * @return a {@link Sept} object.
     */
    public static <T1 extends Serializable, T2 extends Serializable, T3 extends Serializable,
        T4 extends Serializable, T5 extends Serializable, T6 extends Serializable, T7 extends Serializable> Sept<T1, T2, T3, T4, T5, T6, T7> next(final T1 t1, final T2 t2, final T3 t3,
                                                                                                                                                  final T4 t4, final T5 t5, final T6 t6, final T7 t7) {
      return new Sept<>(t1, t2, t3, t4, t5, t6, t7);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Sept)) return false;

      Sept<?, ?, ?, ?, ?, ?, ?> sept = (Sept<?, ?, ?, ?, ?, ?, ?>) o;

      if (t1 != null ? !t1.equals(sept.t1) : sept.t1 != null) return false;
      if (t2 != null ? !t2.equals(sept.t2) : sept.t2 != null) return false;
      if (t3 != null ? !t3.equals(sept.t3) : sept.t3 != null) return false;
      if (t4 != null ? !t4.equals(sept.t4) : sept.t4 != null) return false;
      if (t5 != null ? !t5.equals(sept.t5) : sept.t5 != null) return false;
      if (t6 != null ? !t6.equals(sept.t6) : sept.t6 != null) return false;
      return t7 != null ? t7.equals(sept.t7) : sept.t7 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = t1 != null ? t1.hashCode() : 0;
      result = 31 * result + (t2 != null ? t2.hashCode() : 0);
      result = 31 * result + (t3 != null ? t3.hashCode() : 0);
      result = 31 * result + (t4 != null ? t4.hashCode() : 0);
      result = 31 * result + (t5 != null ? t5.hashCode() : 0);
      result = 31 * result + (t6 != null ? t6.hashCode() : 0);
      result = 31 * result + (t7 != null ? t7.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Sept{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          ", t3=" + t3 +
          ", t4=" + t4 +
          ", t5=" + t5 +
          ", t6=" + t6 +
          ", t7=" + t7 +
          '}';
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * <p>Getter for the field <code>t3</code>.</p>
     *
     * @return a T3 object.
     */
    public T3 getT3() {
      return t3;
    }

    /**
     * <p>Getter for the field <code>t4</code>.</p>
     *
     * @return a T4 object.
     */
    public T4 getT4() {
      return t4;
    }

    /**
     * <p>Getter for the field <code>t5</code>.</p>
     *
     * @return a T5 object.
     */
    public T5 getT5() {
      return t5;
    }

    /**
     * <p>Getter for the field <code>t6</code>.</p>
     *
     * @return a T6 object.
     */
    public T6 getT6() {
      return t6;
    }

    /**
     * <p>Getter for the field <code>t7</code>.</p>
     *
     * @return a T7 object.
     */
    public T7 getT7() {
      return t7;
    }
  }

  /**
   * <p>Sext class.</p>
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Sext<T1 extends Serializable, T2 extends Serializable, T3 extends Serializable,
      T4 extends Serializable, T5 extends Serializable, T6 extends Serializable> {

    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;
    private T6 t6;

    /**
     * <p>Constructor for Sext.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     * @param t3 a T3 object.
     * @param t4 a T4 object.
     * @param t5 a T5 object.
     * @param t6 a T6 object.
     */
    public Sext(final T1 t1, final T2 t2, final T3 t3,
                final T4 t4, final T5 t5, final T6 t6) {
      this.t1 = t1;
      this.t2 = t2;
      this.t3 = t3;
      this.t4 = t4;
      this.t5 = t5;
      this.t6 = t6;
    }

    /**
     * <p>next.</p>
     *
     * @param t1   a T1 object.
     * @param t2   a T2 object.
     * @param t3   a T3 object.
     * @param t4   a T4 object.
     * @param t5   a T5 object.
     * @param t6   a T6 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @param <T3> a T3 object.
     * @param <T4> a T4 object.
     * @param <T5> a T5 object.
     * @param <T6> a T6 object.
     * @return a {@link Sext} object.
     */
    public static <T1 extends Serializable, T2 extends Serializable, T3 extends Serializable,
        T4 extends Serializable, T5 extends Serializable, T6 extends Serializable> Sext<T1, T2, T3, T4, T5, T6> next(final T1 t1, final T2 t2, final T3 t3,
                                                                                                                     final T4 t4, final T5 t5, final T6 t6) {
      return new Sext<>(t1, t2, t3, t4, t5, t6);
    }

    /**
     * <p>Getter for the field <code>t6</code>.</p>
     *
     * @return a T6 object.
     */
    public T6 getT6() {
      return t6;
    }

    /**
     * <p>Getter for the field <code>t5</code>.</p>
     *
     * @return a T5 object.
     */
    public T5 getT5() {
      return t5;
    }

    /**
     * <p>Getter for the field <code>t4</code>.</p>
     *
     * @return a T4 object.
     */
    public T4 getT4() {
      return t4;
    }

    /**
     * <p>Getter for the field <code>t3</code>.</p>
     *
     * @return a T3 object.
     */
    public T3 getT3() {
      return t3;
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Sext)) return false;

      Sext<?, ?, ?, ?, ?, ?> sext = (Sext<?, ?, ?, ?, ?, ?>) o;

      if (t1 != null ? !t1.equals(sext.t1) : sext.t1 != null) return false;
      if (t2 != null ? !t2.equals(sext.t2) : sext.t2 != null) return false;
      if (t3 != null ? !t3.equals(sext.t3) : sext.t3 != null) return false;
      if (t4 != null ? !t4.equals(sext.t4) : sext.t4 != null) return false;
      if (t5 != null ? !t5.equals(sext.t5) : sext.t5 != null) return false;
      return t6 != null ? t6.equals(sext.t6) : sext.t6 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = t1 != null ? t1.hashCode() : 0;
      result = 31 * result + (t2 != null ? t2.hashCode() : 0);
      result = 31 * result + (t3 != null ? t3.hashCode() : 0);
      result = 31 * result + (t4 != null ? t4.hashCode() : 0);
      result = 31 * result + (t5 != null ? t5.hashCode() : 0);
      result = 31 * result + (t6 != null ? t6.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Quint{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          ", t3=" + t3 +
          ", t4=" + t4 +
          ", t5=" + t5 +
          '}';
    }


  }

  class Single<T1 extends Serializable> {
    private T1 t1;

    /**
     * <p>Constructor for Pair.</p>
     *
     * @param t1 a T1 object.
     */
    public Single(final T1 t1) {
      this.t1 = t1;
    }

    /**
     * <p>next.</p>
     *
     * @param a    a T1 object.
     * @param <T1> a T1 object.
     * @return a {@link Single} object.
     */
    public static <T1 extends Serializable> Single<T1> next(T1 a) {
      return new Single<>(a);
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Single{" + "t1=" + t1 + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Single)) return false;
      Single<?> single = (Single<?>) o;
      return Objects.equals(t1, single.t1);
    }

    @Override
    public int hashCode() {
      return Objects.hash(t1);
    }
  }

  /**
   * Copyright (C) 2010 RapidPM
   * Licensed under the EUPL, Version 1.2 (the "Licence");
   * you may not use this file except in compliance with the Licence.
   * You may obtain a copy of the Licence at:
   * https://joinup.ec.europa.eu/software/page/eupl
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the Licence is distributed on an "AS IS" basis,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the Licence for the specific language governing permissions and
   * limitations under the Licence.
   * <p>
   * Created by RapidPM - Team on 10.12.16.
   *
   * @author svenruppert
   * @version $Id: $Id
   */
  class Triple<T1 extends Serializable, T2 extends Serializable, T3 extends Serializable> {
    private T1 t1;
    private T2 t2;
    private T3 t3;

    /**
     * <p>Constructor for Triple.</p>
     *
     * @param t1 a T1 object.
     * @param t2 a T2 object.
     * @param t3 a T3 object.
     */
    public Triple(final T1 t1, final T2 t2, final T3 t3) {
      this.t1 = t1;
      this.t2 = t2;
      this.t3 = t3;
    }

    /**
     * <p>next.</p>
     *
     * @param t1   a T1 object.
     * @param t2   a T2 object.
     * @param t3   a T3 object.
     * @param <T1> a T1 object.
     * @param <T2> a T2 object.
     * @param <T3> a T3 object.
     * @return a {@link Triple} object.
     */
    public static <T1 extends Serializable, T2 extends Serializable, T3 extends Serializable> Triple<T1, T2, T3> next(final T1 t1, final T2 t2, final T3 t3) {
      return new Triple<>(t1, t2, t3);
    }

    /**
     * <p>Getter for the field <code>t3</code>.</p>
     *
     * @return a T3 object.
     */
    public T3 getT3() {
      return t3;
    }

    /**
     * <p>Getter for the field <code>t1</code>.</p>
     *
     * @return a T1 object.
     */
    public T1 getT1() {
      return t1;
    }

    /**
     * <p>Getter for the field <code>t2</code>.</p>
     *
     * @return a T2 object.
     */
    public T2 getT2() {
      return t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Triple)) return false;

      Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;

      if (t1 != null ? !t1.equals(triple.t1) : triple.t1 != null) return false;
      if (t2 != null ? !t2.equals(triple.t2) : triple.t2 != null) return false;
      return t3 != null ? t3.equals(triple.t3) : triple.t3 == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = t1 != null ? t1.hashCode() : 0;
      result = 31 * result + (t2 != null ? t2.hashCode() : 0);
      result = 31 * result + (t3 != null ? t3.hashCode() : 0);
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "Triple{" +
          "t1=" + t1 +
          ", t2=" + t2 +
          ", t3=" + t3 +
          '}';
    }
  }
}