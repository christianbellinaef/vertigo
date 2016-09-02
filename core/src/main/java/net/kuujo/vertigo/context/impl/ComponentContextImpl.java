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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.context.ComponentContext;
import net.kuujo.vertigo.context.InputContext;
import net.kuujo.vertigo.context.OutputContext;
import net.kuujo.vertigo.context.NetworkContext;
import net.kuujo.vertigo.util.Args;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Component context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComponentContextImpl extends BaseContextImpl<ComponentContext> implements ComponentContext {
  private String id;
  private String address;
  private String main;
  private JsonObject config;
  private boolean worker;
  private boolean multiThreaded;
//  private boolean stateful;
  private int replicas;
  private InputContext input;
  private OutputContext output;
//  private Set<String> resources = new HashSet<>();
//  private NetworkContext network;

  @Override
  public String name() {
    return id;
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public String main() {
    return main;
  }

  @Override
  public JsonObject config() {
    return config;
  }

  @Override
  public boolean worker() {
    return worker;
  }

  @Override
  public boolean multiThreaded() {
    return multiThreaded;
  }

//  @Override
//  public boolean stateful() {
//    return stateful;
//  }

  @Override
  public int replicas() {
    return replicas;
  }

  @Override
  public InputContext input() {
    return input;
  }

  @Override
  public OutputContext output() {
    return output;
  }

//  @Override
//  public Set<String> resources() {
//    return resources;
//  }

//  @Override
//  public NetworkContext network() {
//    return network;
//  }

  /**
   * Component context builder.
   */
  public static class Builder implements ComponentContext.Builder {
    private final ComponentContextImpl component;

    public Builder() {
      component = new ComponentContextImpl();
    }

    public Builder(ComponentContextImpl component) {
      this.component = component;
    }

    @Override
    public ComponentContext.Builder setName(String name) {
      Args.checkNotNull(name, "name cannot be null");
      component.id = name;
      return this;
    }

    @Override
    public ComponentContext.Builder setAddress(String address) {
      component.address = Args.checkNotNull(address, "address cannot be null");
      return this;
    }

    @Override
    public Builder setIdentifier(String identifier) {
      Args.checkNotNull(identifier, "identifier cannot be null");
      component.main = identifier;
      return this;
    }

    @Override
    public Builder setConfig(JsonObject config) {
      component.config = config;
      return this;
    }

    @Override
    public Builder setWorker(boolean isWorker) {
      component.worker = isWorker;
      return this;
    }

    @Override
    public Builder setMultiThreaded(boolean isMultiThreaded) {
      component.multiThreaded = isMultiThreaded;
      return this;
    }

//    @Override
//    public ComponentContext.Builder setStateful(boolean isStateful) {
//      component.stateful = isStateful;
//      return this;
//    }

    @Override
    public ComponentContext.Builder setReplicas(int replicas) {
      component.replicas = Args.checkPositive(replicas, "replicas must be a positive integer");
      return this;
    }

    @Override
    public ComponentContext.Builder setInput(InputContext input) {
      component.input = Args.checkNotNull(input, "input cannot be null");
      return this;
    }

    @Override
    public ComponentContext.Builder setOutput(OutputContext output) {
      component.output = Args.checkNotNull(output, "output cannot be null");
      return this;
    }

//    @Override
//    public Builder addResource(String resource) {
//      component.resources.add(resource);
//      return this;
//    }

//    @Override
//    public Builder removeResource(String resource) {
//      component.resources.remove(resource);
//      return this;
//    }

//    @Override
//    public Builder setResources(String... resources) {
//      component.resources = new HashSet<>(Arrays.asList(resources));
//      return this;
//    }

//    @Override
//    public Builder setResources(Collection<String> resources) {
//      component.resources = new HashSet<>(resources);
//      return this;
//    }

//    @Override
//    public Builder setNetwork(NetworkContext network) {
//      Args.checkNotNull(network, "network cannot be null");
//      component.network = network;
//      return this;
//    }

    /**
     * Checks all fields in the constructed component.
     */
    private void checkFields() {
      Args.checkNotNull(component.id, "name cannot be null");
      Args.checkNotNull(component.address, "address cannot be null");
    }

    @Override
    public ComponentContextImpl build() {
      checkFields();
      return component;
    }
  }

}
