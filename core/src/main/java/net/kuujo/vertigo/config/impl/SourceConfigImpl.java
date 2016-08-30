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
package net.kuujo.vertigo.config.impl;

import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.config.SourceConfig;
import net.kuujo.vertigo.config.OutputPortConfig;

/**
 * Connection source options.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class SourceConfigImpl extends EndpointConfigImpl<SourceConfig> implements SourceConfig {

  public SourceConfigImpl() {
    super();
  }

  public SourceConfigImpl(SourceConfig source) {
    super(source);
  }

  public SourceConfigImpl(OutputPortConfig port) {
    super(port.getComponent().getName(), port.getName());
  }

  public SourceConfigImpl(JsonObject source) {
    super(source);
  }

}
