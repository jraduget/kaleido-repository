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
package org.kaleidofoundry.messaging;


/**
 * Constants for messaging module
 * 
 * @author Jerome RADUGET
 */
public interface MessagingConstants {
   
   /** I18N resource name for messaging */
   String I18N_RESOURCE = "i18n/messaging/messages";
   
   /** Transport interface plugin name */
   String TRANSPORT_PLUGIN = "messaging.transports";
   /** JMS transport plugin name (via JNDI access) */
   String JMS_TRANSPORT_PLUGIN = "messaging.transports.jms";
   /** AMQ transport plugin name */
   String AMQ_TRANSPORT_PLUGIN = "messaging.transports.amq";   
   /** Websphere MQ transport plugin name*/
   String WMQ_TRANSPORT_PLUGIN = "messaging.transports.wmq";
   /** Tibco RDV transport plugin name */
   String RDV_TRANSPORT_PLUGIN = "messaging.transports.rdv";   
   
   /** Producer plugin name */
   String PRODUCER_PLUGIN = "messaging.producers";
   /** JMS producer plugin name */
   String JMS_PRODUCER_PLUGIN = "messaging.producers.jms";
   /** AMQ producer plugin name */
   String AMQ_PRODUCER_PLUGIN = "messaging.producers.amq";
   /** Websphere MQ producer plugin name */
   String WMQ_PRODUCER_PLUGIN = "messaging.producers.wmq";   
   /** Tibco RDV producer plugin name */
   String RDV_PRODUCER_PLUGIN = "messaging.producers.rdv";
      
   /** Consumer plugin name */
   String CONSUMER_PLUGIN = "messaging.consumers";
   /** Jms consumer plugin name */
   String JMS_CONSUMER_PLUGIN = "messaging.consumers.jms";
   /** Jms AMQ consumer plugin */
   String AMQ_CONSUMER_PLUGIN = "messaging.consumers.amq";
   /** Websphere MQ consumer plugin */
   String WMQ_CONSUMER_PLUGIN = "messaging.consumers.wmq";   
   /** Tibco Rdv consumer plugin name */
   String RDV_CONSUMER_PLUGIN = "messaging.consumers.rdv";
   
  
   
   /** message field which can store the unique id of the message */
   String MESSAGE_ID_FIELD = "$id";
   
   /** message field which can store the type */
   String MESSAGE_TYPE_FIELD = "$type";

   /** message field which can store the text */
   String MESSAGE_BODY_TEXT_FIELD = "$text";

   /** message field which can store the bytes data */
   String MESSAGE_BODY_BYTES_FIELD = "$bytes";
   
   /** message field which can store the java bean */
   String MESSAGE_BODY_BEAN_FIELD = "$bean";
      
}
