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
import net.kuujo.vertigo.io.connection.impl.OutputConnectionContextImpl;
import net.kuujo.vertigo.io.port.OutputPortContext;

/**
 * Output connection context represents a single partition's output to
 * an partition of another component.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface OutputConnectionContext extends ConnectionContext<OutputConnectionContext, OutputPortContext> {

  /**
   * Returns a new output connection context builder.
   *
   * @return A new output connection context builder.
   */
  static Builder builder() {
    return new OutputConnectionContextImpl.Builder();
  }

  /**
   * Returns a new output connection context builder.
   *
   * @param connection An existing output connection context object to wrap.
   * @return An output connection context builder wrapper.
   */
  static Builder builder(OutputConnectionContext connection) {
    return new OutputConnectionContextImpl.Builder((OutputConnectionContextImpl) connection);
  }

  /**
   * Output connection context builder.
   */
  public static interface Builder extends TypeContext.Builder<Builder, OutputConnectionContext> {

    /**
     * Sets the connection source context.
     *
     * @param source The connection source context.
     * @return The output connection context builder.
     */
    Builder setSource(SourceContext source);

    /**
     * Sets the connection target context.
     *
     * @param target The connection target context.
     * @return The output connection context builder.
     */
    Builder setTarget(TargetContext target);

    /**
     * Sets whether the connection is ordered.
     *
     * @param ordered Whether the connection is ordered.
     * @return The connection context builder.
     */
    Builder setOrdered(boolean ordered);

    /**
     * Sets whether the connection is at-least-once.
     *
     * @param atLeastOnce Whether the connection is at-least-once.
     * @return The connection context builder.
     */
    Builder setAtLeastOnce(boolean atLeastOnce);

    /**
     * Sets the parent output port context.
     *
     * @param port The parent output port context.
     * @return The output connection context builder.
     */
    Builder setPort(OutputPortContext port);

    /**
     * Sets the send timeout of the output port.
     *
     * @param timeout The send timeout of the output port.
     * @return The output connection context builder.
     */
    Builder setSendTimeout(long timeout);
  }

}
