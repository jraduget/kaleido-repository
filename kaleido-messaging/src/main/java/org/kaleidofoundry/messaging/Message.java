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
