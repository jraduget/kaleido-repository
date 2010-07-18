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
package org.kaleidofoundry.messaging.jms;

import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_AcknowledgeMode;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_ConnectionFactoryLocalRef;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_ConnectionFactoryName;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_ConnectionFactoryPassword;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_ConnectionFactoryUser;
import static org.kaleidofoundry.messaging.jms.JmsTransportConstants.PATH_KEY_SessionTransacted;

import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractTransportMessaging;
import org.kaleidofoundry.messaging.TransportMessaging;
import org.kaleidofoundry.messaging.TransportMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.TransportRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jms 1.1 MessagingTransport
 * 
 * @author Jerome RADUGET
 */
public class JmsTransportMessaging extends AbstractTransportMessaging implements TransportMessaging {

   static final Logger LOGGER = LoggerFactory.getLogger(JmsTransportMessaging.class);

   /*
    * A connection factory is the object a client uses to create a connection to a provider.
    * A connection factory encapsulates a set of connection configuration parameters
    * that has been defined by an administrator. Each connection factory is an instance of the
    * ConnectionFactory, QueueConnectionFactory, or TopicConnectionFactory interface.
    */
   private ConnectionFactory connectionFactory;

   private final Vector<Session> sessions; // !! Vector car synchronisé !!
   private final Vector<Connection> connections; // !! Vector car synchronisé !!

   /**
    * @param context
    * @throws TransportMessagingException
    */
   public JmsTransportMessaging(final TransportMessagingContext context) throws TransportMessagingException {
	super(context);

	// Memorize user session and connections
	sessions = new Vector<Session>();
	connections = new Vector<Connection>();

	// Check context information before instantiate ConnectionFactory
	checkContext();

	// ConnectionFactory instanciation
	try {
	   final JndiContext jndiContext = new JndiContext(getConnectionFactoryJndiContextName(), context);
	   final JndiResourceLocator<ConnectionFactory> jndiLocator = new JndiResourceLocator<ConnectionFactory>(jndiContext);
	   connectionFactory = jndiLocator.lookup(getConnectionFactoryJndiName(), ConnectionFactory.class);
	} catch (final JndiResourceException jndie) {
	   throw new TransportMessagingException("messaging.transport.jms.connection.factory.lookup", jndie, getConnectionFactoryJndiName(), jndie.getMessage());
	}
   }

   /**
    * Check cohérence
    * 
    * @throws TransportMessagingException
    */
   protected void checkContext() throws TransportMessagingException {

	if (StringHelper.isEmpty(getConnectionFactoryJndiName())) { throw new TransportMessagingException("messaging.transport.jms.connection.factory.name"); }

	if (StringHelper.isEmpty(getConnectionFactoryJndiContextName())) { throw new TransportMessagingException(
		"messaging.transport.jms.connection.factory.context"); }

	final String strAcknowledgeMode = getContext().getProperty(PATH_KEY_AcknowledgeMode);

	if (!StringHelper.isEmpty(strAcknowledgeMode)) {
	   try {
		Integer.valueOf(strAcknowledgeMode);
	   } catch (final NumberFormatException nfe) {
		throw new TransportMessagingException("messaging.transport.jms.illegal", new String[] { PATH_KEY_AcknowledgeMode });
	   }
	}
   }

   /**
    * Création d'une connection JMS
    * Suivant la configuration du transport,
    * cette session fournira un contexte transactionel ou non,
    * acknowledgera les messages reçus....
    * Les sessions ouvertes par le transports sont mémorisées.
    * A la fermeture du transport les sessions encore actives seront fermées.
    * *
    * 
    * @return
    * @throws TransportMessagingException
    */
   public Connection createConnection() throws TransportMessagingException {
	Connection connection = null; // Connection to Application serveur

	try {

	   // create connection with default jndi user information
	   if (StringHelper.isEmpty(getConnectionFactoryUser())) {
		connection = connectionFactory.createConnection();
	   }
	   // create connection with custom user / password configured
	   else {
		connection = connectionFactory.createConnection(getConnectionFactoryUser(), getConnectionFactoryPassword());
	   }
	   connections.add(connection);

	   return connection;
	} catch (final JMSException jmse) {
	   throw new TransportMessagingException("messaging.transport.jms.connection.create", jmse);
	}
   }

   /**
    * Création d'une session JMS pour l'utilisateur
    * Suivant la configuration du transport,
    * cette session fournira un contexte transactionel ou non,
    * acknowledgera les messages reçus....
    * Les sessions ouvertes par le transports sont mémorisées.
    * A la fermeture du transport les sessions encore actives seront fermées.
    * 
    * @return jms session instance
    * @throws TransportMessagingException
    */
   public Session createSession() throws TransportMessagingException {
	try {
	   // User session for transactionnal use
	   final Session session = createConnection().createSession(isSessionTransacted(), getAcknowledgeMode());
	   sessions.add(session);
	   return session;
	} catch (final JMSException jmse) {
	   throw new TransportMessagingException("messaging.transport.jms.session.create", jmse);
	}
   }

   /**
    * Libération d'une connection JMS au serveur d'application
    * 
    * @param conn
    * @throws TransportMessagingException
    */
   public void closeConnection(final Connection conn) throws TransportMessagingException {
	try {
	   conn.close();
	   connections.remove(conn);
	} catch (final JMSException jmse) {
	   throw new TransportMessagingException("messaging.transport.jms.connection.close", jmse);
	}
   }

   /**
    * Libération d'une session JMS au serveur d'application
    * 
    * @param session
    * @throws TransportMessagingException
    */
   public void closeSession(final Session session) throws TransportMessagingException {
	try {
	   session.close();
	   sessions.remove(session);
	} catch (final JMSException jmse) {
	   throw new TransportMessagingException("messaging.transport.jms.session.close", jmse);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#close()
    */
   public void close() throws TransportMessagingException {

	for (final Connection conn : connections) {
	   try {
		conn.close();
	   } catch (final JMSException jmse) {
		throw new TransportMessagingException("messaging.transport.jms.connection.close", jmse);
	   }
	}

	for (final Session session : sessions) {
	   try {
		session.close();
	   } catch (final JMSException jmse) {
		throw new TransportMessagingException("messaging.transport.jms.session.close", jmse);
	   }
	}
   }

   /**
    * If not specified in context configuration, return <code>false</code>
    * 
    * @return <code>true</code> if session is transacted, <code>false</code> otherwise
    * @see JmsTransportConstants.PATH_KEY_SessionTransacted
    */
   public boolean isSessionTransacted() {

	final String strSessionTransacted = getContext().getProperty(PATH_KEY_SessionTransacted);
	return Boolean.parseBoolean(strSessionTransacted);
   }

   /**
    * You can't specify ACKNOWLEDGE value if session is transacted
    * 
    * @return Session.AUTO_ACKNOWLEDGE (1) if not specified, and otherwise :
    *         <ul>
    *         <li>1 is equivalent to Session.AUTO_ACKNOWLEDGE, automatic acknowledgement if method receive() in listener consumer don't
    *         throws exception</li>
    *         <li>2 is equivalent to Session.CLIENT_ACKNOWLEDGE, client acknowledge by explicit calling Message.acknowledge(). use it with
    *         care...</li>
    *         <li>3 is equivalent to Session.DUPS_OK_ACKNOWLEDGE, lazily acknowledge the delivery of messages (duplicate messages if the JMS
    *         provider fails)</li>
    *         </ul>
    * @see JmsTransportConstants.PATH_KEY_AcknowledgeMode
    * @see #isSessionTransacted()
    * @see javax.jms.Session.AUTO_ACKNOWLEDGE
    * @see javax.jms.Session.CLIENT_ACKNOWLEDGE
    * @see javax.jms.Session.DUPS_OK_ACKNOWLEDGE
    */
   public int getAcknowledgeMode() {
	final String strAcknowledgeMode = getContext().getProperty(PATH_KEY_AcknowledgeMode);

	if (!StringHelper.isEmpty(strAcknowledgeMode)) {
	   try {
		final int acknowledgeMode = Integer.valueOf(strAcknowledgeMode);
		return acknowledgeMode;
	   } catch (final NumberFormatException nfe) {
	   }
	}
	return Session.AUTO_ACKNOWLEDGE;
   }

   /**
    * @return Nom jdni de la ConnectionFactory
    */
   public String getConnectionFactoryJndiName() {
	return getContext().getProperty(PATH_KEY_ConnectionFactoryName);
   }

   /**
    * @return Nom du context jdni à utiliser pour accéder à la ConnectionFactory JMS
    */
   public String getConnectionFactoryJndiContextName() {
	return getContext().getProperty(PATH_KEY_ConnectionFactoryLocalRef);
   }

   /**
    * @return Nom d'utilisateur spécifique à utiliser pour instancier la connectionFactory
    */
   public String getConnectionFactoryUser() {
	return getContext().getProperty(PATH_KEY_ConnectionFactoryUser);
   }

   /**
    * @return Mot de passe utilisateur spécifique à utiliser pour instancier la connectionFactory
    */
   public String getConnectionFactoryPassword() {
	return getContext().getProperty(PATH_KEY_ConnectionFactoryPassword);
   }

   // static
   // ****************************************************************

   /**
    * @param context
    * @return Get Transport for registry or instantiate it, if needed
    */
   public static JmsTransportMessaging registeredTransport(final TransportMessagingContext context) throws TransportMessagingException {
	JmsTransportMessaging transport = null;

	// transport instance (from registry if already instantiate)
	if (TransportRegistry.isRegistered(context.getName())) {
	   final TransportMessaging transportRegistered = TransportRegistry.getTransportMessaging(context.getName());
	   if (transportRegistered instanceof JmsTransportMessaging) {
		transport = (JmsTransportMessaging) transportRegistered;
	   } else {
		throw new TransportMessagingException("messaging.error.register.illegal", new String[] { context.getName(),
			transportRegistered.getClass().getName(), JmsTransportMessaging.class.getName() });
	   }
	} else {
	   transport = new JmsTransportMessaging(context);
	   TransportRegistry.register(context.getName(), transport);
	}
	return transport;
   }
}
