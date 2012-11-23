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
package org.kaleidofoundry.core.plugin;

import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.ReflectionHelper;

/**
 * Plugin static helper methods
 * 
 * @author Jerome RADUGET
 */
public abstract class PluginHelper {

   /**
    * @param classToIntrospect
    * @return if class is annotated by {@link Declare}, return its value attribute, otherwise throws an exception
    * @throws IllegalStateException if class argument is not annotated by {@link Declare}
    */
   @Nullable
   public static String getPluginName(@NotNull final Class<?> classToIntrospect) {
	if (classToIntrospect.isAnnotationPresent(Declare.class)) {
	   final Declare plugin = classToIntrospect.getAnnotation(Declare.class);
	   return plugin.value();
	} else {
	   return null;
	}
   }

   /**
    * @param classImplInstance plugin implementation class instance
    * @return the plugin interface meta-data of the class implementation argument, null if it does not exist or if class implementation
    *         argument is not annotated @{@link Declare}
    */
   @Nullable
   public static Plugin<?> getInterfacePlugin(@NotNull final Object classImplInstance) {
	return getInterfacePlugin(classImplInstance.getClass());
   }

   /**
    * @param classImpl plugin implementation class
    * @return the plugin interface meta-data of the class implementation argument, null if it does not exist or if class implementation
    *         argument is not annotated @{@link Declare}
    */
   @Nullable
   public static Plugin<?> getInterfacePlugin(@NotNull final Class<?> classImpl) {
	if (classImpl.isAnnotationPresent(Declare.class)) {
	   final Set<Class<?>> interfaces = ReflectionHelper.getAllInterfaces(classImpl);
	   for (final Class<?> c : interfaces) {
		if (c.isAnnotationPresent(Declare.class)) { return PluginFactory.getInterfaceRegistry().get(c.getAnnotation(Declare.class).value()); }
	   }
	}
	return null;
   }

}
