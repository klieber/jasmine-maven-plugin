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
package com.github.searls.jasmine.mojo;

import com.github.searls.jasmine.config.JasmineConfiguration;
import com.github.searls.jasmine.config.ServerConfiguration;
import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.runner.ReporterType;
import com.github.searls.jasmine.server.ResourceHandlerConfigurator;
import com.github.searls.jasmine.server.ServerManager;
import com.github.searls.jasmine.server.ServerManagerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Execute specs in a web browser. Monitors your sources/specs for changes as you develop.
 */
@Mojo(name = "bdd", requiresDirectInvocation = true, requiresDependencyResolution = ResolutionScope.TEST)
public class ServerMojo extends AbstractJasmineMojo {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerMojo.class);

  private static final String INSTRUCTION_FORMAT = getInstructionsTemplate();

  /**
   * <p>If set to true, then the plugin will automatically open the spec runner using your systems default web
   * browser. Default is false.</p>
   * <br>
   * <p>Common usage would be to specify the parameter using the `jasmine.open` property at the terminal:</p>
   * <pre>
   *   $ mvn jasmine:bdd -Djasmine.open
   * </pre>
   *
   * @since 3.0
   */
  @Parameter(property = "jasmine.open", defaultValue = "false")
  @SuppressWarnings("FieldMayBeFinal")
  private boolean open = false;

  private final RelativizesFilePaths relativizesFilePaths;
  private final ResourceHandlerConfigurator resourceHandlerConfigurator;
  private final ServerManagerFactory serverManagerFactory;

  @Inject
  public ServerMojo(MavenProject mavenProject,
                    ResourceRetriever resourceRetriever,
                    ReporterRetriever reporterRetriever,
                    RelativizesFilePaths relativizesFilePaths,
                    ResourceHandlerConfigurator resourceHandlerConfigurator,
                    ServerManagerFactory serverManagerFactory) {
    super(mavenProject, ReporterType.HtmlReporter, resourceRetriever, reporterRetriever);
    this.relativizesFilePaths = relativizesFilePaths;
    this.resourceHandlerConfigurator = resourceHandlerConfigurator;
    this.serverManagerFactory = serverManagerFactory;
  }

  @Override
  public void run(ServerConfiguration serverConfiguration,
                  JasmineConfiguration jasmineConfiguration) throws Exception {
    ServerManager serverManager = serverManagerFactory.create();
    serverManager.start(serverConfiguration.getServerPort(), resourceHandlerConfigurator.createHandler(jasmineConfiguration));
    LOGGER.info(this.buildServerInstructions(serverConfiguration, jasmineConfiguration));
    openBrowser(serverConfiguration);
    serverManager.join();
  }

  private String buildServerInstructions(ServerConfiguration serverConfiguration,
                                         JasmineConfiguration jasmineConfiguration) throws IOException {
    return String.format(
      INSTRUCTION_FORMAT,
      serverConfiguration.getServerURL(),
      getSourcePath(jasmineConfiguration),
      getSpecPath(jasmineConfiguration)
    );
  }

  private String getSourcePath(JasmineConfiguration config) throws IOException {
    return this.getRelativePath(config.getBasedir(), config.getSources().getDirectory());
  }

  private String getSpecPath(JasmineConfiguration config) throws IOException {
    return this.getRelativePath(config.getBasedir(), config.getSpecs().getDirectory());
  }

  private String getRelativePath(File basedir, File absolutePath) throws IOException {
    return this.relativizesFilePaths.relativize(basedir, absolutePath);
  }

  private void openBrowser(ServerConfiguration serverConfiguration) {
    if (this.open) {
      Desktop desktop = Desktop.getDesktop();
      try {
        //specify the protocol along with the URL
        desktop.browse(serverConfiguration.getServerURL().toURI());
      } catch (URISyntaxException | IOException e) {
        LOGGER.error("Failed to automatically launch web browser.", e);
      }
    }
  }

  private static String getInstructionsTemplate() {
    String template;
    try {
      template = IOUtils.toString(
        ServerMojo.class.getResourceAsStream("/instructions.template"),
        StandardCharsets.UTF_8
      );
    } catch (IOException e) {
      template = "";
      LOGGER.error("Unable to read instructions template: ", e);
    }
    return template;
  }
}
