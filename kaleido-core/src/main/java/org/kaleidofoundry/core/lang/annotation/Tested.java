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
    * @return user description
    */
   String decription() default "";

   /**
    * @return It represents a reference to the full test method signature (like javadoc syntax)<br/>
    * <br/>
    *         Examples :
    *         <ul>
    *         <li>@Tested(methods=new String["String#charAt(int)"])
    *         <li>String#startsWith(String, int)
    *         <li>com.compagny.module.YourClass#compute(...)
    *         <li>...
    *         </ul>
    */
   String[] methods() default "";

}
