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
package org.kaleidofoundry.core.naming;

import java.sql.SQLException;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple JNDI naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation without
 * parameters, but using external configuration
 * </p>
 * <p>
 * <b>Several examples here:</b>
 * <ul>
 * <li>echo a user message from the database : {@link #echoFromDatabase(String)} lookup a datasource, and test an jdbc sql query</li>
 * <li>echo a message from jms message : {@link #echoFromJMS(String)} lookup a jms resources (connection factory, queue, ...)</li>
 * <li>echo a message from an ejb call : {@link #echoFromEJB(String)} lookup an ejb and call its method</li>
 * </ul>
 * </p>
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/naming/myContext.properties
 * </pre>
 * 
 * <b>Resource file :</b> "classpath:/naming/myContext.properties" contains : <br/>
 * <br/>
 * <b>in a local environment :</b>
 * 
 * <pre>
 * # for a local naming service
 * # -> it is only an example. by default : caching and failover are disabled. Moreover no need of failover in a local context
 * naming.myNamingService.caching=false
 * naming.myNamingService.caching.strategy=none
 * naming.myNamingService.failover.enabled=false
 * </pre>
 * 
 * <b>or in a remote environment :</b>
 * 
 * <pre>
 * # for a remote naming service (glassfish v3 server here)
 * naming.myNamingService.java.naming.factory.initial=com.sun.enterprise.naming.SerialInitContextFactory
 * naming.myNamingService.java.naming.factory.url.pkgs=com.sun.enterprise.naming
 * naming.myNamingService.java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl
 * naming.myNamingService.org.omg.CORBA.ORBInitialHost=127.0.0.1
 * naming.myNamingService.org.omg.CORBA.ORBInitialPort=3700
 * 
 * # caching and failover policies in a remote context
 * naming.myNamingService.caching=all
 * naming.myNamingService.caching.strategy=global
 * naming.myNamingService.failover.enabled=true
 * naming.myNamingService.failover.wait=2000
 * naming.myNamingService.failover.maxretry=5
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class NamingServiceJndiSample01 implements NamingServiceJndiSample {

   @Context
   protected NamingService myNamingService;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromDatabase(java.lang.String)
    */
   @Override
   public String echoFromDatabase(final String myMessage) throws SQLException {
	return echoFromDatabase(myNamingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromJMS(java.lang.String)
    */
   @Override
   public TextMessage echoFromJMS(final String myMessage) throws JMSException {
	return echoFromJMS(myNamingService, myMessage);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.naming.NamingServiceJndiSample#echoFromEJB(java.lang.String)
    */
   @Override
   public String echoFromEJB(final String message) {
	return echoFromEJB(myNamingService, message);
   }

   // **************************************************************************************************************************************
   // commons static methods to test (these methods are used by several junit launcher)
   // **************************************************************************************************************************************

   @Override
   public NamingService getNamingService() {
	return myNamingService;
   }

   /**
    * @param namingService
    * @param myMessage
    * @return myMessage select from database
    * @throws SQLException
    */
   public static String echoFromDatabase(final NamingService namingService, final String myMessage) throws SQLException {

	// get remote or local datasource
	javax.sql.DataSource myDatasource = namingService.locate("jdbc/kaleido", DataSource.class);

	// standard jdbc class to execute a sql request
	java.sql.Connection connection = null;
	java.sql.Statement st = null;
	java.sql.ResultSet rst = null;

	try {
	   // return a connection from the pool
	   connection = myDatasource.getConnection();

	   // select the given input message
	   st = connection.createStatement();
	   rst = st.executeQuery("SELECT '" + myMessage + "'from sysibm.sysdummy1"); // oracle dual equivalent for derby

	   if (rst != null && rst.next()) {
		return rst.getString(1);
	   } else {
		return null;
	   }

	} finally {
	   // cleanup resource
	   try {
		if (rst != null) {
		   rst.close();
		}
	   } catch (SQLException sqle) { /* handle it, fatal log ... */
	   }
	   try {
		if (st != null) {
		   st.close();
		}
	   } catch (SQLException sqle) { /* handle it, fatal log ... */
	   }
	   try {
		if (connection != null) {
		   connection.close();
		}
	   } catch (SQLException sqle) { /* handle it, fatal log ... */
	   }
	}
   }

   /**
    * @param namingService
    * @param myMessage
    * @return the sent jms message
    * @throws JMSException
    */
   public static TextMessage echoFromJMS(final NamingService namingService, final String myMessage) throws JMSException {

	javax.jms.Connection connection = null;
	javax.jms.Session session = null;

	try {
	   // connection factory lookup and session creation (queue or topic connection factory)
	   final ConnectionFactory connectionFactory = namingService.locate("jms/kaleidoQueueFactory", ConnectionFactory.class);
	   connection = connectionFactory.createConnection();
	   session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	   // message creation, using current session
	   final TextMessage message = session.createTextMessage();
	   message.setText(myMessage);

	   // message production for the given destination (queue or topic)
	   final Destination destination = namingService.locate("jms/kaleidoQueue", Queue.class);
	   final MessageProducer producer = session.createProducer(destination);
	   producer.send(message);

	   return message;

	} finally {
	   try {
		if (session != null) {
		   session.close();
		}
	   } finally {
		if (connection != null) {
		   connection.close();
		}
	   }
	}
   }

   /**
    * @param namingService
    * @param message
    * @return the input message parameter, echo from the remote ejb
    */
   public static String echoFromEJB(final NamingService namingService, final String message) {

	// get ejb client service (remote or local)
	// here, the ejb bean is annotated @Stateless(mappedName="ejb/MyBean")
	// if there is no mappedName you can still used the global name (jee6): "java:global/kaleido-it-ear/kaleido-it-jee5/MyBean"
	MyRemoteBean myRemoteBean = namingService.locate("ejb/MyBean", MyRemoteBean.class);

	// call it
	return myRemoteBean.echo(message);
   }
}
