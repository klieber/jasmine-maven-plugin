package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import org.openqa.selenium.WebDriver;

public interface WebDriverSupport {
  WebDriver create(WebDriverConfiguration config);
}
