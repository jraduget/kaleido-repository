package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Semantic annotation used for class implementation that are not thread safe<br/>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotThreadSafe {

   /**
    * @return Optional comment
    */
   String comment() default "";
}
