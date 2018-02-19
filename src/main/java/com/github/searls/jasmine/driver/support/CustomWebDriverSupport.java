package com.github.searls.jasmine.driver.support;

import com.github.searls.jasmine.config.WebDriverConfiguration;
import com.github.searls.jasmine.driver.WebDriverFactoryException;
import com.github.searls.jasmine.mojo.Capability;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CustomWebDriverSupport implements WebDriverSupport {

  public WebDriver create(WebDriverConfiguration config) {
    return create(config.getWebDriverClassName(), config.getWebDriverCapabilities());
  }

  private WebDriver create(String webDriverClass, List<Capability> capabilities) {
    return create(getWebDriverClass(webDriverClass), capabilities);
  }

  private WebDriver create(Class<? extends WebDriver> webDriverClass, List<Capability> capabilities) {
    return create(getWebDriverConstructor(webDriverClass, capabilities), capabilities);
  }

  private WebDriver create(Constructor<? extends WebDriver> webDriverConstructor, List<Capability> capabilities) {
    return create(webDriverConstructor, getWebDriverConstructorArguments(webDriverConstructor, capabilities));
  }

  private WebDriver create(Constructor<? extends WebDriver> webDriverConstructor, Object ... arguments) {
    try {
      return webDriverConstructor.newInstance(arguments);
    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
      throw new WebDriverFactoryException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends WebDriver> getWebDriverClass(String className) {
    try {
      return (Class<WebDriver>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new WebDriverFactoryException(e);
    }
  }

  private Object[] getWebDriverConstructorArguments(Constructor<? extends WebDriver> constructor,
                                                    List<Capability> customCapabilities) {
    if (constructor.getParameterTypes().length == 0) {
      return new Object[0];
    }
    return new Object[]{CapabilityUtils.getCapabilities(customCapabilities)};
  }

  private Constructor<? extends WebDriver> getWebDriverConstructor(Class<? extends WebDriver> webDriverClass,
                                                                   List<Capability> capabilities) {

    WebDriverManager.getInstance(webDriverClass).setup();

    boolean hasCapabilities = !capabilities.isEmpty();
    if (hasCapabilities) {
      return getConstructorWithCapabilities(webDriverClass);
    } else {
      return getConstructorWithoutCapabilities(webDriverClass);
    }
  }

  private <E extends WebDriver> Constructor<E> getConstructorWithoutCapabilities(Class<E> webDriverClass) {
    try {
      return webDriverClass.getConstructor();
    } catch (NoSuchMethodException originalException) {
      try {
        return webDriverClass.getConstructor(Capabilities.class);
      } catch (NoSuchMethodException e) {
        throw new WebDriverFactoryException(originalException);
      }
    }
  }

  private <E extends WebDriver> Constructor<E> getConstructorWithCapabilities(Class<E> webDriverClass) {

    try {
      return webDriverClass.getConstructor(Capabilities.class);
    } catch (NoSuchMethodException originalException) {
      try {
        return webDriverClass.getConstructor();
      } catch (NoSuchMethodException e) {
        throw new WebDriverFactoryException(originalException);
      }
    }
  }
}
