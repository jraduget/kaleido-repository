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

import org.kaleidofoundry.messaging.jms.JmsTransport;
import org.kaleidofoundry.messaging.jms.amq.AmqTransport;
import org.kaleidofoundry.messaging.jms.ibm.WebsphereMQTransport;
import org.kaleidofoundry.messaging.rdv.RdvTransport;

/**
 * Transport provider enumeration
 * 
 * @author Jerome RADUGET
 */
public enum TransportProviderEnum {

   /** JMS 1.1, using JNDI to access ConnectionFactory */
   jms("jms", "1.1", "jms provider", JmsTransport.class),
   
   /** AMQ 5.x using direct ConnectionFactory creation */
   amq("amq", "5.x", "active mq provider", AmqTransport.class),

   /** Webspjere MQ using direct ConnectionFactory creation */
   wmq("wmq", "6.x", "websphere mq provider", WebsphereMQTransport.class),
   
   /** RVD 7.5 */
   rdv("rdv", "7.5", "tibco rdv prodiver", RdvTransport.class);

   private String code;
   private String version;
   private String description;
   private Class<? extends Transport> implementation;

   TransportProviderEnum(final String code, final String version, String description, final Class<? extends Transport> implementation) {
	this.code = code;
	this.version = version;
	this.implementation = implementation;
	this.description = description;
   }

   /**
    * @return Transport unique code
    */
   public String getCode() {
	return code;
   }

   /**
    * @return Transport provider version
    */
   public String getVersion() {
	return version;
   }

   /**
    * @return transport description
    */
   public String getDescription() {
	return description;
   }

   /**
    * @return Implementation class
    */
   public Class<? extends Transport> getImplementation() {
	return implementation;
   }

}
