package org.kaleidofoundry.core.ioc;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.slf4j.Logger;

/**
 * Interceptor used for injecting {@link RuntimeContext} information
 * 
 * @author Jerome RADUGET
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