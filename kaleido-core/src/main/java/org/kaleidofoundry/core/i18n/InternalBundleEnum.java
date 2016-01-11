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
package org.kaleidofoundry.core.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * Internal resource bundle names
 * 
 * @author jraduget
 */
public enum InternalBundleEnum {

   CORE(true, "i18n/core/messages"),
   CACHE(true, "i18n/cache/messages"),
   MESSAGE_BUNDLE(true, "i18n/messagebundle/messages"),
   PLUGIN(true, "i18n/plugin/messages"),
   STORE(true, "i18n/store/messages"),
   CONFIGURATION(true, "i18n/configuration/messages"),
   CONTEXT(true, "i18n/context/messages"),
   NAMING(true, "i18n/naming/messages"),
   WEB(true, "i18n/web/messages"),
   UTIL(true, "i18n/util/messages"),
   MESSAGING(true, "i18n/messaging/messages"),
   MAIL(true, "i18n/mail/messages");

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

	for (final InternalBundleEnum ibe : values()) {
	   if (resourceName.startsWith(ibe.getResourceName())) { return true; }
	}
	return false;
   }
}
