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
package org.kaleidofoundry.messaging;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants for messagings tests
 * 
 * @author Jerome RADUGET
 */
public class MessagingTestsConstants {

   /*
    * Tibco RDV configuration constantes
    * *******************************************
    */
   /** Path to configuration file for reliable RDV transport */
   public final static String RdvReliabledConfigPath = "classpath:/messaging/rdv/rdvConfig-reliable.properties";
   /** Path to configuration file for certified RDV transport */
   public final static String RdvCertifiedConfigPath = "classpath:/messaging/rdv/rdvConfig-certified.properties";
   /** Key in configuration file for Reliable Transport */
   public final static String RdvTransportReliableConfigKey = "rdv-reliable";
   /** Key in configuration file for Certified Transport */
   public final static String RdvTransportCertifiedConfigKey = "rdv-certified";

   /** Key in configuration file for Listener */
   public final static String RdvListenerConfigKey = "rdvconsumer-test";
   /** Key in configuration file for Publisher */
   public final static String RdvPublisherConfigKey = "rdvproducer-test";

   /*
    * JMS configuration constantes
    * *******************************************
    */
   /** Path to configuration file for JMS transport */
   public final static String JmsConfigPath = "classpath:/messaging/jms/jmsConfig.properties";
   /** Key in configuration file for Jms Transport */
   public final static String JmsTransportConfigKey = "jms-transacted";
   /** Key in configuration file for Consummer */
   public final static String JmsConsumerConfigKey = "jmsconsumer-test";
   /** Key in configuration file for Producer */
   public final static String JmsProducerConfigKey = "jmsproducer-test";

   /*
    * Commons tests constants
    * *******************************************
    */
   /** Max message to send */
   public final static int MessageToSendInTest = 10;
   /** Max time while we are waiting after message publising */
   public final static int TimeToSleepWaitingMessage = 5;
   /** Xml body of XmlMessage Test */
   public static final String MessageXmlBodyTest = "<root><country>FR</country></root>";
   /** Binary body of BinaryMessage Test */
   public static final byte[] MessageBinaryBodyTest = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
   /** Bean body of JavaBeanMessage Test */
   public static final SerializableBeanSample MessageJavaBeanBodyTest = new SerializableBeanSample();

   /** Parameters for Message Test */
   public static final Map<String, Object> MessageParameters;

   static {
	MessageParameters = new HashMap<String, Object>();
	MessageParameters.put("paramStr", "strValue");
	MessageParameters.put("paramDate", new GregorianCalendar(2008, 02, 06, 0, 0, 0).getTime());
	MessageParameters.put("paramInt", new Integer(5));
   }
}
