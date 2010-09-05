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
 * Implémentation abstraite de ConsumerMessaging
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractConsumerMessaging implements ConsumerMessaging {

   private ConsumerMessagingContext context;
   private TransportMessaging transport;

   /**
    * @param context
    */
   public AbstractConsumerMessaging(final ConsumerMessagingContext context) {
	this.context = context;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ClientMessaging#getTransport()
    */
   public TransportMessaging getTransport() throws TransportMessagingException {
	return transport;
   }

   protected void setTransport(final TransportMessaging transport) throws TransportMessagingException {
	this.transport = transport;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.ClientMessaging#getContext()
    */
   public ConsumerMessagingContext getContext() {
	return context;
   }

   protected void setContext(final ConsumerMessagingContext context) {
	this.context = context;
   }

}
