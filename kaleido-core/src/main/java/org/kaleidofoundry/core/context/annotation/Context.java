package org.kaleidofoundry.core.context.annotation;

import java.lang.annotation.*;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Annotation used to provide a configuration context to a field, an argument, ... <br/>
 * It will use {@link Configuration} registry, to map the configurations data to the {@link RuntimeContext} class<br/>
 * This context is named, and will be registered when you use it.
 *  
 * @author Jerome RADUGET
 */
@Documented
@Target( { CONSTRUCTOR, METHOD, FIELD, PARAMETER, LOCAL_VARIABLE })
@Retention(RUNTIME)
public @interface Context {

   /**
    * @return context name identifier (must be unique for the annotated class)<br/>
    *         it will be used to register the given context
    */
   String name();

   /**
    * @return configuration identifier, where to find the context name<br/>
    *         it is optional, and if empty string : then context name will be search in all registered configuration<br/>
    */
   String configuration() default "";

}
