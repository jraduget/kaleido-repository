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

import static org.kaleidofoundry.messaging.MessagingConstants.I18N_RESOURCE;
import static org.kaleidofoundry.messaging.MessagingConstants.PRODUCER_DEBUG_PROPERTY;
import static org.kaleidofoundry.messaging.MessagingConstants.PRODUCER_TRANSPORT_REF;

import java.util.concurrent.atomic.AtomicInteger;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractProducer implements Producer {

   /** Default Logger */
   protected static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

   // message counters
   protected final AtomicInteger ProcessedMessagesOK = new AtomicInteger(0);
   protected final AtomicInteger ProcessedMessagesKO = new AtomicInteger(0);
   protected final AtomicInteger ProcessedMessagesSKIPPED = new AtomicInteger(0);

   /** I18n messaging bundle */
   protected final I18nMessages MESSAGING_BUNDLE = I18nMessagesFactory.provides(I18N_RESOURCE, InternalBundleHelper.CoreMessageBundle);
   protected final RuntimeContext<Producer> context;
   protected final Transport transport;

   public AbstractProducer(RuntimeContext<Producer> context) {
	this.context = context;

	// transport
	String transportRef = this.context.getString(PRODUCER_TRANSPORT_REF);
	if (!StringHelper.isEmpty(transportRef)) {
	   this.transport = TransportFactory.provides(transportRef, context);
	} else {
	   throw new EmptyContextParameterException(PRODUCER_TRANSPORT_REF, context);
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getTransport()
    */
   @Override
   public Transport getTransport() throws TransportException {
	return transport;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Producer#getName()
    */
   @Override
   public String getName() {
	return context.getName();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getStatistics()
    */
   @Override
   public UsageStatistics getStatistics() {
	return new UsageStatistics(ProcessedMessagesOK.get(), ProcessedMessagesKO.get(), 0);
   }

   protected boolean isDebug() {
	return context.getBoolean(PRODUCER_DEBUG_PROPERTY, false) || LOGGER.isDebugEnabled();
   }

   protected void debugMessage(Message message) {
	if (isDebug()) {
	   LOGGER.info(">>> sending message with providerId={} , correlationId={} , parameters={}",  new String[] {message.getProviderId(), message.getCorrelationId(), String.valueOf(message.getParameters()) });
	   LOGGER.info("{}", message.toString());
	}
   }

}
