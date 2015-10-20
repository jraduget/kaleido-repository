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
package org.kaleidofoundry.core.context.service_with_no_provider;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author jraduget
 */
public class MySingleService {

   @Context
   private RuntimeContext<?> defaultNamingService;

   @Context
   private RuntimeContext<MySingleService> defaultTypedNamingService;

   @Context("namingServices.jboss")
   private RuntimeContext<?> unTypedRuntimeContext;

   @Context("namingServices.jboss")
   private RuntimeContext<MySingleService> typedRuntimeContext;

   public RuntimeContext<?> getDefaultNamingService() {
	return defaultNamingService;
   }

   public RuntimeContext<MySingleService> getDefaultTypedNamingService() {
	return defaultTypedNamingService;
   }

   public RuntimeContext<?> getUnTypedRuntimeContext() {
	return unTypedRuntimeContext;
   }

   public RuntimeContext<MySingleService> getTypedRuntimeContext() {
	return typedRuntimeContext;
   }

}
