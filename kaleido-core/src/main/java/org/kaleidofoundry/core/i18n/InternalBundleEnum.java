package org.kaleidofoundry.core.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * Internal resource bundle names
 * 
 * @author Jerome RADUGET
 */
public enum InternalBundleEnum {

   Core(true, "i18n/core/messages"),
   Cache(true, "i18n/cache/messages"),
   MessageBundle(true, "i18n/messagebundle/messages"),
   Plugin(true, "i18n/plugin/messages"),
   ResourceStore(true, "i18n/resourcestore/messages"),
   Configuration(true, "i18n/configuration/messages"),
   Runtimecontext(true, "i18n/runtimecontext/messages"),
   Jndi(true, "i18n/jndi/messages");

   private static Set<String> CustomReservedBundle = Collections.synchronizedSet(new HashSet<String>());

   private final boolean standard;
   private final String resourcePath;

   private InternalBundleEnum(final boolean standard, final String resourcePath) {
	this.standard = standard;
	this.resourcePath = resourcePath;
   }

   /**
    * @return is it standard kaleido-foundry resource bundle
    */
   public boolean isStandard() {
	return standard;
   }

   /**
    * @return the bundle resource name
    */
   public String getResourceName() {
	return resourcePath;
   }

   /**
    * @param name
    */
   public static void addCustomReservedBundle(@NotNull final String name) {
	if (Arrays.asList(values()).contains(name)) { throw new IllegalStateException("ResourceBundle '" + name + "' is standard and reserved"); }
	if (CustomReservedBundle.contains(name)) { throw new IllegalStateException("ResourceBundle '" + name + "' is already registered"); }
	CustomReservedBundle.add(name);
   }

   /**
    * @param resourceName
    * @return does the given resource bundle name is internal to kaleido or not
    */
   public static boolean isInternalBundle(@NotNull final String resourceName) {

	for (InternalBundleEnum ibe : values()) {
	   if (resourceName.startsWith(ibe.getResourceName())) { return true; }
	}
	return false;
   }
}
