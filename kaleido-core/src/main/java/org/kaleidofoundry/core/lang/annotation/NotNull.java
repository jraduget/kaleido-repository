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
package org.kaleidofoundry.core.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.lang.NotNullException;

/**
 * Semantic "is not null" for a method parameter or method result<br/>
 * <ul>
 * <li>You can annotated method parameter with {@link NotNull}, so if annotated parameter is null when method is call, a
 * {@link NotNullException} will be thrown (use aspect)
 * <li>You can annotated method with {@link NotNull}, so if method return null, a {@link NotNullException} will be thrown (use aspect)
 * </ul>
 * 
 * @author jraduget
 */
@Documented
@Target( { ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
   
   /** Custom message of the thrown {@link NotNullException} */
   String message() default "";
}
