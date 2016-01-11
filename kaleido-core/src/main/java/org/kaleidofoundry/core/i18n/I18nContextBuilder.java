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

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * {@link I18nMessages} base {@link RuntimeContext} builder & properties.<br/>
 * <b>{@link I18nMessages} commons context properties</b> : <br/>
 * <p>
 * <table border="1">
 * <tr>
 * <td><b>Property name</b></td>
 * <td><b>Property description</b></td>
 * </tr>
 * <tr>
 * <td>baseName</td>
 * <td>resource name of the bundle (with no resource extension)</td>
 * </tr>
 * <tr>
 * <td>locale.lang</td>
 * <td>used to fix the {@link java.util.Locale} language of the resource bundle</td>
 * </tr>
 * <tr>
 * <td>locale.country</td>
 * <td>used to fix the {@link java.util.Locale} country of the resource bundle</td>
 * </tr>
 * <tr>
 * <td>cacheManagerRef</td>
 * <td>cache manager context name to use</td>
 * </tr>
 * <tr>
 * <td>classLoaderClass</td>
 * <td>specific class name to get the {@link ClassLoader} to used</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author jraduget
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

   /**
    * 
    */
   public I18nContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public I18nContextBuilder(final Class<I18nMessages> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public I18nContextBuilder(final Class<I18nMessages> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public I18nContextBuilder(final Class<I18nMessages> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public I18nContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public I18nContextBuilder(final String name, final Class<I18nMessages> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public I18nContextBuilder(final String name, final Class<I18nMessages> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public I18nContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public I18nContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public I18nContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public I18nContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public I18nContextBuilder(final String name) {
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
