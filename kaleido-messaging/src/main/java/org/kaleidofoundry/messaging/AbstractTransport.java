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

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

import static org.kaleidofoundry.messaging.MessagingConstants.TRANSPORT_PROVIDER;

/**
 * Abstract TransportMessaging
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractTransport implements Transport {

   protected String providerCode;
   protected String providerVersion;
   protected final RuntimeContext<Transport> context;

   /**
    * @param context
    * @throws TransportException
    */
   public AbstractTransport(@NotNull final RuntimeContext<Transport> context) throws TransportException {
	this.context = context;
	this.providerCode = context.getString(TRANSPORT_PROVIDER);	
	if (StringHelper.isEmpty(this.providerCode)) {
	   throw new EmptyContextParameterException(TRANSPORT_PROVIDER, context);
	}
	this.providerVersion = TransportProviderEnum.valueOf(providerCode) != null ? TransportProviderEnum.valueOf(providerCode).getVersion() : null;
   }

   public String getProviderCode() {
	return providerCode;
   }

   public String getProviderVersion() {
	return providerVersion;
   }

}
