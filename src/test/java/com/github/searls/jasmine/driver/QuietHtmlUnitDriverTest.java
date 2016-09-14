package com.github.searls.jasmine.driver;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class QuietHtmlUnitDriverTest {

  @Mock
  private WebClient mockWebClient;

  @Mock
  private DesiredCapabilities capabilities;

  private QuietHtmlUnitDriver driver;
  private boolean debug = false;

  private void createDriver() {
    driver = new QuietHtmlUnitDriver(capabilities, debug);
  }

  @Test
  public void enablesJavascript() {
    createDriver();

    Assertions.assertThat(driver.isJavascriptEnabled()).isTrue();
  }

  private void modifyWebClient() {
    createDriver();
    driver.modifyWebClient(mockWebClient);
  }

  @Test
  public void installsNewAjaxController() throws Exception {
    modifyWebClient();

    verify(mockWebClient).setAjaxController(isA(NicelyResynchronizingAjaxController.class));
  }

  @Test
  public void overridesIncorrectnessListenerToSuppressOutput() {
    modifyWebClient();

    verify(mockWebClient).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }

  @Test
  public void doesNotOverrideIncorrectnessListenerWhenDebugFlagIsSet() {
    debug = true;
    modifyWebClient();

    verify(mockWebClient, never()).setIncorrectnessListener(isA(IncorrectnessListener.class));
  }
}
