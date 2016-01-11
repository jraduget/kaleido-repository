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

import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registry of all "enable" plugin interface<br/>
 * An enable plugin is an interface annotation by {@link Declare} with enable attribute set to true
 * 
 * @author jraduget
 */
public class PluginRegistry extends Registry<String, Plugin<?>> {

   private static final long serialVersionUID = -3817010482113243944L;

   /**
    * Constructor with package access in order to avoid direct instantiation. <br/>
    * Please use {@link PluginFactory} to get current instance.
    */
   PluginRegistry() {
   }

   @Override
   public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Plugin interface registry:").append("\n");
	for (Plugin<?> plugin : values()) {
	   builder.append(plugin.getName()).append(" - ").append(plugin.getVersion()).append(" - ").append(plugin.getDescription());
	}
	return builder.toString();
   }

}