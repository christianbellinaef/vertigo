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
package net.kuujo.vertigo.context;

import io.vertx.codegen.annotations.VertxGen;
import net.kuujo.vertigo.context.impl.OutputContextImpl;

import java.util.Collection;

/**
 * Output context is a wrapper around output port information for
 * a single component partition.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface OutputContext extends TypeContext<OutputContext> {

  /**
   * Returns a new output context builder.
   *
   * @return A new output context builder.
   */
  static Builder builder() {
    return new OutputContextImpl.Builder();
  }

  /**
   * Returns a new output context builder.
   *
   * @param output An existing output context object to wrap.
   * @return An output context builder wrapper.
   */
  static Builder builder(OutputContext output) {
    return new OutputContextImpl.Builder((OutputContextImpl) output);
  }

  /**
   * Returns the parent component context.
   *
   * @return The parent component context.
   */
  ComponentContext component();

  /**
   * Returns the output's port contexts.
   *
   * @return A collection of output port contexts.
   */
  Collection<OutputPortContext> ports();

  /**
   * Returns the output port context for a given port.
   *
   * @param name The name of the port to return.
   * @return The output port context.
   */
  OutputPortContext port(String name);

  /**
   * Output context builder.
   */
  public static interface Builder extends TypeContext.Builder<Builder, OutputContext> {

    /**
     * Adds an output port.
     *
     * @param port The output port context.
     * @return The output context builder.
     */
    Builder addPort(OutputPortContext port);

    /**
     * Removes an output port.
     *
     * @param port The output port context.
     * @return The output context builder.
     */
    Builder removePort(OutputPortContext port);

    /**
     * Sets all output ports.
     *
     * @param ports A collection of output port context.
     * @return The output context builder.
     */
    Builder setPorts(OutputPortContext... ports);

    /**
     * Sets all output ports.
     *
     * @param ports A collection of output port context.
     * @return The output context builder.
     */
    Builder setPorts(Collection<OutputPortContext> ports);

    /**
     * Clears all output ports.
     *
     * @return The output context builder.
     */
    Builder clearPorts();

    /**
     * Sets the parent component context.
     *
     * @param component The parent component context.
     * @return The output context builder.
     */
    Builder setComponent(ComponentContext component);
  }

}
