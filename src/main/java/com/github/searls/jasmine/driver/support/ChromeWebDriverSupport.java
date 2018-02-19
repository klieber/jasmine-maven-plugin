package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeWebDriverSupport implements WebDriverSupport {

  public WebDriver create(WebDriverConfiguration config) {
    WebDriverManager.getInstance(ChromeDriver.class).setup();
    return new ChromeDriver(CapabilityUtils.customizeCapabilities(new ChromeOptions().setHeadless(true), config));
  }

}
