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
package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.mojo.Capability;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class CapabilityUtils {

  public static DesiredCapabilities getCapabilities(List<Capability> customCapabilities) {
    return customizeCapabilities(new DesiredCapabilities(), customCapabilities);
  }

  public static <E extends MutableCapabilities> E customizeCapabilities(E capabilities,
                                                                        WebDriverConfiguration config) {
    return customizeCapabilities(capabilities, config.getWebDriverCapabilities());
  }

  private static <E extends MutableCapabilities> E customizeCapabilities(E capabilities,
                                                                         List<Capability> customCapabilities) {
    for (Capability capability : customCapabilities) {
      Object value = capability.getValue();
      if (value != null && (!String.class.isInstance(value) || StringUtils.isNotBlank((String) value))) {
        capabilities.setCapability(capability.getName(), capability.getValue());
      } else if (capability.getList() != null && !capability.getList().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getList());
      } else if (capability.getMap() != null && !capability.getMap().isEmpty()) {
        capabilities.setCapability(capability.getName(), capability.getMap());
      }
    }

    return capabilities;
  }
}
