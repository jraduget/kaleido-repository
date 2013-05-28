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
package org.kaleidofoundry.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.Registry;

/**
 * I18n messages factory
 * 
 * @author Jerome RADUGET
 */
public class I18nMessagesFactory {

   private static I18nMessagesProvider I18N_MESSAGES_PROVIDER = new I18nMessagesProvider(I18nMessages.class);

   /**
    * it clear internal java resource bundle cache<br/>
    * <br/>
    * when you call multiple times {@link #provides(String)}, jdk use an internal cache to get the current bundle
    * once you call {@link #clearCache()}, then next call to {@link #provides(String)} will instantiate / load ... a new
    * {@link DefaultMessageBundle}
    * 
    * @see ResourceBundle#clearCache()
    */
   public static void clearCache() {
	I18nMessagesProvider.clearCache();
   }

   /**
    * @return internal bundle registry
    */
   public static Registry<String, I18nMessages> getRegistry() {
	return I18N_MESSAGES_PROVIDER.getRegistry();
   }

   /**
    * @param context
    * @return ResourceBundle using runtime context properties
    */
   public static I18nMessages provides(final RuntimeContext<I18nMessages> context) {
	return I18N_MESSAGES_PROVIDER.provides(context);
   }

   /**
    * @param baseName name of the resource
    * @return ResourceBundle with server default locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public static I18nMessages provides(@NotNull final String baseName) {
	return I18N_MESSAGES_PROVIDER.provides(baseName);
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @return ResourceBundle with specified locale, and with properties loaded from the same classLoader than ResourceBundle class
    */
   public static I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, locale);
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public static I18nMessages provides(@NotNull final String baseName, final I18nMessages parent) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, parent);
   }

   /**
    * @param baseName name of the resource
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages provides(@NotNull final String baseName, final ResourceBundle parent) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, parent);
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle with server default locale having a parent resource bundle
    */
   public static I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ResourceBundle parent) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, locale, parent);
   }

   /**
    * @param baseName
    * @param locale
    * @param parent parent ResourceBundle
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final I18nMessages parent) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, locale, parent);
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @param loader target class loader
    * @param parent resourceBundle parent
    * @return ResourceBundle build with given locale, and having a parent resource bundle
    */
   public static I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ClassLoader loader, final I18nMessages parent) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, locale, loader, parent);
   }

   /**
    * @param baseName name of the resource
    * @param locale specified locale
    * @param loader target class loader
    * @return ResourceBundle with specified locale, and with properties loaded from specified classLoader
    */
   public static I18nMessages provides(@NotNull final String baseName, @NotNull final Locale locale, final ClassLoader loader) {
	return I18N_MESSAGES_PROVIDER.provides(baseName, locale, loader);
   }
   
   /**
    * enable the jpa resource bundle control resolver
    */
   public static void enableJpaControl() {
	I18nMessagesProvider.enableJpaControl();
   }

   /**
    * disable the jpa resource bundle control resolver
    */
   public static void disableJpaControl() {
	I18nMessagesProvider.disableJpaControl();
   }   

}
