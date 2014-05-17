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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kuujo.vertigo.cluster.ClusterException;
import net.kuujo.vertigo.cluster.Node;
import net.kuujo.vertigo.component.ComponentConfig;
import net.kuujo.vertigo.component.ModuleConfig;
import net.kuujo.vertigo.network.ActiveNetwork;
import net.kuujo.vertigo.network.NetworkConfig;
import net.kuujo.vertigo.platform.PlatformManager;
import net.kuujo.vertigo.platform.impl.DefaultPlatformManager;
import net.kuujo.vertigo.util.CountingCompletionHandler;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * Default node client implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class DefaultNode implements Node {
  private static final long DEFAULT_REPLY_TIMEOUT = 30000;
  private final String address;
  private final Vertx vertx;
  private final PlatformManager platform;

  public DefaultNode(String address, Vertx vertx, Container container) {
    this.address = address;
    this.vertx = vertx;
    this.platform = new DefaultPlatformManager(vertx, container);
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public Node installModule(String moduleName) {
    return installModule(moduleName);
  }

  @Override
  public Node installModule(final String moduleName, final Handler<AsyncResult<Void>> doneHandler) {
    // First check if the module is already installed on the node.
    JsonObject message = new JsonObject()
        .putString("action", "installed")
        .putString("type", "module")
        .putString("module", moduleName);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getBoolean("result", false)) {
          new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
        } else {
          // If we've made it this far then the module is not already installed on the node.
          // First we need to zip up the module from the local platform.
          platform.zipModule(moduleName, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> result) {
              if (result.failed()) {
                new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
              } else {
                // now that the module has been zipped, open the zipped file.
                final String zipFile = result.result();
                vertx.fileSystem().open(zipFile, new Handler<AsyncResult<AsyncFile>>() {
                  @Override
                  public void handle(AsyncResult<AsyncFile> result) {
                    if (result.failed()) {
                      new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
                    } else {
                      // Send a message to the node telling it we're going to upload the module.
                      final AsyncFile file = result.result();
                      JsonObject message = new JsonObject()
                          .putString("action", "upload");
                      vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
                        @Override
                        public void handle(AsyncResult<Message<JsonObject>> result) {
                          if (result.failed()) {
                            new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
                          } else if (result.result().body().getString("status").equals("error")) {
                            new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
                          } else if (result.result().body().getString("status").equals("ok")) {
                            final String id = result.result().body().getString("id");
                            uploadFile(file, id, new Handler<AsyncResult<Void>>() {
                              @Override
                              public void handle(AsyncResult<Void> result) {
                                if (result.failed()) {
                                  new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
                                } else {
                                  // Now that the file has been uploaded we can tell the node to install the module.
                                  JsonObject message = new JsonObject()
                                      .putString("action", "install")
                                      .putString("type", "module")
                                      .putString("module", moduleName)
                                      .putString("upload", id);
                                  vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
                                    @Override
                                    public void handle(AsyncResult<Message<JsonObject>> result) {
                                      if (result.failed()) {
                                        new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
                                      } else if (result.result().body().getString("status").equals("error")) {
                                        new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
                                      } else if (result.result().body().getString("status").equals("ok")) {
                                        new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
                                      }
                                    }
                                  });
                                }
                              }
                            });
                          }
                        }
                      });
                    }
                  }
                });
              }
            }
          });
        }
      }
    });
    return this;
  }

  /**
   * Uploads a file.
   */
  private void uploadFile(final AsyncFile file, final String address, final Handler<AsyncResult<Void>> doneHandler) {
    uploadFile(file, 0, address, doneHandler);
  }

  private void uploadFile(final AsyncFile file, final long position, final String address, final Handler<AsyncResult<Void>> doneHandler) {
    Buffer buffer = new Buffer(4096);
    file.read(buffer, 0, position, 4096, new Handler<AsyncResult<Buffer>>() {
      @Override
      public void handle(AsyncResult<Buffer> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
        } else {
          final Buffer buffer = result.result();
          vertx.eventBus().sendWithTimeout(address, buffer, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<Message<JsonObject>> result) {
              if (result.failed()) {
                new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
              } else if (result.result().body().getString("status").equals("error")) {
                new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
              } else if (result.result().body().getString("status").equals("ok")) {
                if (buffer.length() > 0) {
                  uploadFile(file, position+buffer.length(), address, doneHandler);
                } else {
                  new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
                }
              }
            }
          });
        }
      }
    });
  }

  @Override
  public Node uninstallModule(String moduleName) {
    return uninstallModule(moduleName, null);
  }

  @Override
  public Node uninstallModule(String moduleName, final Handler<AsyncResult<Void>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "uninstall")
        .putString("type", "module")
        .putString("module", moduleName);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node deployModule(String moduleName) {
    return deployModule(moduleName, null, 1, null);
  }

  @Override
  public Node deployModule(String moduleName, JsonObject config) {
    return deployModule(moduleName, config, 1, null);
  }

  @Override
  public Node deployModule(String moduleName, int instances) {
    return deployModule(moduleName, null, instances, null);
  }

  @Override
  public Node deployModule(String moduleName, JsonObject config, int instances) {
    return deployModule(moduleName, config, instances, null);
  }

  @Override
  public Node deployModule(String moduleName, Handler<AsyncResult<String>> doneHandler) {
    return deployModule(moduleName, null, 1, doneHandler);
  }

  @Override
  public Node deployModule(String moduleName, JsonObject config, Handler<AsyncResult<String>> doneHandler) {
    return deployModule(moduleName, config, 1, doneHandler);
  }

  @Override
  public Node deployModule(String moduleName, int instances, Handler<AsyncResult<String>> doneHandler) {
    return deployModule(moduleName, null, instances, doneHandler);
  }

  @Override
  public Node deployModule(String moduleName, JsonObject config, int instances, final Handler<AsyncResult<String>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "deploy")
        .putString("type", "module")
        .putString("module", moduleName)
        .putObject("config", config != null ? config : new JsonObject())
        .putNumber("instances", instances);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<String>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<String>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<String>(result.result().body().getString("id")).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node deployVerticle(String main) {
    return deployVerticle(main, null, 1, null);
  }

  @Override
  public Node deployVerticle(String main, JsonObject config) {
    return deployVerticle(main, config, 1, null);
  }

  @Override
  public Node deployVerticle(String main, int instances) {
    return deployVerticle(main, null, instances, null);
  }

  @Override
  public Node deployVerticle(String main, JsonObject config, int instances) {
    return deployVerticle(main, config, instances, null);
  }

  @Override
  public Node deployVerticle(String main, Handler<AsyncResult<String>> doneHandler) {
    return deployVerticle(main, null, 1, doneHandler);
  }

  @Override
  public Node deployVerticle(String main, JsonObject config, Handler<AsyncResult<String>> doneHandler) {
    return deployVerticle(main, config, 1, doneHandler);
  }

  @Override
  public Node deployVerticle(String main, int instances, Handler<AsyncResult<String>> doneHandler) {
    return deployVerticle(main, null, instances, doneHandler);
  }

  @Override
  public Node deployVerticle(String main, JsonObject config, int instances, final Handler<AsyncResult<String>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "deploy")
        .putString("type", "verticle")
        .putString("main", main)
        .putObject("config", config != null ? config : new JsonObject())
        .putNumber("instances", instances);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<String>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<String>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<String>(result.result().body().getString("id")).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node deployWorkerVerticle(String main) {
    return deployWorkerVerticle(main, null, 1, false, null);
  }

  @Override
  public Node deployWorkerVerticle(String main, JsonObject config) {
    return deployWorkerVerticle(main, config, 1, false, null);
  }

  @Override
  public Node deployWorkerVerticle(String main, int instances) {
    return deployWorkerVerticle(main, null, instances, false, null);
  }

  @Override
  public Node deployWorkerVerticle(String main, JsonObject config, int instances, boolean multiThreaded) {
    return deployWorkerVerticle(main, config, instances, multiThreaded, null);
  }

  @Override
  public Node deployWorkerVerticle(String main, Handler<AsyncResult<String>> doneHandler) {
    return deployWorkerVerticle(main, null, 1, false, doneHandler);
  }

  @Override
  public Node deployWorkerVerticle(String main, JsonObject config, Handler<AsyncResult<String>> doneHandler) {
    return deployWorkerVerticle(main, config, 1, false, doneHandler);
  }

  @Override
  public Node deployWorkerVerticle(String main, int instances, Handler<AsyncResult<String>> doneHandler) {
    return deployWorkerVerticle(main, null, instances, false, doneHandler);
  }

  @Override
  public Node deployWorkerVerticle(String main, JsonObject config, int instances, boolean multiThreaded, final Handler<AsyncResult<String>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "deploy")
        .putString("type", "verticle")
        .putString("main", main)
        .putObject("config", config != null ? config : new JsonObject())
        .putNumber("instances", instances)
        .putBoolean("worker", true)
        .putBoolean("multi-threaded", multiThreaded);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<String>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<String>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<String>(result.result().body().getString("id")).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node undeployModule(String deploymentID) {
    return undeployModule(deploymentID, null);
  }

  @Override
  public Node undeployModule(String deploymentID, final Handler<AsyncResult<Void>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "undeploy")
        .putString("type", "module")
        .putString("id", deploymentID);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node undeployVerticle(String deploymentID) {
    return undeployVerticle(deploymentID, null);
  }

  @Override
  public Node undeployVerticle(String deploymentID, final Handler<AsyncResult<Void>> doneHandler) {
    JsonObject message = new JsonObject()
        .putString("action", "undeploy")
        .putString("type", "verticle")
        .putString("id", deploymentID);
    vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
      @Override
      public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(new ClusterException(result.cause())).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("error")) {
          new DefaultFutureResult<Void>(new ClusterException(result.result().body().getString("message"))).setHandler(doneHandler);
        } else if (result.result().body().getString("status").equals("ok")) {
          new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
        }
      }
    });
    return this;
  }

  @Override
  public Node getNetworks(Handler<AsyncResult<Collection<ActiveNetwork>>> resultHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node getNetwork(String name, Handler<AsyncResult<ActiveNetwork>> resultHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node installNetwork(NetworkConfig network) {
    return installNetwork(network, null);
  }

  @Override
  public Node installNetwork(NetworkConfig network, final Handler<AsyncResult<Void>> doneHandler) {
    List<ModuleConfig> modules = filterModules(network);

    // First we need to determine which modules are not installed on the node.
    final List<ModuleConfig> notInstalled = new ArrayList<>();
    final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(modules.size());
    counter.setHandler(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
        } else {
          // Once we've created a list of modules not installed on the node,
          // zip and upload each of the modules.
          final Map<String, String> uploadIDs = new HashMap<>();
          final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(notInstalled.size());
          counter.setHandler(new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
              } else {
                // Once all the modules have been successfully uploaded, install them.
                final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(uploadIDs.size()).setHandler(doneHandler);
                for (Map.Entry<String, String> entry : uploadIDs.entrySet()) {
                  JsonObject message = new JsonObject()
                      .putString("action", "install")
                      .putString("type", "module")
                      .putString("module", entry.getKey())
                      .putString("upload", entry.getValue());
                  vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
                    @Override
                    public void handle(AsyncResult<Message<JsonObject>> result) {
                      if (result.failed()) {
                        counter.fail(new ClusterException(result.cause()));
                      } else if (result.result().body().getString("status").equals("error")) {
                        counter.fail(new ClusterException(result.result().body().getString("message")));
                      } else if (result.result().body().getString("status").equals("ok")) {
                        counter.succeed();
                      }
                    }
                  });
                }
              }
            }
          });

          for (final ModuleConfig module : notInstalled) {
            platform.zipModule(module.getModule(), new Handler<AsyncResult<String>>() {
              @Override
              public void handle(AsyncResult<String> result) {
                if (result.failed()) {
                  counter.fail(result.cause());
                } else {
                  //Nnow that the module has been zipped, open the zipped file.
                  final String zipFile = result.result();
                  vertx.fileSystem().open(zipFile, new Handler<AsyncResult<AsyncFile>>() {
                    @Override
                    public void handle(AsyncResult<AsyncFile> result) {
                      if (result.failed()) {
                        counter.fail(result.cause());
                      } else {
                        // Send a message to the node telling it we're going to upload the module.
                        final AsyncFile file = result.result();
                        JsonObject message = new JsonObject()
                            .putString("action", "upload");
                        vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
                          @Override
                          public void handle(AsyncResult<Message<JsonObject>> result) {
                            if (result.failed()) {
                              counter.fail(new ClusterException(result.cause()));
                            } else if (result.result().body().getString("status").equals("error")) {
                              counter.fail(new ClusterException(result.result().body().getString("message")));
                            } else if (result.result().body().getString("status").equals("ok")) {
                              final String id = result.result().body().getString("id");
                              uploadFile(file, id, new Handler<AsyncResult<Void>>() {
                                @Override
                                public void handle(AsyncResult<Void> result) {
                                  if (result.failed()) {
                                    counter.fail(result.cause());
                                  } else {
                                    uploadIDs.put(module.getModule(), id);
                                    counter.succeed();
                                  }
                                }
                              });
                            }
                          }
                        });
                      }
                    }
                  });
                }
              }
            });
          }
        }
      }
    });

    for (final ModuleConfig module : modules) {
      JsonObject message = new JsonObject()
          .putString("action", "installed")
          .putString("type", "module")
          .putString("module", module.getModule());
      vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
        @Override
        public void handle(AsyncResult<Message<JsonObject>> result) {
          if (result.failed()) {
            counter.fail(new ClusterException(result.cause()));
          } else if (result.result().body().getString("status").equals("error")) {
            counter.fail(new ClusterException(result.result().body().getString("message")));
          } else if (!result.result().body().getBoolean("result", false)) {
            notInstalled.add(module);
            counter.succeed();
          }
        }
      });
    }
    return this;
  }

  @Override
  public Node uninstallNetwork(NetworkConfig network) {
    return uninstallNetwork(network, null);
  }

  @Override
  public Node uninstallNetwork(NetworkConfig network, final Handler<AsyncResult<Void>> doneHandler) {
    List<ModuleConfig> modules = filterModules(network);
    final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(modules.size()).setHandler(doneHandler);
    for (ModuleConfig module : modules) {
      JsonObject message = new JsonObject()
          .putString("action", "uninstall")
          .putString("type", "module")
          .putString("module", module.getModule());
      vertx.eventBus().sendWithTimeout(address, message, DEFAULT_REPLY_TIMEOUT, new Handler<AsyncResult<Message<JsonObject>>>() {
        @Override
        public void handle(AsyncResult<Message<JsonObject>> result) {
          if (result.failed()) {
            counter.fail(new ClusterException(result.cause()));
          } else if (result.result().body().getString("status").equals("error")) {
            counter.fail(new ClusterException(result.result().body().getString("message")));
          } else if (result.result().body().getString("status").equals("ok")) {
            counter.succeed();
          }
        }
      });
    }
    return this;
  }

  /**
   * Filters modules out of a network configuration.
   */
  private List<ModuleConfig> filterModules(NetworkConfig network) {
    List<ModuleConfig> modules = new ArrayList<>();
    for (@SuppressWarnings("rawtypes") ComponentConfig component : network.getComponents()) {
      if (component.getType().equals(ComponentConfig.Type.MODULE)) {
        modules.add((ModuleConfig) component);
      }
    }
    return modules;
  }

  @Override
  public Node deployNetwork(String name) {
    return deployNetwork(name, null);
  }

  @Override
  public Node deployNetwork(String name, Handler<AsyncResult<ActiveNetwork>> doneHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node deployNetwork(NetworkConfig network) {
    return deployNetwork(network, null);
  }

  @Override
  public Node deployNetwork(NetworkConfig network, Handler<AsyncResult<ActiveNetwork>> doneHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node undeployNetwork(String name) {
    return undeployNetwork(name, null);
  }

  @Override
  public Node undeployNetwork(String name, Handler<AsyncResult<Void>> doneHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Node undeployNetwork(NetworkConfig network) {
    return undeployNetwork(network, null);
  }

  @Override
  public Node undeployNetwork(NetworkConfig network, Handler<AsyncResult<Void>> doneHandler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return String.format("node:%s", address);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Node && ((Node) object).address().equals(address);
  }

}
