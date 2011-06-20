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
package org.kaleidofoundry.core.context;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.slf4j.Logger;

/**
 * Interceptor used for injecting {@link RuntimeContext} information using the {@link Context} method annotation <br/>
 * <br/>
 * Only the first {@link RuntimeContext} argument would be processing
 * <p>
 * Context injection handle by :
 * <ul>
 * <li>constructor (not yet handled)
 * <li>field
 * <li>method
 * <li>method argument
 * </ul>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Immutable
class ContextInjectionMethodInterceptor implements MethodInterceptor {

   private final static Logger LOGGER = AbstractModule.LOGGER;

   public ContextInjectionMethodInterceptor() {
   }

   @Override
   public Object invoke(final MethodInvocation invocation) throws Throwable {

	debugInvovation(invocation);

	// 1. Search RuntimeContext argument
	int cptArg = 0;
	RuntimeContext<?> runtimeContextArg = null;

	for (final Object arg : invocation.getArguments()) {
	   if (arg != null && arg instanceof RuntimeContext<?>) {
		runtimeContextArg = (RuntimeContext<?>) arg;
		break;
	   } else {
		cptArg++;
	   }
	}

	// 2. if one argument instance of RuntimeContext have been found
	if (runtimeContextArg != null) {
	   boolean contextInjected = false;
	   Context contextAnnot = null;

	   // 2.1 scan method arguments, to detect a @Context declaration
	   final Annotation[] argsAnnot = invocation.getMethod().getParameterAnnotations()[cptArg];
	   for (final Annotation a : argsAnnot) {
		if (a instanceof Context) {
		   contextAnnot = (Context) a;
		}
	   }
	   if (contextAnnot != null) {
		// replace context argument datas using annotation meta-datas
		// no way to have parameter name ?
		RuntimeContext.createFrom(contextAnnot, null, runtimeContextArg);
		contextInjected = true;
	   }

	   // 2.2 scan method to detect @Context annotation is none argument @Context annotated
	   if (!contextInjected) {
		contextAnnot = invocation.getMethod().getAnnotation(Context.class);
		if (contextAnnot != null) {
		   // no way to have parameter name ?
		   RuntimeContext.createFrom(contextAnnot, null, runtimeContextArg);
		   contextInjected = true;
		}
	   }
	}

	return invocation.proceed();
   }

   protected void debugInvovation(final MethodInvocation invocation) {
	// 0. debug useful part
	if (LOGGER.isDebugEnabled()) {

	   // method information
	   LOGGER.debug("method #{} invoked", invocation.getMethod().getName());

	   // method argument information
	   int cpt = 0;
	   for (final Object arg : invocation.getArguments()) {
		LOGGER.debug("\tmethod arg[{}]={}.toString()={}", new Object[] { cpt++, arg.getClass().getName(), arg });
	   }

	   // method annotation
	   for (final Annotation annot : invocation.getMethod().getDeclaredAnnotations()) {
		LOGGER.debug("\tmethod {} annotated by {}", invocation.getMethod().getName(), annot.annotationType().getName());
	   }

	   // instance information
	   LOGGER.debug("\tinstance class is \"{}\" toString()=\"{}\"", invocation.getThis().getClass().getName(), invocation.getThis());
	}
   }
}