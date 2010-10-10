/*
 *  Copyright 2008-2010 the original author or authors.
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

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * @author Jerome RADUGET
 */
public class NamingServiceFactory {

   private static final NamingServiceProvider NAMING_SERVICE_PROVIDER = new NamingServiceProvider(NamingService.class);

   /**
    * @param context
    * @return new naming service instance
    * @throws NamingServiceException
    * @throws ProviderException
    */
   public static NamingService provides(@NotNull final RuntimeContext<NamingService> context) throws NamingServiceException, ProviderException {
	return NAMING_SERVICE_PROVIDER.provides(context);
   }

}
