package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Semantic nulleable for a method parameter, method result, ... only use for information
 * 
 * @author Jerome RADUGET
 * @see NotNull this oposite
 */
@Documented
@Target( { ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {

   /**
    * @return Optional comment
    */
   String comment() default "";
}
