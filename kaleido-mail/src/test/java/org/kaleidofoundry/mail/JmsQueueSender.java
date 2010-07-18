/*  
 * Copyright 2008-2010 the original author or authors 
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
package org.kaleidofoundry.mail;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kaleidofoundry.mail.sender.MailSenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example d'envoie de message JMS (P2P avec queue)
 * 
 * @author Jerome RADUGET
 */
public class JmsQueueSender {

   private static final Logger LOGGER = LoggerFactory.getLogger(JmsQueueSender.class);

   public static void main(final String[] args) {

	QueueConnectionFactory factory = null;
	QueueConnection connection = null;
	QueueSession session = null;
	Queue queue = null;
	QueueSender sender = null;

	ObjectMessage message = null;

	// Get the initial context
	Context initialContext = null;
	final Hashtable<String, String> env = new Hashtable<String, String>();

	env.put(Context.PROVIDER_URL, "localhost:1099");
	env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
	env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

	try {
	   initialContext = new InitialContext(env);
	} catch (final NamingException nae) {
	   LOGGER.error("Cannot get initial context", nae);
	   System.exit(1);
	}

	// Lookup connection factory (jndi name)
	try {
	   factory = (QueueConnectionFactory) initialContext.lookup("QueueConnectionFactory");

	} catch (final NamingException nae) {
	   LOGGER.error("JNDI API lookup QueueConnectionFactory failed", nae);
	   System.exit(1);
	}

	// Lookup queue (jndi name)
	try {
	   queue = (Queue) initialContext.lookup("queue/queueMailSession");

	} catch (final NamingException nae) {
	   LOGGER.error("JNDI API lookup Queue failed", nae);
	   System.exit(1);
	}

	try {
	   // Create connection
	   connection = factory.createQueueConnection();
	   session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

	   // Create session (with no transaction)
	   sender = session.createSender(queue);

	   // Create sender
	   message = session.createObjectMessage();

	   final MailMessage mail = MailSenderFactory.createMessage();
	   mail.setSubject("Subject");
	   mail.setContent("Content...<b>Hello world !</b><br/>....");
	   mail.setBodyContentHtml();

	   mail.setFromAdress("jerome.raduget@gmail.com");
	   mail.getToAdress().add("jerome.raduget@gmail.com");
	   mail.getCcAdress().add("toto@tata.fr");
	   message.setObject(mail);

	   // Send
	   sender.send(message);

	} catch (final JMSException jmse) {
	   LOGGER.error("JMS exception occured", jmse);
	} finally {
	   if (connection != null) {
		try {
		   connection.close();
		} catch (final JMSException jmse) {
		   LOGGER.error("JMS connection close error", jmse);
		}
	   }
	}

   }

}
