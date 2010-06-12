package org.kaleidofoundry.mail.sender;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.session.MailSessionContext;

/**
 * MailSenderService, implémentation ejb
 * 
 * @author Jerome RADUGET
 */
public class MailSenderServiceJmsImpl implements MailSenderService {

   private final QueueConnectionFactory factory;
   private final Queue queue;
   private final boolean sessionTransacted;

   /**
    * @param jndiQueueName
    * @param jndiQueueFactoryName
    * @param jndiContext
    * @param jmsSessionTransacted 
    * @throws JndiResourceException
    */
   public MailSenderServiceJmsImpl(final String jndiQueueName, final String jndiQueueFactoryName,
	   final JndiContext jndiContext, final boolean jmsSessionTransacted) throws JndiResourceException {
	// Locator pour le service (à n'instancier qu'une fois généralement.....)
	final JndiResourceLocator<QueueConnectionFactory> locatorQueueFactory = new JndiResourceLocator<QueueConnectionFactory>(
		jndiContext);

	// QueueConnectionFactory
	factory = locatorQueueFactory.lookup(jndiQueueFactoryName, QueueConnectionFactory.class);

	final JndiResourceLocator<Queue> locatorQueue = new JndiResourceLocator<Queue>(jndiContext);

	// Queue JMS
	queue = locatorQueue.lookup(jndiQueueName, Queue.class);

	// Transaction gérée ou non ?
	sessionTransacted = jmsSessionTransacted;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#createMessage()
    */
   public MailMessage createMessage() {
	return new MailMessageBean();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#getSessionContext()
    */
   public MailSessionContext getSessionContext() {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#send(org.kaleidofoundry.mail.MailMessage)
    */
   public void send(final MailMessage message) throws MailException, AddressException, MessagingException {

	ObjectMessage jmsMessage = null;
	QueueConnection connection = null;
	QueueSession session = null;
	QueueSender sender = null;

	try {
	   // Create connection
	   connection = factory.createQueueConnection();
	   session = connection.createQueueSession(sessionTransacted, Session.AUTO_ACKNOWLEDGE);

	   // Create session (with no transaction)
	   sender = session.createSender(queue);

	   // Create sender
	   jmsMessage = session.createObjectMessage();
	   jmsMessage.setObject(message);

	   // Send
	   sender.send(jmsMessage);

	} catch (final JMSException jmse) {
	   throw new MailSenderException("jndi.error.jms", jmse, jmse.getMessage());
	} finally {
	   if (connection != null) {
		try {
		   connection.close();
		} catch (final JMSException jmse) {
		   logger.error("JMS connection close error", jmse);
		}
	   }
	}
   }

}
