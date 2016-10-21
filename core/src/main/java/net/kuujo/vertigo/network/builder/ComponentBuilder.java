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
package net.kuujo.vertigo.network.builder;

import io.vertx.codegen.annotations.VertxGen;

/**
 * Component builder.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface ComponentBuilder extends NetworkLikeBuilder<ComponentBuilder>, ComponentLikeBuilder<ComponentBuilder> {

  /**
   * Sets the unique component name.
   *
   * @param name The unique component name.
   * @return The component builder.
   */
  ComponentBuilder name(String name);

  /**
   * Returns the component input builder.
   *
   * @return The component input builder.
   */
  InputBuilder input();

  /**
   * Returns the component output builder.
   *
   * @return The component output builder.
   */
  OutputBuilder output();

}
