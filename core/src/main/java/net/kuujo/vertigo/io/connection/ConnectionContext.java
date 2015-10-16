/*
 * Copyright 2014 the original author or authors.
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
package net.kuujo.vertigo.io.connection;

import io.vertx.codegen.annotations.VertxGen;
import net.kuujo.vertigo.TypeContext;
import net.kuujo.vertigo.io.port.PortContext;

/**
 * Connection contexts represent a direct connection between two partitions
 * of separate components.<p>
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface ConnectionContext<T extends ConnectionContext<T, U>, U extends PortContext<U, T>> extends TypeContext<T> {

  /**
   * Returns the connection source.
   *
   * @return The connection source.
   */
  SourceContext source();

  /**
   * Returns the connection target.
   *
   * @return The connection target.
   */
  TargetContext target();

  /**
   * Returns whether the connection is ordered.
   *
   * @return Whether the connection is ordered.
   */
  boolean ordered();

  /**
   * Returns whether the connection is at-least-once.
   *
   * @return Whether the connection is at-least-once.
   */
  boolean atLeastOnce();

  /**
   * Returns the connection's parent port.
   *
   * @return The parent port context.
   */
  U port();

  /**
   * Returns the connection's send timeout.
   *
   * @return The connection's send timeout.
   */
  long sendTimeout();

}
