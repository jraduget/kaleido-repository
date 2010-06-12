/*
 * $License$
 */
package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Review annotation, like a to do, but can be processing by an annotation processor to build listing / report
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE,
	ElementType.PACKAGE, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.SOURCE)
public @interface Review {

   /**
    * @return comment of the code review
    */
   String comment() default "";

   /** @return author of the review comment */
   String author() default "";

   /** @return assignee developper for the review */
   String assignee() default "";

}
