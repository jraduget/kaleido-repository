package org.kaleidofoundry.mail.dispatcher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfigurations(value = { @NamedConfiguration(name = "local-session", uri = "classpath:/mailing/localSession.yaml"),
	@NamedConfiguration(name = "asynchronous-dispatcher", uri = "classpath:/mailing/asynchronousDispatcher.yaml") })
public class AsynchronousMailDispatcherTest extends AbstractSynchronousMailDispatcherTest {

   private static MailDispatcher mailDispatcher;

   @BeforeClass
   public static void setup() {
	mailDispatcher = MailDispatcherFactory.provides("myDispatcher");
   }
   
   @AfterClass
   public static void cleanup() throws Exception {
	mailDispatcher.close();
   }
   
   @Override
   protected MailDispatcher getMailDispatcher() {
	return mailDispatcher;
   }
}
