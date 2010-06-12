package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.aop.NotImplementedAspect;

/**
 * Annotation use to show that code portion (methods), contains not yet implemented part<br/>
 * Method annotated by {@link NotYetImplemented} have to throws {@link NotYetImplementedException}<br/>
 * I can be do manually, or with an aop proxy which intercept it, and throws the exception<br/>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotYetImplemented {

   /**
    * @return user comment<br/>
    *         TODO : handle this comment in {@link NotImplementedAspect}<br/>
    *         to inject it in exception i18n message
    */
   String value() default "";
}
