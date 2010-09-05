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

import java.util.Map;

/**
 * Message interface for TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public interface Message {

   /**
    * @return Kind of message (XML, binary, javaBean, ... )
    */
   MessageTypeEnum getType();

   /**
    * @return Parameters linked to message
    */
   Map<String, Object> getParameters();

   /**
    * @param parameters
    *           parameters values must be only primitives types (Integer, Double, Long ...), String objects, and byte arrays
    */
   void setParameters(Map<String, Object> parameters);

   /**
    * @return String representation
    */
   String toString();
}
