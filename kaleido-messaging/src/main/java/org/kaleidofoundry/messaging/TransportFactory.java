/*  
 * Copyright 2008-2016 the original author or authors 
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

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.Registry;

/**
 * @author jraduget
 */
public abstract class TransportFactory {

   private static final TransportProvider PROVIDER = new TransportProvider(Transport.class);

   /**
    * @return transport registry
    */
   public static Registry<String, Transport> getRegistry() {
	return TransportProvider.REGISTRY;
   }

   public static Transport provides(String name) throws ProviderException {
	return PROVIDER.provides(name);
   }

   public static Transport provides(String name, RuntimeContext<?> context) throws ProviderException {
	return PROVIDER.provides(name, context);
   }

   public static Transport provides(String providerCode, String name) throws ProviderException {
	return PROVIDER.provides(providerCode, name);
   }

   public static Transport provides(RuntimeContext<Transport> context) throws ProviderException {
	return PROVIDER.provides(context);
   }

   public static void closeAll() throws TransportException {
	PROVIDER.closeAll();
   }

}
