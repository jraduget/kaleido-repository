package org.kaleidofoundry.core.plugin;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Plugin static helper methods
 * 
 * @author Jerome RADUGET
 */
public abstract class PluginHelper {

   /**
    * @param classToIntrospect
    * @return if class is annotated by {@link DeclarePlugin}, return its value attribute, otherwise throws an exception
    * @throws IllegalStateException if class argument is not annotated by {@link DeclarePlugin}
    */
   public static String getPluginName(@NotNull final Class<?> classToIntrospect) {
	if (classToIntrospect.isAnnotationPresent(DeclarePlugin.class)) {
	   DeclarePlugin plugin = classToIntrospect.getAnnotation(DeclarePlugin.class);
	   return plugin.value();
	} else {
	   throw new IllegalArgumentException("classToIntrospect must be annoted by ");
	}
   }
}
