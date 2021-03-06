package com.github.searls.jasmine.mojo;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
public class TestMojoTest {

  @Mock
  private Log log;

  private TestMojo mojo;

  @Mock
  private Properties properties;

  @Mock
  private RepositorySystem repositorySystem;

  @Before
  public void before() {
    this.mojo = new TestMojo(repositorySystem);
    this.mojo.setLog(log);
  }

  @Test
  public void testExecuteIfSkipIsTrue() throws Exception {
    this.mojo.skipTests = true;
    this.mojo.execute();
    verify(log).info("Skipping Jasmine Specs");
  }
}
