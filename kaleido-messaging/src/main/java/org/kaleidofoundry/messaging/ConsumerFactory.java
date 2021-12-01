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

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author jraduget
 */
public class ConsumerFactory {

   private static final ConsumerProvider PROVIDER = new ConsumerProvider(Consumer.class);

   public Consumer provides(String name) {
	return PROVIDER.provides(name);
   }

   public Consumer provides(String name, RuntimeContext<?> context) {
	return PROVIDER.provides(name, context);
   }

   public final Consumer provides(RuntimeContext<Consumer> context) throws ProviderException {
	return PROVIDER.provides(context);
   }

   public static void stopAll() throws TransportException {
	PROVIDER.stopAll();
   }

}
