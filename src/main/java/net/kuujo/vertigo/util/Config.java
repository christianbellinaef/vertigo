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
package net.kuujo.vertigo.util;

import net.kuujo.vertigo.cluster.ClusterClient;
import net.kuujo.vertigo.context.InstanceContext;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * Context utilities.
 *
 * @author Jordan Halterman
 */
public final class Config {

  /**
   * Builds a verticle configuration.
   *
   * @param context The verticle context.
   * @param cluster The verticle cluster.
   * @return A verticle configuration.
   */
  public static JsonObject buildConfig(InstanceContext context, ClusterClient cluster) {
    JsonObject config = new JsonObject();
    config.putObject("context", InstanceContext.toJson(context));
    config.putString("cluster", cluster.getClass().getName());
    return config;
  }

  /**
   * Parses a cluster client instance from a configuration object.
   *
   * @param config The configuration object.
   * @return A cluster client.
   */
  @SuppressWarnings("unchecked")
  public static ClusterClient parseCluster(JsonObject config, Vertx vertx, Container container) {
    String clusterType = config.getString("cluster");
    if (clusterType == null) {
      throw new IllegalArgumentException("No cluster class specified.");
    }
    Class<? extends ClusterClient> clusterClass;
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try {
      clusterClass = (Class<? extends ClusterClient>) loader.loadClass(clusterType);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Error instantiating serializer factory.");
    }
    return Factories.createObject(clusterClass, vertx, container);
  }

  /**
   * Parses an instance context from a configuration object.
   *
   * @param config The Json configuration object.
   * @return An instance context.
   */
  public static InstanceContext parseContext(JsonObject config) {
    if (config != null && config.containsField("context")) {
      return InstanceContext.fromJson(config.getObject("context"));
    }
    return null;
  }

  /**
   * Populates a configuration with the instance configuration.
   *
   * @param config The current configuration.
   * @param context The component instance context.
   * @return The updated configuration.
   */
  public static JsonObject populateConfig(JsonObject config, InstanceContext context) {
    for (String fieldName : config.getFieldNames()) {
      config.removeField(fieldName);
    }
    JsonObject realConfig = context.component().config();
    for (String fieldName : realConfig.getFieldNames()) {
      config.putValue(fieldName, realConfig.getValue(fieldName));
    }
    return config;
  }

}
