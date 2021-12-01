/*  
 * Copyright 2008-2021 the original author or authors 
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

import org.kaleidofoundry.core.plugin.Declare;

/**
 * Messages consumer
 * 
 * @author jraduget
 */
@Declare(MessagingConstants.CONSUMER_PLUGIN)
public interface Consumer extends Client {

   /**
    * @return consumer name
    */
   String getName();

   /**
    * Start the the consumer processing
    * 
    * @throws TransportException
    */
   void start() throws TransportException;

   /**
    * Stop the consumer processing
    * 
    * @throws TransportException
    */
   void stop() throws TransportException;

   /**
    * Add a message handler for each received message. <br/>
    * It is a processing chain, and {@link MessageHandler} will be executed in the insertion order. <br/>
    * During the chain processing, if an {@link MessageHandler#onReceive(Message)}, the rest of the chain will not be executed
    * 
    * @param handler
    * @return current consumer instance
    */
   Consumer addMessageHandler(MessageHandler handler);

}
