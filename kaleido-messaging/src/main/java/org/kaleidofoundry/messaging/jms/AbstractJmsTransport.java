/*  
 * Copyright 2008-2012 the original author or authors 
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

import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_PASSWORD;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_URL;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_CONNECTION_FACTORY_USER;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_SESSION_ACKNOLEDGE_MODE;
import static org.kaleidofoundry.messaging.TransportContextBuilder.JMS_SESSION_TRANSACTED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractTransport;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Jms Transport
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractJmsTransport<CF extends ConnectionFactory, C extends Connection, D extends Destination> extends AbstractTransport implements
	Transport {

   static final Logger LOGGER = LoggerFactory.getLogger(AbstractJmsTransport.class);

   final List<Session> sessions;
   final List<Connection> connections;

   /**
    * @param context
    * @throws TransportException
    */
   public AbstractJmsTransport(final RuntimeContext<Transport> context) throws TransportException {
	super(context);

	checkContext();

	// Memorize user session and connections
	sessions = Collections.synchronizedList(new ArrayList<Session>());
	connections = Collections.synchronizedList(new ArrayList<Connection>());

   }

   /**
    * @return Current connection factory
    * @throws TransportException
    */
   protected abstract CF getConnectionFactory() throws TransportException;
   
   /**
    * @param session
    * @param name
    * @return Destination {@link Queue} / {@link Topic} / {@link TemporaryQueue} / {@link TemporaryTopic}
    * @throws TransportException
    */
   protected abstract D getDestination(Session session, String name) throws TransportException;

   /**
    * Consistency check
    * 
    * @throws TransportException
    */
   protected abstract void checkContext();

   /**
    * JMS connection creation
    * 
    * @return
    * @throws TransportException
    */
   @SuppressWarnings("unchecked")
   public C createConnection() throws TransportException {

	try {
	   final Connection connection;

	   // create connection with default jndi user information
	   if (StringHelper.isEmpty(getUser())) {
		connection = getConnectionFactory().createConnection();
	   }
	   // create connection with custom user / password configured
	   else {
		connection = getConnectionFactory().createConnection(getUser(), getPassword());
	   }
	   connections.add(connection);

	   return (C) connection;
	} catch (final JMSException jmse) {
	   throw new TransportException("messaging.transport.jms.connection.create", jmse);
	}
   }

   /**
    * Create a jms session with an existing connection
    * Regarding the transport configuration :
    * <ul>
    * <li>This session could be transactional or not</li>
    * <li>This session could be shared between thread or not</li>
    * <li>The messages acknowledged policies could be set</li>
    * <li>The open session (which have not been closed) are memorized, and could be cleaned and closed when transport is closed</li>
    * </ul>
    * 
    * @return a new jms session
    * @throws TransportException
    */
   public Session createSession(@NotNull Connection connection) throws TransportException {
	try {
	   // User session for transactional use
	   final Session session = connection.createSession(isSessionTransacted(), getAcknowledgeMode());
	   sessions.add(session);
	   return session;
	} catch (final JMSException jmse) {
	   throw new TransportException("messaging.transport.jms.session.create", jmse);
	}
   }

   /**
    * close the given connection
    * 
    * @param conn
    * @throws TransportException
    */
   public void closeConnection(final Connection conn) throws TransportException {
	if (conn == null) { return;}
	try {
	   conn.close();
	   connections.remove(conn);
	} catch (final JMSException jmse) {
	   throw new TransportException("messaging.transport.jms.connection.close", jmse);
	}
   }

   /**
    * close the given session
    * 
    * @param session
    * @throws TransportException
    */
   public void closeSession(final Session session) throws TransportException {
	if (session == null) { return;}
	try {
	   session.close();
	   sessions.remove(session);
	} catch (final JMSException jmse) {
	   throw new TransportException("messaging.transport.jms.session.close", jmse);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.TransportMessaging#close()
    */
   public void close() throws TransportException {

	super.close();
	
	Iterator<Connection> connIterator = connections.iterator();
	while (connIterator.hasNext()) {
	   try {
		Connection conn = connIterator.next();
		conn.close();
		connIterator.remove();
	   } catch (final JMSException jmse) {
		throw new TransportException("messaging.transport.jms.connection.close", jmse);
	   }
	}

	Iterator<Session> sessionIterator = sessions.iterator();
	while (sessionIterator.hasNext()) {
	   try {
		Session session = sessionIterator.next();
		session.close();
		sessionIterator.remove();
	   } catch (final JMSException jmse) {
		throw new TransportException("messaging.transport.jms.session.close", jmse);
	   }
	}
		
   }

   /**
    * If not specified in context configuration, return <code>false</code>
    * 
    * @return <code>true</code> if session is transacted, <code>false</code> otherwise
    * @see JmsMessagingConstants.PATH_KEY_SessionTransacted
    */
   public boolean isSessionTransacted() {
	final String strSessionTransacted = context.getString(JMS_SESSION_TRANSACTED);
	return Boolean.parseBoolean(strSessionTransacted);
   }

   /**
    * You can't specify ACKNOWLEDGE value if session is transacted
    * 
    * @return Session.AUTO_ACKNOWLEDGE (1) if not specified, and otherwise :
    *         <ul>
    *         <li>1 is equivalent to Session.AUTO_ACKNOWLEDGE, automatic acknowledgment if method receive() in listener consumer don't
    *         throws exception</li>
    *         <li>2 is equivalent to Session.CLIENT_ACKNOWLEDGE, client acknowledge by explicit calling Message.acknowledge(). use it with
    *         care...</li>
    *         <li>3 is equivalent to Session.DUPS_OK_ACKNOWLEDGE, lazily acknowledge the delivery of messages (duplicate messages if the JMS
    *         provider fails)</li>
    *         </ul>
    * @see JmsMessagingConstants.PATH_KEY_AcknowledgeMode
    * @see #isSessionTransacted()
    * @see javax.jms.Session.AUTO_ACKNOWLEDGE
    * @see javax.jms.Session.CLIENT_ACKNOWLEDGE
    * @see javax.jms.Session.DUPS_OK_ACKNOWLEDGE
    */
   public int getAcknowledgeMode() {
	final String strAcknowledgeMode = context.getString(JMS_SESSION_ACKNOLEDGE_MODE);

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
    * @return ConnectionFactory url (for non jndi access)
    */
   public String getURL() {
	return context.getString(JMS_CONNECTION_FACTORY_URL);
   }

   /**
    * @return ConnectionFactory user name
    */
   public String getUser() {
	return context.getString(JMS_CONNECTION_FACTORY_USER);
   }

   /**
    * @return ConnectionFactory user password
    */
   public String getPassword() {
	return context.getString(JMS_CONNECTION_FACTORY_PASSWORD);
   }

}
