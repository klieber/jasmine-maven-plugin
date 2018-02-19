package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class HtmlUnitWebDriverSupport implements WebDriverSupport {

  public WebDriver create(WebDriverConfiguration config) {
    return new QuietHtmlUnitDriver(
      CapabilityUtils.customizeCapabilities(DesiredCapabilities.htmlUnit(), config),
      config.isDebug()
    );
  }

}
