package org.kaleidofoundry.mail.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "local-testing", uri = "classpath:/mailing/localSession.yaml")
public class LocalMailSessionServiceTest {

   private MailSessionService mailSessionService;

   @Before
   public void setup() {
	mailSessionService = MailSessionFactory.provides("mySession-ssl");
   }

   @Test
   public void provideLocalSession() {
	assertNotNull(mailSessionService);
	assertTrue(mailSessionService instanceof LocalMailSessionService);

	LocalMailSessionService localMailSession = ((LocalMailSessionService) mailSessionService);
	assertEquals("smtp.gmail.com", localMailSession.context.getString("smtp.host"));
	assertEquals(Integer.valueOf(465), localMailSession.context.getInteger("smtp.port"));
	assertEquals(true, localMailSession.context.getBoolean("smtp.auth.enable"));
	assertEquals(true, localMailSession.context.getBoolean("smtp.ssl"));
	assertEquals("javax.net.ssl.SSLSocketFactory", localMailSession.context.getString("smtp.socketFactory.class"));
	assertEquals(Integer.valueOf(465), localMailSession.context.getInteger("smtp.socketFactory.port"));
   }

}
