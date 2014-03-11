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
package net.kuujo.vertigo.input;

import net.kuujo.vertigo.context.InputStreamContext;
import net.kuujo.vertigo.message.JsonMessage;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 * An input stream.
 *
 * @author Jordan Halterman
 */
public interface InputStream {

  /**
   * Returns the input stream context.
   *
   * @return The input stream context.
   */
  InputStreamContext context();

  /**
   * Registers a message handler on the stream.
   *
   * @param message A message handler.
   * @return The input stream.
   */
  InputStream messageHandler(Handler<JsonMessage> message);

  /**
   * Starts the input stream.
   *
   * @return The input stream.
   */
  InputStream start();

  /**
   * Starts the input stream.
   *
   * @param doneHandler An asynchronous handler to be called once started.
   * @return The input stream.
   */
  InputStream start(Handler<AsyncResult<Void>> doneHandler);

  /**
   * Stops the input stream.
   */
  void stop();

  /**
   * Stops the input stream.
   *
   * @param doneHandler An asynchronous handler to be called once stopped.
   */
  void stop(Handler<AsyncResult<Void>> doneHandler);

}
