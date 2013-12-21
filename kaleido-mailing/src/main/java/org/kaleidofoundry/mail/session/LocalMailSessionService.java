/*  
 * Copyright 2008-2014 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.mail.session;

import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_AUTH;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_HOST;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_PASSWORD;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_PORT;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_SSL;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_SSL_SOCKETFACTORY;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_SSL_SOCKETFACTORY_FALLBACK;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_SSL_SOCKETFACTORY_PORT;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SMTP_USER;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.LOCAL_SSL_FACTORY;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Local mail session provider
 * 
 * @author jraduget
 */
@Declare(value = "mailSessions.local")
public class LocalMailSessionService implements MailSessionService {

   private final RuntimeContext<MailSessionService> context;

   public LocalMailSessionService(RuntimeContext<MailSessionService> context) {
	super();
	this.context = context;
   }

   public Session createSession() {

	final String hostName = context.getString(LOCAL_SMTP_HOST);
	final int portName = context.getInteger(LOCAL_SMTP_PORT, 25);
	final boolean authen = context.getBoolean(LOCAL_SMTP_AUTH, false);

	if (StringHelper.isEmpty(hostName)) throw new EmptyContextParameterException(LOCAL_SMTP_HOST, context);

	final boolean ssl = context.getBoolean(LOCAL_SMTP_SSL, false);

	final Properties mailProps = new Properties();

	mailProps.put("mail.smtp.host", hostName);
	mailProps.put("mail.smtp.port", portName);
	mailProps.put("mail.smtp.auth", authen);

	if (ssl) {
	   // check with jdk6 ... Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	   mailProps.put("mail.smtp.socketFactory.class", context.getString(LOCAL_SMTP_SSL_SOCKETFACTORY, LOCAL_SSL_FACTORY));
	   mailProps.put("mail.smtp.socketFactory.fallback", context.getBoolean(LOCAL_SMTP_SSL_SOCKETFACTORY_FALLBACK, false));
	   mailProps.put("mail.smtp.auth", "true");
	   mailProps.put("mail.smtp.socketFactory.port", context.getString(LOCAL_SMTP_SSL_SOCKETFACTORY_PORT));
	}

	if (authen == false) {
	   return Session.getDefaultInstance(mailProps, null);
	} else {
	   return Session.getDefaultInstance(mailProps, new Authenticator() {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
		   return new PasswordAuthentication(context.getString(LOCAL_SMTP_USER), context.getString(LOCAL_SMTP_PASSWORD));
		}
	   });
	}

   }

}
