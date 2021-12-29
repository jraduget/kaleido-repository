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

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * @author jraduget
 */
public class PluginInspectorTest  {

   private PluginInspector pluginInspector;

   @Before
   public void setup() {
	pluginInspector = new PluginInspector();
   }

   @After
   public void cleanup() {
   }

   /**
    * assert that plugin inspector load correct plugin information, compare to annotated interface
    * 
    * @throws IOException
    * @throws ClassNotFoundException
    */
   @Test
   public void testLoadPluginMetaData() throws IOException, ClassNotFoundException {

	Set<Plugin<?>> plugins = pluginInspector.loadPluginMetaData();

	assertNotNull(plugins);
	assertTrue(plugins.size() > 0);

	for (Plugin<?> plugin : plugins) {
	   assertNotNull(plugin);
	   Declare declarePlugin = plugin.getAnnotatedClass().getAnnotation(Declare.class);
	   assertNotNull(declarePlugin);
	   assertEquals(declarePlugin.value(), plugin.getName());
	   if (!StringHelper.isEmpty(declarePlugin.version())) {
		assertEquals(declarePlugin.version(), plugin.getVersion());
	   }
	   assertEquals(declarePlugin.enable(), plugin.isEnable());
	   assertTrue(plugin.isStandard());
	   assertNotNull(plugin.getAnnotatedClass());
	}

   }

   /**
    * assert that plugin inspector load correct plugin implementation information, compare to annotated class
    * 
    * @throws IOException
    * @throws ClassNotFoundException
    */
   @Test
   public void testLoadPluginImplementationMetaData() throws IOException, ClassNotFoundException {

	Set<Plugin<?>> pluginImpls = pluginInspector.loadPluginImplementationMetaData();

	assertNotNull(pluginImpls);
	assertTrue(pluginImpls.size() > 0);

	for (Plugin<?> pluginImpl : pluginImpls) {
	   assertNotNull(pluginImpl);
	   Declare declarePlugin = pluginImpl.getAnnotatedClass().getAnnotation(Declare.class);
	   assertNotNull(declarePlugin);
	   assertEquals(declarePlugin.value(), pluginImpl.getName());
	   if (!StringHelper.isEmpty(declarePlugin.version())) {
		assertEquals(declarePlugin.version(), pluginImpl.getVersion());
	   }
	   assertEquals(declarePlugin.description(), pluginImpl.getDescription());
	   assertEquals(declarePlugin.enable(), pluginImpl.isEnable());
	   assertTrue(pluginImpl.isStandard());
	   assertNotNull(pluginImpl.getAnnotatedClass());
	}
   }
}
