/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.core.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Annotate your interface or implementation class with {@link Declare} if you want to registered it in plugin
 * repository. <br/>
 * You can annotate :<br/>
 * <ul>
 * <li>interface
 * <li>implementation class of a declared interface
 * </ul>
 * 
 * @author jraduget
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Declare {

   /**
    * @return unique name of the declared plugin
    * @see RuntimeContext#getPrefix() plugin name will be used as a configuration prefix for runtime context
    */
   String value();

   /**
    * @return use case description of the declared plugin
    */
   String description() default "";

   /**
    * @return current version of the declared plugin
    */
   String version() default "";

   /**
    * @return active or disable the plugin usage
    */
   boolean enable() default true;

}
