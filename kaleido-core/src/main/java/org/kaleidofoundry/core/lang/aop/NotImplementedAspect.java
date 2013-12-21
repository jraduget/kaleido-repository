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
package org.kaleidofoundry.core.lang.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.kaleidofoundry.core.lang.NotImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 * @see NotImplemented
 */
@Aspect
public class NotImplementedAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(NotImplementedAspect.class);

   /**
    * Pointcut for classes {@link NotImplemented} annotated. <b>Constructors</b> intercepter match in order to disable instantiation.
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("within(@org.kaleidofoundry.core.lang.annotation.NotImplemented *) && execution(*.new(..)) && if()")
   public static boolean classes(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotImplementedAspect - classes match");
	return true;
   }

   /**
    * Pointcut for constructor {@link NotImplemented} annotated
    * 
    * @param jp
    * @param esjp
    * @return true
    */
   @Pointcut("execution(@org.kaleidofoundry.core.lang.annotation.NotImplemented *.new(..)) && if()")
   public static boolean constructor(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotImplementedAspect - constructor match");
	return true;
   }

   /**
    * Pointcut for method {@link NotImplemented} annotated
    * 
    * @param jp
    * @param esjp
    * @return true
    */
   @Pointcut("execution(@org.kaleidofoundry.core.lang.annotation.NotImplemented * *(..)) && if()")
   public static boolean method(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotImplementedAspect - method match");
	return true;
   }

   @Before("classes(jp, esjp) ")
   public void beforeClasses(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotImplementedAspect - classes - {}", jp);
	String annotValue = jp.getThis().getClass().getAnnotation(NotImplemented.class).value();
	if (StringHelper.isEmpty(annotValue)) {
	   throw new NotImplementedException();
	} else {
	   throw new NotImplementedException(annotValue);
	}
   }

   @Before("constructor(jp, esjp) && @annotation(annotation)")
   public void beforeConstructor(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final NotImplemented annotation) {
	LOGGER.debug("@Before NotImplementedAspect - constructor - {}", jp);
	if (StringHelper.isEmpty(annotation.value())) {
	   throw new NotImplementedException();
	} else {
	   throw new NotImplementedException(annotation.value());
	}
   }

   @Before("method(jp, esjp) && @annotation(annotation) ")
   public void beforeMethod(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final NotImplemented annotation) {
	LOGGER.debug("@Before NotImplementedAspect - method - {}", jp);
	if (StringHelper.isEmpty(annotation.value())) {
	   throw new NotImplementedException();
	} else {
	   throw new NotImplementedException(annotation.value());
	}
   }

}
