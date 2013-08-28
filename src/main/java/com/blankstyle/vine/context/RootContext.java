/*
* Copyright 2013 the original author or authors.
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
package com.blankstyle.vine.context;

import org.vertx.java.core.json.JsonObject;

/**
 * A JSON object-based root context.
 *
 * @author Jordan Halterman
 */
public class RootContext implements Context {

  private static final String DEFAULT_ADDRESS = "vine.root";

  private JsonObject context = new JsonObject();

  public RootContext() {
  }

  public RootContext(String name) {
    context.putString("name", name);
  }

  public RootContext(JsonObject json) {
    context = json;
  }

  /**
   * Returns the root address.
   */
  public String getAddress() {
    return context.getString("address", DEFAULT_ADDRESS);
  }

  @Override
  public JsonObject serialize() {
    return context;
  }

}
