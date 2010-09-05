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

/**
 * Interface TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public interface TransportMessaging {

   /**
    * @return Code unique descrivant le transport
    */
   String getCode();

   /**
    * @return Version de l'implementation du transport
    */
   String getVersion();

   /**
    * @return Contexte de connection au Transport
    */
   TransportMessagingContext getContext();

   /**
    * Fermeture et libération des ressources
    * 
    * @throws TransportMessagingException
    */
   void close() throws TransportMessagingException;

   /**
    * @return Transport drive xml messages
    */
   boolean isXmlMessage();

   /**
    * @return Transport drive binary messages
    */
   boolean isBinaryMessage();
}
