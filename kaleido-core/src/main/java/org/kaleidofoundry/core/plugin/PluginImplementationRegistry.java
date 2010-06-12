package org.kaleidofoundry.core.plugin;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registry of all enable plugin implementation <br/>
 * An enable plugin is a concrete class annotation by {@link DeclarePlugin} with enable attribute set to true
 * 
 * @author Jerome RADUGET
 */
@ThreadSafe
public class PluginImplementationRegistry extends Registry<Plugin<?>> {

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

	if (!cinterface.isAnnotationPresent(DeclarePlugin.class)) { throw new IllegalArgumentException(
		"argument \"cinterface\" is is not annotated by DeclarePlugin"); }

	Set<Plugin<T>> result = new LinkedHashSet<Plugin<T>>();

	for (Plugin<?> pluginImpl : values()) {

	   Set<Class<?>> interfaces = ReflectionHelper.getAllInterfaces(pluginImpl.getAnnotatedClass());

	   for (Class<?> i : interfaces) {
		if (i == cinterface && i.isAnnotationPresent(DeclarePlugin.class)) {
		   result.add((Plugin<T>) pluginImpl);
		}
	   }
	}

	return result;
   }
}
