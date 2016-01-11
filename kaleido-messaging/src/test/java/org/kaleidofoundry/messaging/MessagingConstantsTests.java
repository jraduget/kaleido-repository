/*  
 * Copyright 2008-2016 the original author or authors 
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Constants for messaging tests
 * 
 * @author jraduget
 */
public abstract class MessagingConstantsTests {

   /*
    * Tibco RDV configuration constants
    * *******************************************
    */
   /** Path to configuration file for reliable RDV transport */
   public final static String RDV_RELIABLE_CONFIG_PATH = "classpath:/messaging/rdv/rdvConfig-reliable.properties";
   /** Path to configuration file for certified RDV transport */
   public final static String RDV_CERTIFIED_CONFIG_PATH = "classpath:/messaging/rdv/rdvConfig-certified.properties";
   /** Key in configuration file for Reliable Transport */
   public final static String RDV_RELIABLE_CONFIG_TRANSPORT_KEY = "rdv-reliable";
   /** Key in configuration file for Certified Transport */
   public final static String RDV_CERTIFIED_CONFIG_TRANSPORT_KEY = "rdv-certified";

   /** Key in configuration file for Listener */
   public final static String RDV_CONFIG_CONSUMER_KEY = "rdvconsumer-test";
   /** Key in configuration file for Publisher */
   public final static String RDV_CONFIG_PRODUCER_KEY = "rdvproducer-test";

   /*
    * JMS configuration constants
    * *******************************************
    */
   /** Path to configuration file for JMS transport */
   public final static String JMS_CONFIG_PATH = "classpath:/messaging/jms/jmsConfig.yaml";
   /** Key in configuration file for Jms Transport */
   public final static String JMS_CONFIG_TRANSPORT_KEY = "jmstransacted";
   /** Key in configuration file for Consumer */
   public final static String JMS_CONFIG_CONSUMER_KEY = "jmsconsumer";
   /** Key in configuration file for Producer */
   public final static String JMS_CONFIG_PRODUCER_KEY = "jmsproducer";

   /** Active MQ default url */
   public final static String JMS_EMBEDDED_BORKER_URL = "vm://localhost?broker.persistent=false";

   /*
    * Commons tests constants
    * *******************************************
    */
   /** Max message to send */
   public final static int MESSAGE_TOSEND_IN_TEST = 10;
   /** Max time while we are waiting after message publishing */
   public final static int TIME_TO_SLEEP_WAITING_MESSAGE = 5;
   /** Text body of TextMessage Test */
   public static final String MESSAGE_TEXT_BODY_TEST = "hello world héèh!";
   /** Xml body of XmlMessage Test */
   public static final String MESSAGE_XML_BODY_TEST = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root><country>FR</country></root>";
   /** Binary body of BinaryMessage Test */
   public static final byte[] MESSAGE_BINARY_TEST = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
   /** Bean body of JavaBeanMessage Test */
   public static final SerializableBeanSample MESSAGE_JAVABEAN_TEST = new SerializableBeanSample();

   public static Map<String, Object> buildParameters(Map<String, Object> parameters) {
	try {
	   DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	   parameters.put("paramStr", "strValue");
	   parameters.put("paramDate", dateFormat.parse("2010-03-06T00:00:00.000"));
	   parameters.put("paramCalendar", dateFormat.parse("2012-03-06T00:00:00.000"));
	   parameters.put("paramInt", new Integer(5));
	   return parameters;
	} catch (ParseException pe) {
	   throw new IllegalStateException("check date format", pe);
	}
   }

}
