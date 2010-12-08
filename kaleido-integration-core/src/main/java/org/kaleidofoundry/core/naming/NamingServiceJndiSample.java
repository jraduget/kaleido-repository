package org.kaleidofoundry.core.naming;

import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

public interface NamingServiceJndiSample {

   /**
    * use naming service to <b>get a connection from a datasource (connection pool)</b>
    * 
    * @return input message, but select from database
    * @throws SQLException
    * @throws NamingServiceException
    */
   String echoFromDatabase(String myMessage) throws SQLException;

   /**
    * use naming service to <b>send a jms message</b>
    * 
    * @param myMessage
    * @return jms message sent
    * @throws JMSException
    * @throws NamingServiceException
    */
   TextMessage echoFromJMS(String myMessage) throws JMSException;

   /**
    * use naming service to <b>call a remote or local ejb</b> <br/>
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
    * @see MyLocalBean
    * @see MyRemoteBean
    * @see MyBean
    */
   String echoFromEJB(final String message);


   /**
    * @return only use for integration testing
    */
   NamingService getNamingService();

}