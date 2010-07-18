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

import java.util.HashMap;
import java.util.Map;

/**
 * Registry des Transports instanciés
 * 
 * @author Jerome RADUGET
 */
public class TransportRegistry {

   private static final Map<String, TransportMessaging> registry = new HashMap<String, TransportMessaging>();

   /**
    * Récupère un TransportMessaging précédemment enregistré
    * 
    * @param name
    * @return
    * @throws TransportRegistryException Si non enregistré
    */
   public static TransportMessaging getTransportMessaging(final String name) throws TransportRegistryException {
	TransportMessaging ds = null;
	ds = registry.get(name);
	if (ds == null) { throw new TransportRegistryException("messaging.error.register.none", new String[] { name }); }
	return ds;
   }

   /**
    * Enregistre un nouveau TransportMessaging
    * 
    * @param name
    * @param messaging
    * @throws TransportRegistryException Si déjà enregistré
    */
   public static void register(final String name, final TransportMessaging messaging) throws TransportRegistryException {
	synchronized (registry) {
	   if (registry.get(name) == null) {
		registry.put(name, messaging);
	   } else {
		throw new TransportRegistryException("messaging.error.register.already", new String[] { name });
	   }
	}
   }

   /**
    * Désenregistre un TransportMessaging existant
    * 
    * @param name
    */
   public static void unregister(final String name) {
	synchronized (registry) {
	   registry.remove(name);
	}
   }

   /**
    * @param name
    * @return
    */
   public static boolean isRegistered(final String name) {
	return registry.get(name) != null;
   }
}
