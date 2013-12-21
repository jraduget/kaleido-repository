/*
 *  Copyright 2008-2014 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.naming;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.util.Registry;

/**
 * @author jraduget
 */
@ThreadSafe
public class NamingServiceProvider extends AbstractProviderService<NamingService> {

   private static final Registry<String, NamingService> REGISTRY = new Registry<String, NamingService>();

   /**
    * @param genericClassInterface
    */
   public NamingServiceProvider(final Class<NamingService> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, NamingService> getRegistry() {
	return REGISTRY;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.ProviderService#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public NamingService _provides(final RuntimeContext<NamingService> context) throws ProviderException {
	return new JndiNamingService(context);
   }

}
