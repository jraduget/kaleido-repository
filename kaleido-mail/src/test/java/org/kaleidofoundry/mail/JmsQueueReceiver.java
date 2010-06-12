package org.kaleidofoundry.mail;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsQueueReceiver {

   private static final Logger LOGGER = LoggerFactory.getLogger(JmsQueueReceiver.class);

   public static void main(final String[] args) {

	QueueConnectionFactory factory = null;
	QueueConnection connection = null;
	QueueSession session = null;
	Queue queue = null;
	QueueReceiver receiver = null;

	Message message = null;

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

	// Lookup connection factory (jndi name)
	try {
	   queue = (Queue) initialContext.lookup("queue/queueMailSession");

	} catch (final NamingException nae) {
	   LOGGER.error("JNDI API lookup Queue failed", nae);
	   System.exit(1);
	}

	try {
	   // Create connection
	   connection = factory.createQueueConnection();
	   connection.start();
	   session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

	   // Create session (with no transaction)
	   receiver = session.createReceiver(queue);

	   // Send
	   message = receiver.receive();

	   if (message != null && message instanceof ObjectMessage) {

		final MailMessage mail = (MailMessage) ((ObjectMessage) message).getObject();

		LOGGER.info(mail.getSubject());
		LOGGER.info(mail.getContent());

	   }

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
