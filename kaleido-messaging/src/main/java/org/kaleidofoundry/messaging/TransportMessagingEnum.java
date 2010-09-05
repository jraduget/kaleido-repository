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

import org.kaleidofoundry.messaging.jms.JmsTransportMessaging;
import org.kaleidofoundry.messaging.rdv.RdvTransportMessaging;

/**
 * Enumération des implementations de Transport possible pour le framework
 * 
 * @author Jerome RADUGET
 */
public enum TransportMessagingEnum {

   /** JMS 1.1 */
   SunJMS("JMS-1.1", "1.1", JmsTransportMessaging.class),
   /** RVD 7.5 */
   TibcoRDV("Tibco-RDV-7.5", "7.5", RdvTransportMessaging.class);

   private String code;
   private String version;
   private Class<? extends TransportMessaging> implementation;

   TransportMessagingEnum(final String code, final String version, final Class<? extends TransportMessaging> implementation) {
	this.code = code;
	this.version = version;
	this.implementation = implementation;
   }

   /**
    * @return Code unique de l'implémentation
    */
   public String getCode() {
	return code;
   }

   /**
    * @return Version (API) de l'implémentation du transport
    */
   public String getVersion() {
	return version;
   }

   /**
    * @return Classe d'implémentation
    */
   public Class<? extends TransportMessaging> getImplementation() {
	return implementation;
   }

}
