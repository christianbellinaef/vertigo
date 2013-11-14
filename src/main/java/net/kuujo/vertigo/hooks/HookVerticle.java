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
package net.kuujo.vertigo.hooks;

import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * A hook verticle.
 *
 * This verticle should be extended by verticles that are intended to
 * be deployed as a {@link VerticleHook} or {@link ModuleHook}. This
 * verticle implementation provides an interface for handling all hook
 * events available to a component hook. When started, the verticle will
 * automatically register a local handler on the event bus at a specific
 * address, the address to which the hooked component will send event
 * messages.
 *
 * @author Jordan Halterman
 */
public abstract class HookVerticle extends Verticle {

  @Override
  public void start(final Future<Void> future) {
    String address = container.config().getString("address");
    vertx.eventBus().registerLocalHandler(address, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        JsonObject body = message.body();
        if (body != null) {
          String event = body.getString("event");
          switch (event) {
            case "start":
              handleStart();
              break;
            case "receive":
              handleReceive(body.getString("id"));
              break;
            case "ack":
              handleAck(body.getString("id"));
              break;
            case "fail":
              handleFail(body.getString("id"));
              break;
            case "emit":
              handleEmit(body.getString("id"));
              break;
            case "acked":
              handleAcked(body.getString("id"));
              break;
            case "failed":
              handleFailed(body.getString("id"));
              break;
            case "timeout":
              handleTimeout(body.getString("id"));
              break;
            case "stop":
              handleStop();
              break;
          }
        }
      }
    });

    // Copy the actual verticle configuration to the top level of container.config().
    JsonObject config = container.config().getObject("config");
    for (String fieldName : config.getFieldNames()) {
      container.config().putValue(fieldName, config.getValue(fieldName));
    }
    container.config().removeField("config");
    super.start(future);
  }

  /**
   * Called when the hooks component is started.
   */
  protected abstract void handleStart();

  /**
   * Called when the hooked component receives a message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleReceive(String id);

  /**
   * Called when the hooked component acks a received message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleAck(String id);

  /**
   * Called when the hooked component fails a received message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleFail(String id);

  /**
   * Called when the hooked component emits a message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleEmit(String id);

  /**
   * Called when the hooked component receives an ack for an emitted message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleAcked(String id);

  /**
   * Called when the hooked component receives a failure for an emitted message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleFailed(String id);

  /**
   * Called when the hooked component receives a timeout for an emitted message.
   *
   * @param id
   *   The unique message identifier.
   */
  protected abstract void handleTimeout(String id);

  /**
   * Called when the hook component stops.
   */
  protected abstract void handleStop();

}
