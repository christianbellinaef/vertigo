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

import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.context.ComponentContext;
import net.kuujo.vertigo.context.InputContext;
import net.kuujo.vertigo.context.InputPortContext;
import net.kuujo.vertigo.context.OutputPortContext;
import net.kuujo.vertigo.util.Args;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Input context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class InputContextImpl extends BaseContextImpl<InputContext> implements InputContext {
  private ComponentContext component;
  private Map<String, InputPortContext> ports = new HashMap<>();

  @Override
  public ComponentContext component() {
    return component;
  }

  @Override
  public Collection<InputPortContext> ports() {
    return ports.values();
  }

  @Override
  public InputPortContext port(String name) {
    return ports.get(name);
  }


  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ports.forEach((key, port) -> {
      json.put(key, port.toJson());
    });
    return json;
  }

  /**
   * Input context builder.
   */
  public static class Builder implements InputContext.Builder {
    private final InputContextImpl input;

    public Builder() {
      input = new InputContextImpl();
    }

    public Builder(InputContextImpl input) {
      this.input = input;
    }

    @Override
    public Builder addPort(InputPortContext port) {
      Args.checkNotNull(port, "port cannot be null");
      input.ports.put(port.name(), port);
      return this;
    }

    @Override
    public Builder removePort(InputPortContext port) {
      Args.checkNotNull(port, "port cannot be null");
      input.ports.remove(port.name());
      return this;
    }

    @Override
    public Builder setPorts(InputPortContext... ports) {
      input.ports.clear();
      for (InputPortContext port : ports) {
        input.ports.put(port.name(), port);
      }
      return this;
    }

    @Override
    public Builder setPorts(Collection<InputPortContext> ports) {
      Args.checkNotNull(ports, "ports cannot be null");
      input.ports.clear();
      for (InputPortContext port : ports) {
        input.ports.put(port.name(), port);
      }
      return this;
    }

    @Override
    public Builder clearPorts() {
      input.ports.clear();
      return this;
    }

    @Override
    public Builder setComponent(ComponentContext component) {
      Args.checkNotNull(component, "component cannot be null");
      input.component = component;
      return this;
    }

    @Override
    public Builder update(JsonObject config) {
      config.forEach(value -> {
        InputPortContext port = InputPortContext
            .builder()
            .setInput(input)
            .update((JsonObject)value.getValue())
            .build();
        input.ports.put(value.getKey(), port);
      });
      return this;
    }

    @Override
    public InputContextImpl build() {
      return input;
    }
  }

}
