package org.kaleidofoundry.mail.dispatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-session", uri = "classpath:/mailing/localSession.yaml"),
	@NamedConfiguration(name = "synchronous-dispatcher", uri = "classpath:/mailing/synchronousDispatcher.yaml") })
public class SynchronousMailDispatcherTest extends AbstractSynchronousMailDispatcherTest {

   private MailDispatcher mailDispatcher;

   @Before
   public void setup() {
	mailDispatcher = MailDispatcherFactory.provides("myDispatcher");
   }
   
   @After
   public void cleanup() throws Exception {
	mailDispatcher.close();
   }
   
   @Override
   protected MailDispatcher getMailDispatcher() {
	return mailDispatcher;
   }
}
