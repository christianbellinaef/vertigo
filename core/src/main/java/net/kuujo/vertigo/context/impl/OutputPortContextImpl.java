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
import net.kuujo.vertigo.context.OutputContext;
import net.kuujo.vertigo.context.OutputConnectionContext;
import net.kuujo.vertigo.context.OutputPortContext;
import net.kuujo.vertigo.util.Args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Output port context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class OutputPortContextImpl extends BasePortContextImpl<OutputPortContext, OutputConnectionContext> implements OutputPortContext {
  private OutputContext output;

  @Override
  public OutputContext output() {
    return output;
  }

  /**
   * Output port context builder.
   */
  public static class Builder implements OutputPortContext.Builder {
    private final OutputPortContextImpl port;

    public Builder() {
      port = new OutputPortContextImpl();
    }

    public Builder(OutputPortContextImpl port) {
      this.port = port != null ? port : new OutputPortContextImpl();
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
    public Builder addConnection(OutputConnectionContext connection) {
      Args.checkNotNull(connection, "connection cannot be null");
      port.connections.add(connection);
      return this;
    }

    @Override
    public Builder removeConnection(OutputConnectionContext connection) {
      Args.checkNotNull(connection, "connection cannot be null");
      port.connections.remove(connection);
      return this;
    }

    @Override
    public Builder setConnections(OutputConnectionContext... connections) {
      port.connections = new ArrayList<>(Arrays.asList(connections));
      return this;
    }

    @Override
    public Builder setConnections(Collection<OutputConnectionContext> connections) {
      Args.checkNotNull(connections, "connections cannot be null");
      port.connections = new ArrayList<>(connections);
      return this;
    }

    @Override
    public Builder setOutput(OutputContext output) {
      Args.checkNotNull(output, "output cannot be null");
      port.output = output;
      return this;
    }

    @Override
    public OutputPortContextImpl build() {
      return port;
    }
  }

}
