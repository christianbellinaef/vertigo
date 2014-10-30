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
package net.kuujo.vertigo.io.port;

import io.vertx.codegen.annotations.VertxGen;
import net.kuujo.vertigo.io.Output;

/**
 * Output port to which messages are sent.<p>
 *
 * The output port can contain any number of {@link net.kuujo.vertigo.io.stream.OutputStream}
 * to which it sends messages. Each message that is sent on the output port will
 * be sent on all underlying streams. Selection occurs within each stream
 * rather than at the port level.<p>
 *
 * Streams are constructed based on current network configuration information.
 * When the network configuration is updated, the port will automatically update
 * its internal streams.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@VertxGen
public interface OutputPort<T> extends Port<OutputPort<T>, T>, Output<OutputPort<T>, T> {
}
