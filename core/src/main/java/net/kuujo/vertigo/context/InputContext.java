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
import net.kuujo.vertigo.context.impl.InputContextImpl;

import java.util.Collection;

/**
 * Input context is a wrapper around input port information for
 * a single component partition.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface InputContext extends TypeContext<InputContext> {

  /**
   * Returns a new input context builder.
   *
   * @return A new input context builder.
   */
  static Builder builder() {
    return new InputContextImpl.Builder();
  }

  /**
   * Returns a new input context builder.
   *
   * @param input An existing input context object to wrap.
   * @return An input context builder wrapper.
   */
  static Builder builder(InputContext input) {
    return new InputContextImpl.Builder((InputContextImpl) input);
  }

  /**
   * Returns the parent component context.
   *
   * @return The parent component context.
   */
  ComponentContext component();

  /**
   * Returns the input's port contexts.
   *
   * @return A collection of input port contexts.
   */
  Collection<InputPortContext> ports();

  /**
   * Returns the input port context for a given port.
   *
   * @param name The name of the port to return.
   * @return The input port context.
   */
  InputPortContext port(String name);

  /**
   * Input context builder.
   */
  public static interface Builder extends TypeContext.Builder<Builder, InputContext> {

    /**
     * Adds an input port.
     *
     * @param port The input port context.
     * @return The input context builder.
     */
    Builder addPort(InputPortContext port);

    /**
     * Removes an input port.
     *
     * @param port The input port context.
     * @return The input context builder.
     */
    Builder removePort(InputPortContext port);

    /**
     * Sets all input ports.
     *
     * @param ports A collection of input port context.
     * @return The input context builder.
     */
    Builder setPorts(InputPortContext... ports);

    /**
     * Sets all input ports.
     *
     * @param ports A collection of input port context.
     * @return The input context builder.
     */
    Builder setPorts(Collection<InputPortContext> ports);

    /**
     * Clears all input ports.
     *
     * @return The input context builder.
     */
    Builder clearPorts();

    /**
     * Sets the parent component context.
     *
     * @param component The parent component context.
     * @return The input context builder.
     */
    Builder setComponent(ComponentContext component);
  }

}
