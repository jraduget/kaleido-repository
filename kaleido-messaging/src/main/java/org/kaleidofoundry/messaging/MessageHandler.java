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

/**
 * Message handler that could be defined for a {@link Consumer}
 * 
 * @author jraduget
 */
public interface MessageHandler {

   /**
    * The processing when a message is received without error
    * 
    * @param message
    * @return <code>true|false</code> true to continue message consumer processing, false to abort it
    * @throws MessageException exception due to the message content
    * @throws Throwable all other kind of exception which will be handled in {@link #onError(Message, Throwable)}
    */
   boolean onReceive(Message message) throws MessageException, Throwable;

   /**
    * The processing error, when a messaging exception occurred when a message is received or processed in the message handler chain<br/>
    * <ul>
    * <li>If message parameter is null, it means that no message has been yet built at this step.</li>
    * <li>If message parameter is not null, the exception occurred in the message handler processing : {@link #onReceive(Message)}</li>
    * </ul>
    * <p>
    * The exception handling can't be blocking, because the consumer stay alive for other incoming messages. So you can log the error (done
    * by default), audit the error in a database, send the message to an error queue that is processed manually...
    * </p>
    * 
    * @param message it can be null is the error occurred while receiving the message
    * @param th the exception
    */
   void onError(Message message, Throwable th);

}
