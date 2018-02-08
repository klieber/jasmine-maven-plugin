/*-
 * #%L
 * jasmine-maven-plugin
 * %%
 * Copyright (C) 2010 - 2017 Justin Searls
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.eclipse.jetty.server;

import org.eclipse.jetty.util.component.Container;

//Link Seam for jetty server.
public class Server {

  public static String getVersion() {
    return "";
  }

  public void addConnector(Connector connector) {
  }

  public void start() throws Exception {
  }

  public void stop() throws Exception {
  }

  public void join() throws Exception {
  }

  public void setHandler(Handler handler) {
  }

  public Container getContainer() {
    return null;
  }

  public interface Graceful extends Handler {
    void setShutdown(boolean shutdown);
  }
}
