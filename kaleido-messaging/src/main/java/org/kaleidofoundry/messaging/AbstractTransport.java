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

import static org.kaleidofoundry.messaging.TransportContextBuilder.TRANSPORT_PROVIDER;

import java.util.Map;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Abstract TransportMessaging
 * 
 * @author jraduget
 */
public abstract class AbstractTransport implements Transport {

   protected String providerCode;
   protected String providerVersion;
   protected final RuntimeContext<Transport> context;

   protected final Registry<String, Consumer> ConsumerRegistry = new Registry<String, Consumer>();
   protected final Registry<String, Producer> ProducerRegistry = new Registry<String, Producer>();

   /**
    * @param context
    * @throws TransportException
    */
   public AbstractTransport(@NotNull final RuntimeContext<Transport> context) throws TransportException {
	this.context = context;
	this.providerCode = context.getString(TRANSPORT_PROVIDER);
	if (StringHelper.isEmpty(this.providerCode)) { throw new EmptyContextParameterException(TRANSPORT_PROVIDER, context); }
	this.providerVersion = TransportProviderEnum.valueOf(providerCode) != null ? TransportProviderEnum.valueOf(providerCode).getVersion() : null;
   }

   public String getProviderCode() {
	return providerCode;
   }

   public String getProviderVersion() {
	return providerVersion;
   }

   @Override
   public Map<String, Consumer> getConsumers() {
	return ConsumerRegistry;
   }

   @Override
   public Map<String, Producer> getProducers() {
	return ProducerRegistry;
   }

   @Override
   public void close() throws TransportException {
	for (Consumer consumer : ConsumerRegistry.values()) {
	   consumer.stop();
	}
	for (Producer producer : ProducerRegistry.values()) {
	   producer.stop();
	}
   }

}
