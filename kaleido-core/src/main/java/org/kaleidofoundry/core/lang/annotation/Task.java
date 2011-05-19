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

/**
 * The task annotation is like a "to do"<br/>
 * The advantage : it can be processing by an annotation processor to build listing / report
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE, ElementType.PARAMETER,
	ElementType.FIELD, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.SOURCE)
public @interface Task {

   /** @return a bug tracker code */
   String code() default "";

   /** @return a description of the task */
   String comment() default "";

   /** @return labels of the task */
   TaskLabel[] labels() default TaskLabel.ImplementIt;

   /** @return the author of the task */
   String author() default "";

   /** @return the assignee for the task */
   String assignee() default "";

}
