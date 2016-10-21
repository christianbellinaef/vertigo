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
package net.kuujo.vertigo.cluster.impl;

import io.vertx.core.Vertx;
import net.kuujo.vertigo.cluster.Cluster;
import net.kuujo.vertigo.cluster.ClusterOptions;
import net.kuujo.vertigo.spi.ClusterFactory;

/**
 * Remote cluster factory implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class ClusterFactoryImpl implements ClusterFactory {

  @Override
  public Cluster createCluster(Vertx vertx, ClusterOptions options) {
    return null;
  }

  @Override
  public Cluster createClusterProxy(Vertx vertx, String address) {
    return null;
  }

}
