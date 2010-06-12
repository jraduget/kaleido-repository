/*
 * $License$
 */
package org.kaleidofoundry.core.plugin;

import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * @author Jerome RADUGET
 */
public class PluginInspectorTest extends Assert {

   private PluginInspector pluginInspector;

   @Before
   public void setup() {
	pluginInspector = new PluginInspector();
	// speed up test case - i18n jpa is not used for this test
	I18nMessagesFactory.disableJpaControl();
   }

   @After
   public void cleanup() {
	I18nMessagesFactory.enableJpaControl();
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
	   DeclarePlugin declarePlugin = plugin.getAnnotatedClass().getAnnotation(DeclarePlugin.class);
	   assertNotNull(declarePlugin);
	   assertEquals(declarePlugin.value(), plugin.getName());
	   assertEquals(declarePlugin.description(), plugin.getDescription());
	   assertEquals(declarePlugin.version(), plugin.getVersion());
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
	   DeclarePlugin declarePlugin = pluginImpl.getAnnotatedClass().getAnnotation(DeclarePlugin.class);
	   assertNotNull(declarePlugin);
	   assertEquals(declarePlugin.value(), pluginImpl.getName());
	   assertEquals(declarePlugin.version(), pluginImpl.getVersion());
	   assertEquals(declarePlugin.description(), pluginImpl.getDescription());
	   assertEquals(declarePlugin.enable(), pluginImpl.isEnable());
	   assertTrue(pluginImpl.isStandard());
	   assertNotNull(pluginImpl.getAnnotatedClass());
	}
   }
}
