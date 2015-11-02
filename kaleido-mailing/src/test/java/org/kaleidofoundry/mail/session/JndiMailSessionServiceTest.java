package org.kaleidofoundry.mail.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;

@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "jndi-testing", uri = "classpath:/mailing/jndiSession.yaml")
public class JndiMailSessionServiceTest {

   private MailSessionService mailSessionService;

   @Before
   public void setup() {
	mailSessionService = MailSessionFactory.provides("mySession");
   }

   @Test
   public void provideJndiSession() {
	assertNotNull(mailSessionService);
	assertTrue(mailSessionService instanceof JndiMailSessionService);

	JndiMailSessionService jndiMailSession = ((JndiMailSessionService) mailSessionService);
	assertEquals("mail/smtp/session", jndiMailSession.context.getString("namingService.name"));
	assertEquals("tomcat", jndiMailSession.context.getString("namingService.service-ref"));
	
	//assertEquals(true, jndiMailSession.context.getBoolean("smtp.auth.enable"));
	//assertEquals(true, jndiMailSession.context.getBoolean("smtp.ssl"));
	//assertEquals("javax.net.ssl.SSLSocketFactory", jndiMailSession.context.getString("smtp.socketFactory.class"));
	//assertEquals(Integer.valueOf(465), jndiMailSession.context.getInteger("smtp.socketFactory.port"));
   }

}
