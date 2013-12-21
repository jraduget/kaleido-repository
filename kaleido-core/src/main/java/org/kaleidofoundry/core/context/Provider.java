/*
 *  Copyright 2008-2014 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.plugin.Declare;

/**
 * Annotation used on plugin interface ({@link Declare}) <br/>
 * It is used to specify the {@link ProviderService} class implementation that will create and inject the right instance, given a
 * {@link RuntimeContext} or {@link Context}
 * 
 * @author jraduget
 * @see ProviderService
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {

   /**
    * @return provider class which will provides plugin instances
    */
   Class<? extends ProviderService<?>> value();

   /**
    * @return how the instances have to be provided : as a singleton, as a prototype.... <br/>
    *         If the singleton is defined, an internal registry instances will be used to keep all the instances
    */
   Scope scope() default Scope.prototype;

}
