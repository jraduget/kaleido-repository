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
 * Constants for TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public interface TransportMessagingConstants {

   /*
    * Constants for context configuration
    * **********************************************************
    */
   /** Prefix for all transport messaging context configuration */
   public static final String PREFIX_Transport_Property = "messaging.transport";

   /** Prefix for all publisher messaging context configuration */
   public static final String PREFIX_Producer_Property = "messaging.producer";

   /** Prefix for all listener messaging context configuration */
   public static final String PREFIX_Listener_Property = "messaging.consumer";

   /** Suffix for path key config, specifying transport (local-ref), for consumer and producer... */
   public static final String SUFFIX_TransportLocalRef_Property = "transport.local-ref";

   /**
    * Kind of message drive by transport :
    * <ul>
    * <li>xml</li>
    * <li>binary</li>
    * </ul>
    */
   public static final String SUFFIX_MessageType_Property = "message";

   /*
    * Constants for message field
    * **********************************************************
    */
   /** Type du message véhiculer */
   public static final String MessageTypeField = "message.type";

   /** Nom de la données du message contenant le corps xml */
   public static final String MessageXmlBodyField = "xml.body";

   /** Nom de la données du message contenant le binaire */
   public static final String MessageBinaryBodyField = "binary.body";
}
