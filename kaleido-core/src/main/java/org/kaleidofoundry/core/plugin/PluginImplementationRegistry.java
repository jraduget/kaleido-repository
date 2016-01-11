/*  
 * Copyright 2008-2016 the original author or authors 
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registry of all enable plugin implementation <br/>
 * An enable plugin is a concrete class annotation by {@link Declare} with enable attribute set to true
 * 
 * @author jraduget
 */
@ThreadSafe
public class PluginImplementationRegistry extends Registry<String, Plugin<?>> {

   private static final long serialVersionUID = 8291953439110040529L;

   /**
    * Constructor with package access in order to avoid direct instantiation. <br/>
    * Please use {@link PluginFactory} to get current instance.
    */
   PluginImplementationRegistry() {
   }

   /**
    * extract from plugin implementation registry, the class which implements given interface argument
    * 
    * @param cinterface
    * @return list of plugin implementation implementing given interface argument
    * @param <T>
    */
   @SuppressWarnings("unchecked")
   public <T> Set<Plugin<T>> findByInterface(final Class<T> cinterface) {

	if (!cinterface.isInterface()) { throw new IllegalArgumentException("argument \"cinterface\" is not an interface."); }

	if (!cinterface.isAnnotationPresent(Declare.class)) { throw new IllegalArgumentException("argument \"cinterface\" is is not annotated by DeclarePlugin"); }

	Set<Plugin<T>> result = new LinkedHashSet<Plugin<T>>();

	for (Plugin<?> pluginImpl : values()) {

	   Set<Class<?>> interfaces = ReflectionHelper.getAllInterfaces(pluginImpl.getAnnotatedClass());

	   for (Class<?> i : interfaces) {
		if (i == cinterface && i.isAnnotationPresent(Declare.class)) {
		   result.add((Plugin<T>) pluginImpl);
		}
	   }
	}

	return result;
   }
   
   @Override
   public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Plugin provider registry:").append("\n");
	for (Plugin<?> plugin : values()) {
	   builder.append(plugin.getName()).append(" - ").append(plugin.getVersion()).append(" - ").append(plugin.getDescription());
	}
	return builder.toString();
   }
}
