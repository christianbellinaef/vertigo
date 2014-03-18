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
package net.kuujo.vertigo.network.manager;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import net.kuujo.vertigo.cluster.ClusterClient;
import net.kuujo.vertigo.context.ComponentContext;
import net.kuujo.vertigo.context.InstanceContext;
import net.kuujo.vertigo.context.NetworkContext;
import net.kuujo.vertigo.network.Network;
import net.kuujo.vertigo.context.impl.ContextBuilder;
import net.kuujo.vertigo.util.CountingCompletionHandler;
import net.kuujo.vertigo.util.Factories;
import net.kuujo.vertigo.util.serializer.Serializer;
import net.kuujo.vertigo.util.serializer.SerializerFactory;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonObject;

/**
 * Vertigo network manager.
 *
 * @author Jordan Halterman
 */
public abstract class NetworkManager extends BusModBase {
  private static final Serializer networkSerializer = SerializerFactory.getSerializer(Network.class);
  private static final Serializer contextSerializer = SerializerFactory.getSerializer(NetworkContext.class);
  private String address;
  private ClusterClient cluster;
  private Queue<Message<JsonObject>> queue = new ArrayDeque<>();
  private boolean locked;

  private final Handler<Message<JsonObject>> messageHandler = new Handler<Message<JsonObject>>() {
    @Override
    public void handle(Message<JsonObject> message) {
      queue.add(message);
      checkQueue();
    }
  };

  private void checkQueue() {
    if (!locked) {
      Message<JsonObject> message = queue.poll();
      if (message != null) {
        String action = message.body().getString("action");
        if (action != null) {
          switch (action) {
            case "deploy":
              doDeploy(message);
              break;
            case "undeploy":
              doUndeploy(message);
              break;
            case "shutdown":
              container.exit();
              break;
            default:
              sendError(message, "Invalid action " + action);
              break;
          }
        }
      }
    }
  }

  private void lock() {
    locked = true;
  }

  private void unlock() {
    locked = false;
    vertx.runOnContext(new Handler<Void>() {
      @Override
      public void handle(Void _) {
        checkQueue();
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public void start(final Future<Void> startResult) {
    address = getMandatoryStringConfig("address");
    String clusterType = getMandatoryStringConfig("cluster");
    Class<? extends ClusterClient> clusterClass;
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try {
      clusterClass = (Class<? extends ClusterClient>) loader.loadClass(clusterType);
    }
    catch (Exception e) {
      startResult.setFailure(new IllegalArgumentException("Error instantiating serializer factory."));
      return;
    }
    cluster = Factories.createObject(clusterClass, vertx, container);
    vertx.eventBus().registerHandler(address, messageHandler, new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          startResult.setFailure(result.cause());
        }
        else {
          NetworkManager.super.start(startResult);
        }
      }
    });
  }

  /**
   * Deploys a network.
   */
  private void doDeploy(final Message<JsonObject> message) {
    JsonObject jnetwork = getMandatoryObject("network", message);
    if (jnetwork == null) {
      sendError(message, "No network defined.");
      return;
    }

    lock();
    Network network = networkSerializer.deserializeObject(jnetwork, Network.class);
    deployNetwork(ContextBuilder.buildContext(network), new Handler<AsyncResult<NetworkContext>>() {
      @Override
      public void handle(AsyncResult<NetworkContext> result) {
        if (result.failed()) {
          sendError(message, result.cause().getMessage());
        }
        else {
          sendOK(message, new JsonObject().putObject("context", contextSerializer.serializeToObject(result.result())));
        }
        unlock();
      }
    });
  }

  private void deployNetwork(final NetworkContext context, final Handler<AsyncResult<NetworkContext>> doneHandler) {
    final CountingCompletionHandler<Void> complete = new CountingCompletionHandler<Void>(context.components().size());
    complete.setHandler(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          new DefaultFutureResult<NetworkContext>(result.cause()).setHandler(doneHandler);
        }
        else {
          new DefaultFutureResult<NetworkContext>(context).setHandler(doneHandler);
        }
      }
    });
    deployComponents(context.components(), complete);
  }

  private void deployComponents(List<ComponentContext<?>> components, final CountingCompletionHandler<Void> complete) {
    for (final ComponentContext<?> component : components) {
      final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(component.instances().size());
      counter.setHandler(new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> result) {
          if (result.failed()) {
            complete.fail(result.cause());
          }
          else {
            complete.succeed();
          }
        }
      });
      deployInstances(component.instances(), counter);
    }
  }

  private void deployInstances(List<InstanceContext> instances, final CountingCompletionHandler<Void> counter) {
    for (final InstanceContext instance : instances) {
      cluster.isDeployed(instance.address(), new Handler<AsyncResult<Boolean>>() {
        @Override
        public void handle(AsyncResult<Boolean> result) {
          if (result.failed()) {
            counter.fail(result.cause());
          }
          else if (result.result()) {
            cluster.set(instance.address(), InstanceContext.toJson(instance), new Handler<AsyncResult<Void>>() {
              @Override
              public void handle(AsyncResult<Void> result) {
                if (result.failed()) {
                  counter.fail(result.cause());
                }
                else {
                  counter.succeed();
                }
              }
            });
          }
          else {
            if (instance.component().isModule()) {
              deployModule(instance, counter);
            }
            else if (instance.component().isVerticle() && !instance.component().toVerticle().isWorker()) {
              deployVerticle(instance, counter);
            }
            else if (instance.component().isVerticle() && instance.component().toVerticle().isWorker()) {
              deployWorkerVerticle(instance, counter);
            }
          }
        }
      });
    }
  }

  private void deployModule(final InstanceContext instance, final CountingCompletionHandler<Void> counter) {
    cluster.deployModule(instance.address(), instance.component().toModule().module(), new JsonObject().putString("address", instance.address()), 1, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> result) {
        if (result.failed()) {
          counter.fail(result.cause());
        }
        else {
          cluster.set(instance.address(), InstanceContext.toJson(instance), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                counter.fail(result.cause());
              }
              else {
                counter.succeed();
              }
            }
          });
        }
      }
    });
  }

  private void deployVerticle(final InstanceContext instance, final CountingCompletionHandler<Void> counter) {
    cluster.deployVerticle(instance.address(), instance.component().toVerticle().main(),  new JsonObject().putString("address", instance.address()), 1, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> result) {
        if (result.failed()) {
          counter.fail(result.cause());
        }
        else {
          cluster.set(instance.address(), InstanceContext.toJson(instance), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                counter.fail(result.cause());
              }
              else {
                counter.succeed();
              }
            }
          });
        }
      }
    });
  }

  private void deployWorkerVerticle(final InstanceContext instance, final CountingCompletionHandler<Void> counter) {
    cluster.deployWorkerVerticle(instance.address(), instance.component().toVerticle().main(), new JsonObject().putString("address", instance.address()), 1, instance.component().toVerticle().isMultiThreaded(), new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> result) {
        if (result.failed()) {
          counter.fail(result.cause());
        }
        else {
          cluster.set(instance.address(), InstanceContext.toJson(instance), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                counter.fail(result.cause());
              }
              else {
                counter.succeed();
              }
            }
          });
        }
      }
    });
  }

  /**
   * Undeploys a network.
   */
  private void doUndeploy(final Message<JsonObject> message) {
    String address = message.body().getString("address");
    if (address != null) {
      doUndeployAll(message);
      return;
    }

    JsonObject jnetwork = getMandatoryObject("network", message);
    if (jnetwork == null) {
      sendError(message, "No network defined.");
      return;
    }

    lock();
    final Network network = networkSerializer.deserializeObject(jnetwork, Network.class);
    undeployNetwork(ContextBuilder.buildContext(network), new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          sendError(message, result.cause().getMessage());
        }
        else {
          sendOK(message);
        }
        unlock();
      }
    });
  }

  private void undeployNetwork(final NetworkContext context, final Handler<AsyncResult<Void>> doneHandler) {
    final CountingCompletionHandler<Void> complete = new CountingCompletionHandler<Void>(context.components().size());
    complete.setHandler(new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          new DefaultFutureResult<Void>(result.cause()).setHandler(doneHandler);
        }
        else {
          new DefaultFutureResult<Void>((Void) null).setHandler(doneHandler);
        }
      }
    });
    undeployComponents(context.components(), complete);
  }

  private void undeployComponents(List<ComponentContext<?>> components, final CountingCompletionHandler<Void> complete) {
    for (final ComponentContext<?> component : components) {
      final CountingCompletionHandler<Void> counter = new CountingCompletionHandler<Void>(component.instances().size());
      counter.setHandler(new Handler<AsyncResult<Void>>() {
        @Override
        public void handle(AsyncResult<Void> result) {
          if (result.failed()) {
            complete.fail(result.cause());
          }
          else {
            complete.succeed();
          }
        }
      });
      undeployInstances(component.instances(), counter);
    }
  }

  private void undeployInstances(List<InstanceContext> instances, final CountingCompletionHandler<Void> counter) {
    for (final InstanceContext instance : instances) {
      cluster.isDeployed(instance.address(), new Handler<AsyncResult<Boolean>>() {
        @Override
        public void handle(AsyncResult<Boolean> result) {
          if (result.failed()) {
            counter.fail(result.cause());
          }
          else if (result.result()) {
            if (instance.component().isModule()) {
              undeployModule(instance, counter);
            }
            else if (instance.component().isVerticle()) {
              undeployVerticle(instance, counter);
            }
          }
          else {
            cluster.delete(instance.address(), new Handler<AsyncResult<Void>>() {
              @Override
              public void handle(AsyncResult<Void> result) {
                if (result.failed()) {
                  counter.fail(result.cause());
                }
                else {
                  counter.succeed();
                }
              }
            });
          }
        }
      });
    }
  }

  private void undeployModule(final InstanceContext instance, final CountingCompletionHandler<Void> counter) {
    cluster.undeployModule(instance.address(), new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          counter.fail(result.cause());
        }
        else {
          cluster.delete(instance.address(), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                counter.fail(result.cause());
              }
              else {
                counter.succeed();
              }
            }
          });
        }
      }
    });
  }

  private void undeployVerticle(final InstanceContext instance, final CountingCompletionHandler<Void> counter) {
    cluster.undeployVerticle(instance.address(), new Handler<AsyncResult<Void>>() {
      @Override
      public void handle(AsyncResult<Void> result) {
        if (result.failed()) {
          counter.fail(result.cause());
        }
        else {
          cluster.delete(instance.address(), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                counter.fail(result.cause());
              }
              else {
                counter.succeed();
              }
            }
          });
        }
      }
    });
  }

  /**
   * Undeploys the entire network.
   */
  private void doUndeployAll(final Message<JsonObject> message) {
    String address = getMandatoryString("address", message);
    if (address == null) {
      return;
    }

    cluster.get(address, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> result) {
        if (result.failed()) {
          sendError(message, result.cause().getMessage());
        }
        else if (result.result() == null) {
          sendOK(message);
        }
        else {
          undeployNetwork(contextSerializer.deserializeString(result.result(), NetworkContext.class), new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
              if (result.failed()) {
                sendError(message, result.cause().getMessage());
              }
              else {
                sendOK(message);
              }
            }
          });
        }
      }
    });
  }

}