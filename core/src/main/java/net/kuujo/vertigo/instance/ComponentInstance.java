/*
 * Copyright 2013-2014 the original author or authors.
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
package net.kuujo.vertigo.instance;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import net.kuujo.vertigo.context.ComponentContext;

/**
 * The component is the primary unit of processing in Vertigo. Vertigo
 * networks consist of any number of components that are arbitrarily
 * connected to one another and communicate with one another using
 * message passing through internally-defined input and output ports.<p>
 *
 * Components receive messages on input ports and send messages on output
 * ports, but component-to-component relationships are defined externally
 * to the component implementation in the {@link net.kuujo.vertigo.network.builder.NetworkBuilder}. This means
 * that when a message is sent to an output port, the component doesn't
 * know where the message will go. Instead, Vertigo handles message routing
 * internally, abstracting relationship details from component implementations.<p>
 *
 * Each component may define any number of input and output ports. Ports are
 * created lazily by simply referencing them in {@link InputCollector} and
 * {@link OutputCollector} interfaces. If a message is sent to a port that
 * has no connections then the message will simply disappear.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface ComponentInstance {

  /**
   * Gets the component Vertx partition.
   *
   * @return The component Vertx partition.
   */
  Vertx vertx();

  /**
   * Returns the component context.
   *
   * The component context can be used to retrieve useful information about an
   * entire network.
   *
   * @return The component context.
   */
  ComponentContext context();

  /**
   * Returns the component's {@link InputCollector}. This is the element of the
   * component which provides an interface for receiving messages from other components.
   *
   * @return The components {@link InputCollector}.
   */
  InputCollector input();

  /**
   * Returns the component's {@link OutputCollector}. This is the element of the
   * component which provides an interface for sending messages to other components.
   *
   * @return The component's {@link OutputCollector}.
   */
  OutputCollector output();

//  /**
//   * Returns the partition logger. This is a special logger that references the
//   * Vertigo component partition and can allow for fine grained control of logging
//   * within Vertigo components.
//   *
//   * @return The logger for the component partition.
//   */
//  Logger logger();

//  /**
//   * Returns the component state.
//   *
//   * @return The component state.
//   */
//  JsonObject state();
//
//  /**
//   * Performs a checkpoint of the component state.
//   *
//   * @param handler A handler to be called with the component state.
//   * @return The component instance.
//   */
//  @Fluent
//  ComponentInstance checkpoint(Handler<JsonObject> handler);
//
//  /**
//   * Recovers the component state.
//   *
//   * @param handler A handler to be called with the component state.
//   * @return The component instance.
//   */
//  @Fluent
//  ComponentInstance recover(Handler<JsonObject> handler);

  /**
   * Starts the component.
   *
   * @return The component partition.
   */
  @Fluent
  ComponentInstance start();

  /**
   * Starts the component.
   *
   * @param doneHandler An asynchronous handler to be called once the component is started.
   * @return The component partition.
   */
  @Fluent
  ComponentInstance start(Handler<AsyncResult<Void>> doneHandler);

  /**
   * Stops the component.
   */
  void stop();

  /**
   * Stops the component.
   *
   * @param doneHandler An asynchronous handler to be called once the component is stopped.
   */
  void stop(Handler<AsyncResult<Void>> doneHandler);

}
