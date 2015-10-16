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
package net.kuujo.vertigo.builder.impl;

import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.builder.*;
import net.kuujo.vertigo.io.connection.ConnectionConfig;

import java.util.Collection;

/**
 * Connection source component builder implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class ConnectionSourceComponentBuilderImpl implements ConnectionSourceComponentBuilder {
  private final NetworkBuilderImpl network;
  private final ComponentBuilder component;
  private final Collection<ConnectionConfig> connections;
  private final ConnectionConfig connection;

  public ConnectionSourceComponentBuilderImpl(NetworkBuilderImpl network, ComponentBuilder component, Collection<ConnectionConfig> connections, ConnectionConfig connection) {
    this.network = network;
    this.component = component;
    this.connections = connections;
    this.connection = connection;
  }

  @Override
  public ConnectionSourceComponentBuilder identifier(String identifier) {
    component.identifier(identifier);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder config(JsonObject config) {
    component.config(config);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder worker() {
    component.worker();
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder worker(boolean worker) {
    component.worker(worker);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder multiThreaded() {
    component.multiThreaded();
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder multiThreaded(boolean multiThreaded) {
    component.multiThreaded(multiThreaded);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder stateful() {
    component.stateful();
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder stateful(boolean stateful) {
    component.stateful(stateful);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder replicas(int replicas) {
    component.replicas(replicas);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder port(String port) {
    connection.getSource().setPort(port);
    network.component(connection.getSource().getComponent()).output().port(port);
    return this;
  }

  @Override
  public ConnectionSourceComponentBuilder sendTimeout(long timeout) {
    connection.setSendTimeout(timeout);
    return this;
  }

  @Override
  public ConnectionSourceBuilder and() {
    return new ConnectionSourceBuilderImpl(network, connections);
  }

  @Override
  public ConnectionSourceComponentBuilder and(String component) {
    return and().component(component);
  }

  @Override
  public ConnectionTargetBuilder to() {
    return new InitialConnectionTargetBuilderImpl(network, connections);
  }

  @Override
  public ConnectionTargetComponentBuilder to(String component) {
    return to().component(component);
  }

  @Override
  public ComponentBuilder component() {
    return network.component();
  }

  @Override
  public ComponentBuilder component(String name) {
    return network.component(name);
  }

  @Override
  public ConnectionSourceBuilder connect() {
    return network.connect();
  }

  @Override
  public ConnectionSourceComponentBuilder connect(String component) {
    return network.connect(component);
  }
}
