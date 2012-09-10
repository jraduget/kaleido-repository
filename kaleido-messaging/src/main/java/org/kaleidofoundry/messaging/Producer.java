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

import java.util.Collection;

import org.kaleidofoundry.core.plugin.Declare;

/**
 * Messages producer
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.PRODUCER_PLUGIN)
public interface Producer extends Client {

   /**
    * @return producer name
    */
   String getName();        
   
   /**
    * @param message
    * @throws TransportException
    */
   void send(Message message) throws MessagingException;

   /**
    * Send the message (with a maximum time)
    * 
    * @param message
    * @param timeout maximum time (-1 : infinite time). if the timeout is reached, a {@link MessagingTimeoutException} is thrown
    * @throws MessagingException
    */
   void send(Message message, long timeout) throws MessagingException;
   
   /**
    * @param messages
    * @throws TransportException
    */
   void send(Collection<Message> messages) throws MessagingException;
   
   /**
    * Send the message (with a maximum time)
    * 
    * @param messages
    * @param timeout maximum time (-1 : infinite time). if the timeout is reached, a {@link MessagingTimeoutException} is thrown
    * @throws MessagingException
    */
   void send(Collection<Message> messages, long timeout) throws MessagingException;
   
   
   
}
