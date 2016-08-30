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

package net.kuujo.vertigo.context.impl;

import io.vertx.core.eventbus.MessageCodec;
import net.kuujo.vertigo.context.InputContext;
import net.kuujo.vertigo.context.InputConnectionContext;
import net.kuujo.vertigo.context.InputPortContext;
import net.kuujo.vertigo.util.Args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Input port context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class InputPortContextImpl extends BasePortContextImpl<InputPortContext, InputConnectionContext> implements InputPortContext {
  private InputContext input;

  @Override
  public InputContext input() {
    return input;
  }

  /**
   * Input port context builder.
   */
  public static class Builder implements InputPortContext.Builder {
    private final InputPortContextImpl port;

    public Builder() {
      port = new InputPortContextImpl();
    }

    public Builder(InputPortContextImpl port) {
      this.port = port != null ? port : new InputPortContextImpl();
    }

    @Override
    public Builder setName(String name) {
      Args.checkNotNull(name, "name cannot be null");
      port.name = name;
      return this;
    }

    @Override
    public Builder setType(Class<?> type) {
      port.type = Args.checkNotNull(type, "type cannot be null");
      return this;
    }

    @Override
    public Builder setCodec(Class<? extends MessageCodec> codec) {
      port.codec = codec;
      return this;
    }

    @Override
    public Builder setPersistent(boolean persistent) {
      port.persistent = persistent;
      return this;
    }

    @Override
    public Builder addConnection(InputConnectionContext connection) {
      Args.checkNotNull(connection, "connection cannot be null");
      port.connections.add(connection);
      return this;
    }

    @Override
    public Builder removeConnection(InputConnectionContext connection) {
      Args.checkNotNull(connection, "connection cannot be null");
      port.connections.remove(connection);
      return this;
    }

    @Override
    public Builder setConnections(InputConnectionContext... connections) {
      port.connections = new ArrayList<>(Arrays.asList(connections));
      return this;
    }

    @Override
    public Builder setConnections(Collection<InputConnectionContext> connections) {
      Args.checkNotNull(connections, "connections cannot be null");
      port.connections = new ArrayList<>(connections);
      return this;
    }

    @Override
    public Builder setInput(InputContext input) {
      Args.checkNotNull(input, "input cannot be null");
      port.input = input;
      return this;
    }

    @Override
    public InputPortContextImpl build() {
      return port;
    }
  }

}
