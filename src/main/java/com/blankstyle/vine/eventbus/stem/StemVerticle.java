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
package com.blankstyle.vine.eventbus.stem;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import com.blankstyle.vine.context.JsonStemContext;
import com.blankstyle.vine.context.StemContext;
import com.blankstyle.vine.eventbus.CommandDispatcher;
import com.blankstyle.vine.eventbus.DefaultCommandDispatcher;
import com.blankstyle.vine.eventbus.JsonCommand;
import com.blankstyle.vine.eventbus.stem.actions.Assign;
import com.blankstyle.vine.eventbus.stem.actions.Release;

/**
 * A Vine stem verticle.
 *
 * @author Jordan Halterman
 */
public class StemVerticle extends BusModBase implements Handler<Message<JsonObject>> {

  private StemContext context;

  private CommandDispatcher dispatcher = new DefaultCommandDispatcher() {{
    registerAction(Assign.NAME, Assign.class);
    registerAction(Release.NAME, Release.class);
  }};

  @Override
  public void start() {
    context = new JsonStemContext(container.config());
    dispatcher.setEventBus(vertx.eventBus());
    dispatcher.setContext(context);
    vertx.eventBus().registerHandler(context.getAddress(), this);
  }

  @Override
  public void handle(final Message<JsonObject> message) {
    dispatcher.dispatch(new JsonCommand(message.body()), new Handler<AsyncResult<Object>>() {
      @Override
      public void handle(AsyncResult<Object> result) {
        if (result.succeeded()) {
          message.reply(result.result());
        }
        else {
          message.reply(result.cause());
        }
      }
    });
  }

}