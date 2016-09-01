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
package net.kuujo.vertigo.network.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.network.*;
import net.kuujo.vertigo.network.NetworkConfig;
import net.kuujo.vertigo.util.Args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * NetworkConfig implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class NetworkImpl implements NetworkConfig {
  private String name;
  private final Collection<ComponentConfig> components = new ArrayList<>();
  private final Collection<ConnectionConfig> connections = new ArrayList<>();

  public NetworkImpl() {
    this(UUID.randomUUID().toString());
  }

  public NetworkImpl(String name) {
    this.name = name;
  }

  public NetworkImpl(JsonObject network) {
    update(network);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public NetworkConfig setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Collection<ComponentConfig> getComponents() {
    return components;
  }

  @Override
  public ComponentConfig getComponent(String name) {
    for (ComponentConfig component : components) {
      if (component.getName().equals(name)) {
        return component;
      }
    }
    return null;
  }

  @Override
  public boolean hasComponent(String name) {
    for (ComponentConfig component : components) {
      if (component.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ComponentConfig addComponent(String name) {
    ComponentConfig component = NetworkConfig.component(name).setNetwork(this);
    components.add(component);
    return component;
  }

  @Override
  public ComponentConfig addComponent(ComponentConfig component) {
    components.add(NetworkConfig.component(component).setNetwork(this));
    return component;
  }

  @Override
  public ComponentConfig removeComponent(String name) {
    Iterator<ComponentConfig> iterator = components.iterator();
    while (iterator.hasNext()) {
      ComponentConfig component = iterator.next();
      if (component.getName() != null && component.getName().equals(name)) {
        iterator.remove();
        return component;
      }
    }
    return null;
  }

  @Override
  public ComponentConfig removeComponent(ComponentConfig component) {
    Iterator<ComponentConfig> iterator = components.iterator();
    while (iterator.hasNext()) {
      ComponentConfig info = iterator.next();
      if (info.equals(component)) {
        iterator.remove();
        return component;
      }
    }
    return null;
  }

  @Override
  public Collection<ConnectionConfig> getConnections() {
    return connections;
  }

  @Override
  public ConnectionConfig createConnection(ConnectionConfig connection) {
    connections.add(NetworkConfig.connection(connection));
    return connection;
  }

  @Override
  public ConnectionConfig createConnection(OutputPortConfig output, InputPortConfig input) {
    ConnectionConfig connection = NetworkConfig.connection(output, input);
    connections.add(connection);
    return connection;
  }

  @Override
  public ConnectionConfig destroyConnection(ConnectionConfig connection) {
    Iterator<ConnectionConfig> iterator = connections.iterator();
    while (iterator.hasNext()) {
      ConnectionConfig c = iterator.next();
      if (c.equals(connection)) {
        iterator.remove();
        return c;
      }
    }
    return null;
  }

  @Override
  public void update(JsonObject network) {
    this.name = Args.checkNotNull(network.getString(NETWORK_NAME));
    JsonObject components = network.getJsonObject(NETWORK_COMPONENTS);
    if (components != null) {
      for (String name : components.fieldNames()) {
        this.components.add(NetworkConfig.component(components.getJsonObject(name)).setName(name).setNetwork(this));
      }
    }
    JsonArray connections = network.getJsonArray(NETWORK_CONNECTIONS);
    if (connections != null) {
      for (Object connection : connections) {
        this.connections.add(NetworkConfig.connection((JsonObject) connection));
      }
    }
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(NETWORK_NAME, name);
    JsonObject components = new JsonObject();
    for (ComponentConfig component : this.components) {
      components.put(component.getName(), component.toJson());
    }
    json.put(NETWORK_COMPONENTS, components);
    JsonArray connections = new JsonArray();
    for (ConnectionConfig connection : this.connections) {
      connections.add(connection.toJson());
    }
    json.put(NETWORK_CONNECTIONS, connections);
    return json;
  }

}
