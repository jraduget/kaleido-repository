package org.kaleidofoundry.core.plugin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annote your interface or implementation class with {@link DeclarePlugin} if you want to registered it in plugin
 * repository. <br/>
 * You can annotate :<br/>
 * <ul>
 * <li>interface plugin
 * <li>class implementation plugin
 * </ul>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeclarePlugin {

   /**
    * @return unique name
    */
   String value();

   /**
    * @return use case description
    */
   String description() default "";

   /**
    * @return current version
    */
   String version() default "";

   /**
    * @return does implementation have to be a singleton
    */
   boolean singleton() default false;

   /**
    * @return active or not plugin use
    */
   boolean enable() default true;

}
