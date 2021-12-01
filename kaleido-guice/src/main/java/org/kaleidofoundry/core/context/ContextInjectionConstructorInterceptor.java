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
package org.kaleidofoundry.core.context;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.slf4j.Logger;

/**
 * Interceptor used for injecting {@link RuntimeContext} information
 * 
 * @author jraduget
 */
class ContextInjectionConstructorInterceptor implements ConstructorInterceptor {

   private final static Logger LOGGER = AbstractModule.LOGGER;

   @Override
   public Object construct(final ConstructorInvocation invocation) throws Throwable {

	if (LOGGER.isDebugEnabled()) {

	   // method information
	   LOGGER.debug("method #{} invoked", invocation.getConstructor().getName());

	   // method argument information
	   int cpt = 0;
	   for (Object arg : invocation.getArguments()) {
		LOGGER.debug("\tmethod arg[{}]={}.toString()={}", new Object[] { cpt++, arg.getClass().getName(), arg });
	   }

	   // method annotation
	   for (Annotation annot : invocation.getConstructor().getDeclaredAnnotations()) {
		LOGGER.debug("\tmethod {} annotated by {}", invocation.getConstructor().getName(), annot.annotationType().getName());
	   }

	   // instance information
	   LOGGER.debug("\tinstance class is \"{}\" toString()=\"{}\"", invocation.getThis().getClass().getName(), invocation.getThis());

	}

	return invocation.proceed();
   }

}