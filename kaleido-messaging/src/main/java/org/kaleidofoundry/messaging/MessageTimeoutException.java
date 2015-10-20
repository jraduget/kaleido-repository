/*  
 * Copyright 2008-2014 the original author or authors 
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
 * @author jraduget
 */
public class MessageTimeoutException extends MessageException {

   private static final long serialVersionUID = 9005545543717544311L;

   private MessageTimeoutException(String code, String... parameters) {
	super(code, parameters);
   }

   public static MessageTimeoutException buildConsumerTimeoutException(String workername) {
	return new MessageTimeoutException("messaging.consumer.receive.timeout", workername);
   }

   public static MessageTimeoutException buildProducerTimeoutException(String workername) {
	return new MessageTimeoutException("messaging.producer.sent.timeout", workername);
   }
}
