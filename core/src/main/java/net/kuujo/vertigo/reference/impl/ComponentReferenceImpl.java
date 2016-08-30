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
package net.kuujo.vertigo.reference.impl;

import io.vertx.core.Vertx;
import net.kuujo.vertigo.context.ComponentContext;
import net.kuujo.vertigo.reference.ComponentReference;
import net.kuujo.vertigo.reference.InputReference;
import net.kuujo.vertigo.reference.OutputReference;

/**
 * Component reference implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class ComponentReferenceImpl implements ComponentReference {
  private final Vertx vertx;
  private final ComponentContext context;

  public ComponentReferenceImpl(Vertx vertx, ComponentContext context) {
    this.vertx = vertx;
    this.context = context;
  }

  @Override
  public InputReference input() {
    return new InputReferenceImpl(vertx, context.address());
  }

  @Override
  public OutputReference output() {
    return new OutputReferenceImpl(vertx, context.address());
  }

}
