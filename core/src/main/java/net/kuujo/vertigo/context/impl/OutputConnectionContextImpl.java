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

import net.kuujo.vertigo.context.OutputConnectionContext;
import net.kuujo.vertigo.context.SourceContext;
import net.kuujo.vertigo.context.TargetContext;
import net.kuujo.vertigo.context.OutputPortContext;
import net.kuujo.vertigo.util.Args;

/**
 * Output connection context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class OutputConnectionContextImpl extends BaseConnectionContextImpl<OutputConnectionContext, OutputPortContext> implements OutputConnectionContext {

  /**
   * Output connection context builder.
   */
  public static class Builder implements OutputConnectionContext.Builder {
    private OutputConnectionContextImpl connection;

    public Builder() {
      connection = new OutputConnectionContextImpl();
    }

    public Builder(OutputConnectionContextImpl connection) {
      this.connection = connection;
    }

    @Override
    public Builder setSource(SourceContext source) {
      Args.checkNotNull(source, "source cannot be null");
      connection.source = source;
      return this;
    }

    @Override
    public Builder setTarget(TargetContext target) {
      Args.checkNotNull(target, "target cannot be null");
      connection.target = target;
      return this;
    }

    @Override
    public Builder setOrdered(boolean ordered) {
      connection.ordered = ordered;
      return this;
    }

    @Override
    public Builder setAtLeastOnce(boolean atLeastOnce) {
      connection.atLeastOnce = atLeastOnce;
      return this;
    }

    @Override
    public Builder setPort(OutputPortContext port) {
      Args.checkNotNull(port, "port cannot be null");
      connection.port = port;
      return this;
    }

    @Override
    public OutputConnectionContext.Builder setSendTimeout(long timeout) {
      connection.sendTimeout = timeout;
      return this;
    }

    @Override
    public OutputConnectionContextImpl build() {
      return connection;
    }
  }

}
