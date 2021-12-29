/*
 *  Copyright 2008-2021 the original author or authors.
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

import javax.naming.NameNotFoundException;

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * @author jraduget
 */
public class NamingServiceNotFoundException extends NamingServiceException {

   private static final long serialVersionUID = 3986757398779544518L;

   /**
    * @param resourceName jndi resource name
    */
   public NamingServiceNotFoundException(final String resourceName, final RuntimeContext<NamingService> context) {
	super("naming.jndi.error.initialContext.lookup.notfound", resourceName, context.toString("\n"));
   }

   /**
    * @param resourceName jndi resource name
    * @param nnfe
    * @param context
    */
   public NamingServiceNotFoundException(final String resourceName, final NameNotFoundException nnfe, final RuntimeContext<NamingService> context) {
	super("naming.jndi.error.initialContext.lookup.notfound", nnfe, resourceName, context.toString("\n"));
   }

}
