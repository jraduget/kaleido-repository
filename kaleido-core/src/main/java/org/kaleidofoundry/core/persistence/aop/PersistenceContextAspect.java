package org.kaleidofoundry.core.persistence.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
@Aspect
public class PersistenceContextAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceContextAspect.class);

   /**
    * Pointcut for constructor arguments annotated @NotNull
    * 
    * @param jp
    * @param esjp
    * @return true to enable advice
    */
   @Pointcut("set(* @javax.persistence.PersistenceContext javax.persistence.EntityManager) && if()")
   public static boolean entityManagerAnnotatedByPersistenceContext(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut PersistenceContextAspect - entityManagerAnnotatedByPersistenceContext match");
	// TODO - disable pointcut at runtime in a managed environment
	return true;
   }

}
