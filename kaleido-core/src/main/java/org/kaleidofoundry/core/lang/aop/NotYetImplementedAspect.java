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
package org.kaleidofoundry.core.lang.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.lang.annotation.Tested;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 * @see NotYetImplemented
 */
@Aspect
@Tested
public class NotYetImplementedAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(NotYetImplementedAspect.class);

   /**
    * Pointcut for classes {@link NotYetImplemented} annotated. <b>Constructors</b> intercepter match in order to disable instantiation.
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("within(@org.kaleidofoundry.core.lang.annotation.NotYetImplemented *) && execution(*.new(..)) && if()")
   public static boolean classes(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotYetImplementedAspect - classes match");
	return true;
   }

   /**
    * Pointcut for constructor {@link NotYetImplemented} annotated
    * 
    * @param jp
    * @param esjp
    * @return true
    */
   @Pointcut("execution(@org.kaleidofoundry.core.lang.annotation.NotYetImplemented *.new(..)) && if()")
   public static boolean constructor(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotYetImplementedAspect - constructor match");
	return true;
   }

   /**
    * Pointcut for method {@link NotYetImplemented} annotated
    * 
    * @param jp
    * @param esjp
    * @return true
    */
   @Pointcut("execution(@org.kaleidofoundry.core.lang.annotation.NotYetImplemented * *(..)) && if()")
   public static boolean method(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut NotYetImplementedAspect - method match");
	return true;
   }

   @Before("classes(jp, esjp)")
   public void beforeClasses(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotYetImplementedAspect - classes - {}", jp);
	String annotValue = jp.getThis().getClass().getAnnotation(NotYetImplemented.class).value();
	if (StringHelper.isEmpty(annotValue)) {
	   throw new NotYetImplementedException();
	} else {
	   throw new NotYetImplementedException(annotValue);
	}
   }

   @Before("constructor(jp, esjp) && @annotation(annotation)")
   public void beforeConstructor(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final NotYetImplemented annotation) {
	LOGGER.debug("@Before NotYetImplementedAspect - constructor - {}", jp);
	if (StringHelper.isEmpty(annotation.value())) {
	   throw new NotYetImplementedException();
	} else {
	   throw new NotYetImplementedException(annotation.value());
	}
   }

   @Before("method(jp, esjp) && @annotation(annotation)")
   public void beforeMethod(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final NotYetImplemented annotation) {
	LOGGER.debug("@Before NotYetImplementedAspect - method - {}", jp);
	if (StringHelper.isEmpty(annotation.value())) {
	   throw new NotYetImplementedException();
	} else {
	   throw new NotYetImplementedException(annotation.value());
	}
   }

}
