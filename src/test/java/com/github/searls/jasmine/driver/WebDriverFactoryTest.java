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
package com.github.searls.jasmine.driver;

import com.github.searls.jasmine.config.ImmutableWebDriverConfiguration;
import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.driver.support.QuietHtmlUnitDriver;
import com.github.searls.jasmine.mojo.Capability;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverFactoryTest {

  @InjectMocks
  private WebDriverFactory factory;

  @Before
  public void setUp() {
    factory = new WebDriverFactory();
  }

  @Test
  public void createsQuietHtmlUnitDriver() {
    WebDriverConfiguration config = createMockConfig(HtmlUnitDriver.class);
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(QuietHtmlUnitDriver.class);
  }

  @Test
  public void customDriverIsCreatedWithDefaultConstructorIfNoCapabilitiesConstructorExists() {
    WebDriverConfiguration config = createMockConfig(CustomDriverWithDefaultConstructor.class);
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithDefaultConstructor.class);
  }

  @Test
  public void customDriverIsCreatedWithCapabilitiesIfConstructorExists() {
    WebDriverConfiguration config = createMockConfig(CustomDriverWithCapabilities.class);
    assertThat(factory.createWebDriver(config)).isExactlyInstanceOf(CustomDriverWithCapabilities.class);
  }

  @Test
  public void enablesJavascriptOnCustomDriver() {
    WebDriverConfiguration config = createMockConfig(CustomDriverWithCapabilities.class);
    Capabilities capabilities = createWebDriverAndReturnCapabilities(config);
    assertThat(capabilities.is(SUPPORTS_JAVASCRIPT)).isTrue();
  }

  @Test
  public void setsCapabilityFromMap() {
    Capability capability = new Capability();
    capability.setName("foo");
    capability.setValue("bar");

    WebDriverConfiguration config = ImmutableWebDriverConfiguration.builder()
      .debug(false)
      .webDriverClassName(CustomDriverWithCapabilities.class.getName())
      .addWebDriverCapabilities(capability)
      .build();

    assertThat(createWebDriverAndReturnCapabilities(config).getCapability("foo")).isEqualTo("bar");
  }

  private Capabilities createWebDriverAndReturnCapabilities(WebDriverConfiguration config) {
    CustomDriverWithCapabilities driver = (CustomDriverWithCapabilities) factory.createWebDriver(config);
    return driver.getCapabilities();
  }

  private <E extends WebDriver> WebDriverConfiguration createMockConfig(Class<E> webDriverClass) {
    return ImmutableWebDriverConfiguration.builder()
      .debug(false)
      .webDriverClassName(webDriverClass.getName())
      .build();
  }
}
