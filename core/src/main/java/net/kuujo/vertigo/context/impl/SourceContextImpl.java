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

import net.kuujo.vertigo.context.SourceContext;
import net.kuujo.vertigo.util.Args;

/**
 * Connection source context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class SourceContextImpl extends BaseContextImpl<SourceContext> implements SourceContext {
  private String component;
  private String port;
  private String address;

  @Override
  public String component() {
    return component;
  }

  @Override
  public String port() {
    return port;
  }

  @Override
  public String address() {
    return address;
  }

  /**
   * Source context builder.
   */
  public static class Builder implements SourceContext.Builder {
    private final SourceContextImpl source;

    public Builder() {
      source = new SourceContextImpl();
    }

    public Builder(SourceContextImpl source) {
      this.source = source;
    }

    @Override
    public Builder setComponent(String component) {
      source.component = component;
      return this;
    }

    @Override
    public Builder setPort(String port) {
      source.port = port;
      return this;
    }

    @Override
    public Builder setAddress(String address) {
      source.address = Args.checkNotNull(address, "address cannot be null");
      return this;
    }

    @Override
    public SourceContextImpl build() {
      return source;
    }
  }

}
