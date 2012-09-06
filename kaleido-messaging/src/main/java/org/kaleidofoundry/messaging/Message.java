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

import java.util.Map;

/**
 * Interface used to produce / consume messages
 * 
 * @author Jerome RADUGET
 * 
 * @see Transport
 * @see Producer
 * @see Consumer
 */
public interface Message {

   // TODO : add uuid, timestamp (long), error destination, priority (int), expiration date (long)   
   
   /**
    * @return internal provider id 
    */
   String getProviderId();
   
   /**
    * @return Correlation Id (may be unused if supported by the provider)
    */
   String getCorrelationId();
   
   /**
    * @return Kind of message (XML, binary, javaBean, ... )
    * @see MessageTypeEnum
    */
   MessageTypeEnum getType();

   /**
    * @return Parameters linked to message
    *         this values must be only primitives types (Integer, Double, Long ...), String objects, and byte arrays
    */
   Map<String, Object> getParameters();

   /**
    * @return String representation
    */
   String toString();
}
