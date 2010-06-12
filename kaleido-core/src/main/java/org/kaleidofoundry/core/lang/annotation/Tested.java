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
 * Use for an interface, a class, a method : if annotated, it mean that it have a unit test case <br/>
 * <ul>
 * <li>for a interface annotated: mean that all public method have tested or not(with an abstract test case)</li>
 * <li>for a implementation class: mean that all public method have tested or not</li>
 * <li>for a method class, mean method have tested or not</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface Tested {

   /**
    * @return test case have been written or not
    */
   boolean value() default true;

   /**
    * @return class name qualifier of the unit test class
    */
   String[] unitClassNames() default "";

   /**
    * @return method name in the unit test class
    */
   String[] unitMethodNames() default "";

   /**
    * @return free description
    */
   String decription() default "";
}
