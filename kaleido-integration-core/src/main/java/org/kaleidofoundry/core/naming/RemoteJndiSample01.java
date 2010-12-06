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
 * <h3>Simple naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation without
 * parameters, but using external configuration
 * </p>
 * 
 * <ul>	
 * 	<li>echo message from database : {@link #echoFromDatabase(String)} lookup a datasource, and test an jdbc sql query</li>
 * 	<li>echo message from jms message : {@link #echoFromJMS(String)} lookup a jms resources (connection factory, queue, ...) and test an jdbc sql query</li>
 * 	<li>echo message from ejb call : {@link #echoFromEJB(String)} lookup a remote ejb</li> 
 * </ul>
 * 
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/naming/myContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/naming/myContext.properties" contains :
 * 
 * <pre>
 * # naming service for glassfish v3 server
 * naming.myRemoteCtx.java.naming.factory.initial=com.sun.enterprise.naming.SerialInitContextFactory
 * naming.myRemoteCtx.java.naming.factory.url.pkgs=com.sun.enterprise.naming
 * naming.myRemoteCtx.java.naming.factory.state=com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl
 * naming.myRemoteCtx.org.omg.CORBA.ORBInitialHost=127.0.0.1
 * naming.myRemoteCtx.org.omg.CORBA.ORBInitialPort=3700
 * # caching and failover policies in a remote context
 * naming.myRemoteCtx.caching=all
 * naming.myRemoteCtx.caching.strategy=global
 * naming.myRemoteCtx.failover.enabled=true
 * naming.myRemoteCtx.failover.wait=2000
 * naming.myRemoteCtx.failover.maxretry=5
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class RemoteJndiSample01 {

   @Context("myRemoteCtx")
   private NamingService namingService;

   /**
    * use naming service to <b>get a connection from a datasource (connection pool)</b>
    * 
    * @return
    * @throws SQLException
    * @throws NamingServiceException
    */
   public String echoFromDatabase(String myMessage) throws SQLException {

	// get remote datasource
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
	   rst = st.executeQuery("SELECT '" + myMessage + "'from sysibm.sysdummy1");
	   
	   if (rst != null && rst.next()) {
		return rst.getString(1);
	   } else {
		return null;
	   }
	   
	} finally {
	   // cleanup resource
	   try { if (rst != null) { rst.close(); }} catch (SQLException sqle) {}  
	   try { if (st != null) { st.close(); }} catch (SQLException sqle) {}
	   try { if (connection != null) { connection.close(); }} catch (SQLException sqle) {}
	}
   }
   
   /**
    * use naming service to <b>send a jms message</b>
    * 
    * @param myMessage
    * @return jms message sent
    * @throws JMSException
    * @throws NamingServiceException
    */
   public TextMessage echoFromJMS(String myMessage) throws JMSException {

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
		if (session != null) { session.close(); }
	   } finally {
		if (connection != null) { connection.close(); }
	   }
	}
   }   
   
   /**
    * use naming service to <b>call a remote ejb</b>
    * <br/>
    * <br/> 
    * Stdout for echoFromEJB("hello world")":
    * 
    * <pre>
    * hello world
    * </pre>
    * 
    * @param message
    * @return input message
    * @throws NamingServiceException
    */
   public String echoFromEJB(final String message) {

	// get ejb client service (remote)
	MyRemoteBean myRemoteBean = namingService.locate("java:global/kaleido-integration-ear/kaleido-integration-ejb/MyBean", MyRemoteBean.class);

	// call it
	return myRemoteBean.echo(message);
   }   
}
