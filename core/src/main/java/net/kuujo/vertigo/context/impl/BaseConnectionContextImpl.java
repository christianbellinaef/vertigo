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

import net.kuujo.vertigo.context.ConnectionContext;
import net.kuujo.vertigo.context.SourceContext;
import net.kuujo.vertigo.context.TargetContext;
import net.kuujo.vertigo.context.PortContext;

/**
 * Connection context implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class BaseConnectionContextImpl<T extends ConnectionContext<T, U>, U extends PortContext<U, T>> extends BaseContextImpl<T> implements ConnectionContext<T,  U> {
  protected SourceContext source;
  protected TargetContext target;
//  protected boolean ordered;
//  protected boolean atLeastOnce;
  protected long sendTimeout;
  protected U port;

  @Override
  public SourceContext source() {
    return source;
  }

  @Override
  public TargetContext target() {
    return target;
  }

//  @Override
//  public boolean ordered() {
//    return ordered;
//  }
//
//  @Override
//  public boolean atLeastOnce() {
//    return atLeastOnce;
//  }

  @Override
  public long sendTimeout() {
    return sendTimeout;
  }

  @Override
  public U port() {
    return port;
  }

}
