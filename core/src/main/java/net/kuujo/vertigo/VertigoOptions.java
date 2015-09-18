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
package net.kuujo.vertigo;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import net.kuujo.vertigo.util.Configs;

/**
 * Vertigo options.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@DataObject
public class VertigoOptions extends VertxOptions {
  private String clusterAddress;

  public VertigoOptions() {
  }

  public VertigoOptions(VertigoOptions options) {
    super(options);
  }

  public VertigoOptions(JsonObject options) {
    super(options);
    options.mergeIn(Configs.load());
  }

  /**
   * Sets the Vertigo cluster address.
   *
   * @param address The Vertigo cluster address.
   * @return The Vertigo options.
   */
  public VertigoOptions setClusterAddress(String address) {
    this.clusterAddress = address;
    return this;
  }

  /**
   * Returns the Vertigo cluster address.
   *
   * @return The Vertigo cluster address.
   */
  public String getClusterAddress() {
    return clusterAddress;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = 37 * hashCode + super.hashCode();
    hashCode = 37 * hashCode + clusterAddress.hashCode();
    return hashCode;
  }

}
