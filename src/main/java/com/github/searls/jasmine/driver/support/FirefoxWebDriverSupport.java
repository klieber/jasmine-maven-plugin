package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxWebDriverSupport implements WebDriverSupport {

  public WebDriver create(WebDriverConfiguration config) {
    WebDriverManager.getInstance(FirefoxDriver.class).setup();
    FirefoxOptions options = CapabilityUtils.customizeCapabilities(new FirefoxOptions().setHeadless(true), config);
    return new FirefoxDriver(options);
  }
}
