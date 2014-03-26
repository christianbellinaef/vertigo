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
package net.kuujo.vertigo.network;

import net.kuujo.vertigo.input.grouping.Grouping;

/**
 * Connection configuration.
 *
 * @author Jordan Halterman
 */
public interface ConnectionConfig extends Config {

  /**
   * Returns the connection source.
   *
   * @return The connection source info.
   */
  Source getSource();

  /**
   * Returns the connection target.
   *
   * @return The connection target info.
   */
  Target getTarget();

  /**
   * Returns the connection grouping.
   *
   * @return The connection grouping. Defaults to round grouping if no grouping
   *         was set on the connection.
   */
  Grouping getGrouping();

  /**
   * Sets the connection grouping.
   *
   * @param grouping The connection grouping.
   * @return The connection configuration.
   */
  ConnectionConfig groupBy(Grouping grouping);

  /**
   * Sets a random connection grouping on the input.
   * 
   * @return The connection instance.
   */
  ConnectionConfig randomGrouping();

  /**
   * Sets a round-robin connection grouping on the input.
   * 
   * @return The connection instance.
   */
  ConnectionConfig roundGrouping();

  /**
   * Sets a fields grouping on the connection.
   * 
   * @param fields The fields on which to hash.
   * @return The connection instance.
   */
  ConnectionConfig fieldsGrouping(String... fields);

  /**
   * Sets an all grouping on the connection.
   * 
   * @return The connection instance.
   */
  ConnectionConfig allGrouping();

  /**
   * Connection source.
   *
   * @author Jordan Halterman
   */
  public static interface Source extends Config {

    /**
     * Returns the connection source component.
     *
     * @return The source component name.
     */
    String getComponent();

    /**
     * Sets the connection source component.
     *
     * @param component The connection source component.
     * @return The source instance.
     */
    Source setComponent(String component);

    /**
     * Returns the connection source port.
     *
     * @return The connection source port.
     */
    String getPort();

    /**
     * Sets the connection source port.
     *
     * @param port The connection source port.
     * @return The source instance.
     */
    Source setPort(String port);

  }

  /**
   * Connection target.
   *
   * @author Jordan Halterman
   */
  public static interface Target extends Config {

    /**
     * Returns the connection target component.
     *
     * @return The target component name.
     */
    String getComponent();

    /**
     * Sets the connection target component.
     *
     * @param component The connection target component.
     * @return The target instance.
     */
    Target setComponent(String component);

    /**
     * Returns the connection target port.
     *
     * @return The connection target port.
     */
    String getPort();

    /**
     * Sets the connection target port.
     *
     * @param port The connection target port.
     * @return The target instance.
     */
    Target setPort(String port);

  }

}