/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.vertigo.function;

/**
 * A single argument function.
 *
 * @author Jordan Halterman
 *
 * @param <T> The function argument type.
 * @param <R> The function return value type.
 */
public interface Function<T, R> {

  /**
   * Executes the function.
   *
   * @param value
   *   The function argument.
   * @return
   *   The function return value.
   */
  R call(T value);

}
