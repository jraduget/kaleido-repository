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
package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.lang.NotImplementedException;

/**
 * Annotation use to show that code portion (methods), contains code that will not be implemented<br/>
 * Method annotated by {@link NotImplemented} have to throws {@link NotImplementedException}<br/>
 * I can be do manually, or with an aop proxy which intercept it, and throws the exception<br/>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotImplemented {

   /**
    * @return user comment <br/>
    *         it will be injected at runtime in {@link NotImplementedException} message
    */
   String value() default "";
}
