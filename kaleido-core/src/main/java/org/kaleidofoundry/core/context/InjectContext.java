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
package org.kaleidofoundry.core.context;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.config.Configuration;

/**
 * Annotation used to provide a configuration context to a field, an argument, ... <br/>
 * It will use {@link Configuration} registry, to map the configurations data to the {@link RuntimeContext} class<br/>
 * This context is named, and will be registered when you use it.
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { CONSTRUCTOR, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface InjectContext {

   /**
    * @return context name identifier (must be unique for the annotated class)<br/>
    *         it will be used to register the given context
    */
   String value();

   /**
    * @return configuration identifiers, where to find the context name<br/>
    *         it is optional, and if empty : then context name will be search in all registered configuration<br/>
    */
   String[] configurations() default {};

   /**
    * @return when injected the {@link RuntimeContext}, default is lazy when getting {@link RuntimeContext} field
    */
   When when() default When.LazyGet;

   /**
    * @return static parameters, that could no be changed at runtime, but which will be provide to the underling instance
    */
   Parameter[] parameters() default {};
}
