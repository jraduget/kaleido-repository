/*
 * $License$
 */
package org.kaleidofoundry.core.plugin;

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
public class PluginFactoryTest extends Assert {

   @Before
   public void setup() {
	// speed up test case - i18n jpa is not used for this test
	I18nMessagesFactory.disableJpaControl();
   }

   @After
   public void cleanup() {
	I18nMessagesFactory.enableJpaControl();
   }

   /**
    * assert that plugin interface is well registered, with correct annotation meta-data
    */
   @Test
   public void testInterfaceRegistry() {

	DeclarePlugin declarePlugin = PluginSample.class.getAnnotation(DeclarePlugin.class);
	assertNotNull(declarePlugin);

	final PluginRegistry pluginRegistry = PluginFactory.getInterfaceRegistry();

	assertNotNull("plugin registry is null", pluginRegistry);
	assertTrue("no plugin registered", pluginRegistry.size() > 0);
	assertTrue("plugin registry does no contain it", pluginRegistry.containsKey(declarePlugin.value()));

	final Plugin<?> newPlugin = pluginRegistry.get(declarePlugin.value());

	assertEquals(declarePlugin.value(), newPlugin.getName());
	assertEquals(declarePlugin.description(), newPlugin.getDescription());
	assertEquals(declarePlugin.version(), newPlugin.getVersion());
	assertEquals(declarePlugin.enable(), newPlugin.isEnable());
	assertTrue(newPlugin.isStandard());
	assertNotNull(newPlugin.getAnnotatedClass());
	assertEquals(newPlugin.getAnnotatedClass().getName(), PluginSample.class.getName());

   }

   /**
    * assert that plugin implementations are well registered, with correct annotation meta-data for each
    */
   @Test
   public void testImplementationRegistry() {

	DeclarePlugin declarePluginImpl = PluginSampleImpl.class.getAnnotation(DeclarePlugin.class);
	DeclarePlugin declarePluginImpl2 = PluginSampleImpl2.class.getAnnotation(DeclarePlugin.class);
	DeclarePlugin declarePluginImpl3 = PluginSampleImpl3Disable.class.getAnnotation(DeclarePlugin.class);

	assertNotNull(declarePluginImpl);
	assertNotNull(declarePluginImpl2);
	assertNotNull(declarePluginImpl3);

	final PluginImplementationRegistry pluginRegistryImpl = PluginFactory.getImplementationRegistry();

	assertNotNull(pluginRegistryImpl);
	assertTrue(pluginRegistryImpl.size() > 0);
	assertTrue(pluginRegistryImpl.containsKey(declarePluginImpl.value()));
	assertTrue(pluginRegistryImpl.containsKey(declarePluginImpl2.value()));
	assertFalse(pluginRegistryImpl.containsKey(declarePluginImpl3.value()));

	final Plugin<?> newPluginImpl = pluginRegistryImpl.get(declarePluginImpl.value());

	assertEquals(declarePluginImpl.value(), newPluginImpl.getName());
	assertEquals(declarePluginImpl.version(), newPluginImpl.getVersion());
	assertEquals(declarePluginImpl.description(), newPluginImpl.getDescription());
	assertEquals(declarePluginImpl.enable(), newPluginImpl.isEnable());
	assertTrue(newPluginImpl.isStandard());
	assertNotNull(newPluginImpl.getAnnotatedClass());
	assertEquals(newPluginImpl.getAnnotatedClass().getName(), PluginSampleImpl.class.getName());

	final Plugin<?> newPluginImpl2 = pluginRegistryImpl.get(declarePluginImpl2.value());

	assertEquals(declarePluginImpl2.value(), newPluginImpl2.getName());
	assertEquals(declarePluginImpl2.version(), newPluginImpl2.getVersion());
	assertEquals(declarePluginImpl2.description(), newPluginImpl2.getDescription());
	assertEquals(declarePluginImpl2.enable(), newPluginImpl2.isEnable());
	assertTrue(newPluginImpl2.isStandard());
	assertNotNull(newPluginImpl2.getAnnotatedClass());
	assertEquals(PluginSampleImpl2.class.getName(), newPluginImpl2.getAnnotatedClass().getName());

	Set<Plugin<PluginSample>> pluginImpls = pluginRegistryImpl.findByInterface(PluginSample.class);
	assertNotNull(pluginImpls);
	assertEquals(2, pluginImpls.size());
   }

}
