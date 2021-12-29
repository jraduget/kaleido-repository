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
package org.kaleidofoundry.core.lang.aop;

import static org.kaleidofoundry.core.util.AspectjHelper.debugJoinPoint;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Technical notes : </b> <br/>
 * <br/>
 * 1. Handle parameter annotation since aspectj 1.5.x :
 * <ul>
 * <li>execution(* *(@org.group.project.module.YourAnnotation *)) execution of any method, where the first parameter @YourAnnotation
 * annotated
 * <li>execution(* *(@org.group.project.module.YourAnnotation (*))) execution of any method, where one of the parameter @YourAnnotation
 * annotated
 * </ul>
 * 2. Handle matching parameter annotations (in multiple static locations) since aspectj 1.6.8 :
 * <ul>
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(@YourAnnotation (*),..))) // 1st param has annotation
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(*,@YourAnnotation (*),..))) // 2nd param has annotation
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(*,*, @YourAnnotation (*),..))) // 3rd param has annotation
 * </ul>
 * If you want to bind the annotated parameter you need the corresponding binding clause too : <br/>
 * <ul>
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(@YourAnnotation (*),..) && args(x,..))) // 1st param has annotation
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(*,@YourAnnotation (*),..) && args(*,x,..))) // 2nd param has annotation
 * <li>@Pointcut("execution(public * (@ClassAnnotation *).*(*,*, @YourAnnotation (*),..) && args(*,*,x,..))) // 3rd param has annotation
 * </ul>
 * <b>args(..,x,..) is not allowed...</b> <br/>
 * <br/>
 * 3. Handle matching parameter annotations in multiple dynamic locations since aspectj 1.6.9 :
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=233718
 * 
 * @author jraduget
 * @see NotNull
 */
@Aspect
public class NotNullAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(NotNullAspect.class);

   /**
    * Pointcut for constructor arguments annotated @NotNull
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("execution(*.new(..,@org.kaleidofoundry.core.lang.annotation.NotNull (*),..)) && if()")
   public static boolean constructorArgs(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotNullAspect - constructorArgs match");
	return true;
   }

   /**
    * Pointcut for method arguments annotated @NotNull
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("execution(* *(..,@org.kaleidofoundry.core.lang.annotation.NotNull (*),..)) && if()")
   public static boolean methodArgs(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotNullAspect - methodArgs match");
	return true;
   }

   /**
    * Pointcut for method annotated @NotNull result
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("call(@org.kaleidofoundry.core.lang.annotation.NotNull * *(..)) && if()")
   public static boolean methodResult(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotNullAspect - methodResult match");
	return true;
   }

   @Before("constructorArgs(jp, esjp)")
   public void beforeConstructorArgs(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotNullAspect - constructorArgs - {}", jp);
	handleArgument(jp, esjp);
   }

   @Before("methodArgs(jp, esjp)")
   public void beforeMethodArgs(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotNullAspect - methodArgs - {}", jp);
	handleArgument(jp, esjp);
   }

   @Around("methodResult(jp, esjp)")
   public Object afterMethodResult(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final ProceedingJoinPoint thisJoinPoint) throws Throwable {
	LOGGER.debug("@After NotNullAspect - afterMethodResult");
	Object result = thisJoinPoint.proceed();
	if (result == null) {
	   throw new NotNullException(jp.getStaticPart().getSignature().toString(), jp.getSignature().toString(), jp.getSourceLocation().toString());
	} else {
	   return result;
	}
   }

   /**
    * introspect constructor or method argument, in order to detect if parameter annotated {@link NotNull} is null or not. <br/>
    * add useful debug informations too
    * 
    * @param jp
    * @param esjp
    */
   void handleArgument(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {

	debugJoinPoint(LOGGER, jp);

	Annotation[][] parametersAnnotations = null;
	String[] parametersName = null;

	// 1. extract method / constructor parameter static informations
	if (jp.getSignature() instanceof ConstructorSignature) {
	   parametersName = ((ConstructorSignature) jp.getSignature()).getParameterNames();
	   parametersAnnotations = ((ConstructorSignature) jp.getSignature()).getConstructor().getParameterAnnotations();
	}
	if (jp.getSignature() instanceof MethodSignature) {
	   parametersName = ((MethodSignature) jp.getSignature()).getParameterNames();
	   parametersAnnotations = ((MethodSignature) jp.getSignature()).getMethod().getParameterAnnotations();
	}

	// 2. introspect input parameters
	if (parametersName != null && parametersAnnotations != null) {
	   for (int paramCpt = 0; paramCpt < parametersName.length; paramCpt++) {
		Annotation[] parameterAnnotations = parametersAnnotations[paramCpt];
		if (parameterAnnotations != null) {
		   for (Annotation parameterAnnotation : parameterAnnotations) {
			// parameter annotated with @NotNull + null value
			if (NotNull.class == parameterAnnotation.annotationType() && jp.getArgs()[paramCpt] == null) { 
				throw new NotNullException(
						jp.getStaticPart().getSignature().toString(), 
						parametersName[paramCpt], 
						jp.getSignature().toString(), 
						jp.getSourceLocation().toString()
				); 
			}
		   }
		}
	   }
	}
   }

}
