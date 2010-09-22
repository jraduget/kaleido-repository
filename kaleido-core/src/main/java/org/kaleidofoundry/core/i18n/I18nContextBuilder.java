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

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;

/**
 * @author Jerome RADUGET
 */
public class I18nContextBuilder extends AbstractRuntimeContextBuilder<I18nMessages> {

   /**
    * @see I18nMessages#getResourceName()
    */
   public static final String BaseName = "baseName";

   /**
    * used to fix the {@link java.util.Locale} language of the resource bundle
    */
   public static final String LocaleLanguage = "locale.lang";

   /**
    * used to fix the {@link java.util.Locale} country of the resource bundle
    */
   public static final String LocaleCountry = "locale.country";
   
   /**
    * cache manager context name to use
    */
   public static final String CacheManagerRef = "cacheManagerRef";

   /**
    * specific class name to get the {@link ClassLoader} to used
    */
   public static final String ClassLoaderClass = "classLoaderClass";

   
   public I18nContextBuilder() {
	super();
   }

   public I18nContextBuilder(Class<I18nMessages> pluginInterface, Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   public I18nContextBuilder(Class<I18nMessages> pluginInterface) {
	super(pluginInterface);
   }

   public I18nContextBuilder(Configuration... configurations) {
	super(configurations);
   }

   public I18nContextBuilder(String name, Class<I18nMessages> pluginInterface, Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   public I18nContextBuilder(String name, Configuration... configurations) {
	super(name, configurations);
   }

   public I18nContextBuilder(String name, String prefixProperty, Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   public I18nContextBuilder(String name, String prefix) {
	super(name, prefix);
   }

   public I18nContextBuilder(String name) {
	super(name);
   }

   /**
    * @param baseName
    * @return set baseName context parameter
    * @see #BaseName
    */
   public I18nContextBuilder withBaseName(final String baseName) {
	getContextParameters().put(BaseName, baseName);
	return this;
   }

   /**
    * @param localeLanguage
    * @return set {@link java.util.Locale} language context parameter
    * @see #LocaleLanguage
    */
   public I18nContextBuilder withLocaleLanguage(final String localeLanguage) {
	getContextParameters().put(LocaleLanguage, localeLanguage);
	return this;
   }
   
   /**
    * @param localeCountry
    * @return set {@link java.util.Locale} country context parameter
    * @see #LocaleCountry
    */
   public I18nContextBuilder withLocaleCountry(final String localeCountry) {
	getContextParameters().put(LocaleCountry, localeCountry);
	return this;
   }   

   /**
    * @param cacheManagerRef
    * @return set cacheManagerRef context parameter
    * @see #CacheManagerRef
    */
   public I18nContextBuilder withCacheManagerRef(final String cacheManagerRef) {
	getContextParameters().put(CacheManagerRef, cacheManagerRef);
	return this;
   }
}
