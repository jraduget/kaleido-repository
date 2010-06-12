package org.kaleidofoundry.core.ioc;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextProvider;
import org.kaleidofoundry.core.context.annotation.Context;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.slf4j.Logger;

/**
 * Interceptor used for injecting {@link RuntimeContext} information with {@link Context} annotation <br/>
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

   private final RuntimeContextProvider<?> runtimeContextProvider;

   public ContextInjectionMethodInterceptor() {
	runtimeContextProvider = new RuntimeContextProvider<Object>();
   }

   @Override
   public Object invoke(final MethodInvocation invocation) throws Throwable {

	debugInvovation(invocation);

	// 1. Search RuntimeContext argument
	int cptArg = 0;
	RuntimeContext<?> runtimeContextArg = null;

	for (Object arg : invocation.getArguments()) {
	   if (arg != null && arg instanceof RuntimeContext<?>) {
		runtimeContextArg = (RuntimeContext<?>) arg;
		break;
	   } else {
		cptArg++;
	   }
	}

	// 2. if one argument instance of RuntimeContext have been fount
	if (runtimeContextArg != null) {
	   boolean contextInjected = false;
	   Context contextAnnot = null;

	   // 2.1 scan method arguments, to detect a @Context declaration
	   Annotation[] argsAnnot = invocation.getMethod().getParameterAnnotations()[cptArg];
	   for (Annotation a : argsAnnot) {
		if (a instanceof Context) {
		   contextAnnot = (Context) a;
		}
	   }
	   if (contextAnnot != null) {
		// replace argument value by new one
		invocation.getArguments()[cptArg] = runtimeContextProvider.provides(contextAnnot, runtimeContextArg);
		contextInjected = true;
	   }

	   // 2.2 scan method to detect @Context annotation is none argument @Context annotated
	   if (!contextInjected) {
		contextAnnot = invocation.getMethod().getAnnotation(Context.class);
		if (contextAnnot != null) {
		   invocation.getArguments()[cptArg] = runtimeContextProvider.provides(contextAnnot, runtimeContextArg);
		   contextInjected = true;
		}
	   }
	}

	return invocation.proceed();
   }

   protected void debugInvovation(final MethodInvocation invocation) {
	// 0. debug usefull part
	if (LOGGER.isDebugEnabled()) {

	   // method information
	   LOGGER.debug("method #{} invoked", invocation.getMethod().getName());

	   // method argument information
	   int cpt = 0;
	   for (Object arg : invocation.getArguments()) {
		LOGGER.debug("\tmethod arg[{}]={}.toString()={}", new Object[] { cpt++, arg.getClass().getName(), arg });
	   }

	   // method annotation
	   for (Annotation annot : invocation.getMethod().getDeclaredAnnotations()) {
		LOGGER.debug("\tmethod {} annotated by {}", invocation.getMethod().getName(), annot.annotationType().getName());
	   }

	   // instance information
	   LOGGER.debug("\tinstance class is \"{}\" toString()=\"{}\"", invocation.getThis().getClass().getName(), invocation.getThis());
	}
   }
}