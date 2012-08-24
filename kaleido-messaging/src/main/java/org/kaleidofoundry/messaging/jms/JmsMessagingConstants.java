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

/**
 * JMS Transport Constants
 * 
 * @author Jerome RADUGET
 */
public interface JmsMessagingConstants {

   /** ConnectionFactory naming service name */
   String CONNECTION_FACTORY_NAME = "namingService.name";

   /** ConnectionFactory naming service reference */
   String CONNECTION_FACTORY_NAMING_SERVICE_REF = "namingService.service-ref";

   /** ConnectionFactory url (for non jndi access) */
   String CONNECTION_FACTORY_URL = "connectionFactory.url";
   
   /** ConnectionFactory user */
   String CONNECTION_FACTORY_USER = "connectionFactory.user";

   /** ConnectionFactory password */
   String CONNECTION_FACTORY_PASSWORD = "connectionFactory.password";

   /** Session transacted property */
   String SESSION_TRANSACTED = "session.transacted";

   /** ConnectionFactory host (for provider like Websphere MQ) */
   String CONNECTION_FACTORY_HOST = "connectionFactory.host";
   
   /** ConnectionFactory port (for provider like Websphere MQ) */
   String CONNECTION_FACTORY_PORT = "connectionFactory.port";
   
   /** ConnectionFactory channel (for provider like Websphere MQ) */
   String CONNECTION_FACTORY_CHANNEL = "connectionFactory.channel";

   /** ConnectionFactory transport type (for provider like Websphere MQ) */
   String CONNECTION_FACTORY_TRANSPORT_TYPE = "connectionFactory.transportType";
   
   /** Destination type : queue|topic (queue is the default) */
   String DESTINATION_TYPE = "destination.type";

   /** Destination target client (for provider like Websphere MQ) : 1 (non jms) is the default */
   String DESTINATION_TARGET_CLIENT = "destination.targetClient";

   /** Destination persistence type (for provider like Websphere MQ) : 1 (persistent) is the default */
   String DESTINATION_PERSISTENT_TYPE = "destination.persistentType";

   
   /**
    * Session acknowledgeMode property
    * 
    * @see javax.jms.Session.AUTO_ACKNOWLEDGE
    * @see javax.jms.Session.CLIENT_ACKNOWLEDGE
    * @see javax.jms.Session.DUPS_OK_ACKNOWLEDGE
    */
   String SESSION_ACKNOLEDGE_MODE = "session.acknowledgeMode";

   /** Consumer thread pool count */
   String CONSUMER_MESSAGE_SELECTOR_PROPERTY = "message.selector";

   /** <code>true|false</code> If true, inhibit the delivery of messages published by its own connection. Default value is false */
   String CONSUMER_NOLOCAL_PROPERTY = "message.noLocal";

   /** Destination name of the consumer (queue or topic) - it can be the jndi or phycical queue / topic name */
   String CONSUMER_DESTINATION = "destination.name";

   /** Destinations names separated by {@link #PRODUCER_DESTINATIONS_SEPARATOR} - it can be the jndi or phycical queue / topic name*/
   String PRODUCER_DESTINATIONS = "destination.names";

   /** Destinations names separator */
   String PRODUCER_DESTINATIONS_SEPARATOR = ";";

   /**
    * 0 (lower) - 9 (highest)
    * 0-4 as gradations of normal priority
    * 5-9 gradations of expedited priority.
    * 
    * @see javax.jms.Message.getJMSPriority()
    */
   String MESSAGE_PRIORITY = "message.priority";
   /**
    * 0 : no message expiration
    * 
    * @see javax.jms.Message.getJMSExpiration()
    */
   String MESSAGE_EXPIRATION = "message.expiration";

   /** async , sync */
   String MESSAGE_CONSUMPTION = "message.consumption";

   /** Full date format for date handling in message */
   String MESSAGE_FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
