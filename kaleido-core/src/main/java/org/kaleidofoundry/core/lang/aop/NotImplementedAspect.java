package org.kaleidofoundry.core.lang.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.kaleidofoundry.core.lang.NotImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.lang.annotation.Tested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 * @see NotImplemented
 * @see NotYetImplemented
 */
@Aspect
@Tested
public class NotImplementedAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(NotImplementedAspect.class);

   /**
    * Pointcut for classes @NotImplemented annotated. <b>Constructors</b> intercepter match in order to disable instantiation.
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
    * Pointcut for constructor @NotImplemented annotated
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
    * Pointcut for method @NotImplemented annotated
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

   @Before("classes(jp, esjp)")
   public void beforeClasses(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotImplementedAspect - classes - {}", jp);
	throw new NotImplementedException();
   }

   @Before("constructor(jp, esjp)")
   public void beforeConstructor(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotImplementedAspect - constructor - {}", jp);
	throw new NotImplementedException();
   }

   @Before("method(jp, esjp)")
   public void beforeMethod(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Before NotImplementedAspect - method - {}", jp);
	throw new NotImplementedException();
   }

}
