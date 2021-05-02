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
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.i18n.I18nConstants.I18nDefaultMessageBundlePluginName;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default message bundle implementation
 * 
 * @author jraduget
 */
@Declare(I18nDefaultMessageBundlePluginName)
public class DefaultMessageBundle extends ResourceBundle implements I18nMessages {

   static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageBundle.class);

   // used for user resource bundle data
   final Cache<String, String> resourceBundleCache;
   // used internally for kaleidofoundry internal resource bundle data
   final Properties resourceBundleNoCache;
   // bundle resource name
   final String resourceName;
   // bundle parent (can be null)
   private ResourceBundle parent;

   // internal runtime context
   private final RuntimeContext<I18nMessages> context;

   /**
    * @param resourceName
    * @param properties
    */
   public DefaultMessageBundle(final String resourceName, final Properties properties) {
	this(resourceName, properties, new RuntimeContext<I18nMessages>(I18nMessages.class));
   }

   /**
    * @param resourceName
    * @param properties
    * @param runtimeContext
    */
   public DefaultMessageBundle(final String resourceName, final Properties properties, final RuntimeContext<I18nMessages> runtimeContext) {

	context = runtimeContext;

	// internal kaleidofoundry resource bundle, does not use internal cache
	if (InternalBundleEnum.isInternalBundle(resourceName)) {
	   LOGGER.debug("Create message bundle with no cache provider for '{}'", resourceName);
	   resourceBundleNoCache = properties;
	   resourceBundleCache = null;
	}
	// user resource bundle, does use internal cache
	else {
	   LOGGER.debug("Create message bundle with cache provider for '{}'", resourceName);
	   final CacheManager cacheManager;
	   final String cacheManagerContextRef = context.getString(I18nContextBuilder.CacheManagerRef);

	   if (!StringHelper.isEmpty(cacheManagerContextRef)) {
		cacheManager = CacheManagerFactory.provides(new RuntimeContext<CacheManager>(cacheManagerContextRef, CacheManager.class, context));
	   } else {
		cacheManager = CacheManagerFactory.provides();
	   }
	   resourceBundleCache = cacheManager.getCache(resourceName.startsWith("i18n/") ? "kaleidofoundry/" + resourceName : "kaleidofoundry/i18n/"
		   + resourceName);
	   resourceBundleNoCache = null;
	   // copy common properties to internal Cache<String,String> storage
	   for (final String propName : properties.stringPropertyNames()) {
		resourceBundleCache.put(propName, properties.getProperty(propName));
	   }
	}

	this.resourceName = resourceName;
   }

   /**
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   DefaultMessageBundle() {
	resourceName = null;
	context = null;
	resourceBundleCache = null;
	resourceBundleNoCache = null;
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
    */
   @Override
   protected Object handleGetObject(final String key) {
	return resourceBundleCache != null ? resourceBundleCache.get(key) : resourceBundleNoCache.getProperty(key);
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#getKeys()
    */
   @Override
   public Enumeration<String> getKeys() {
	final Set<String> handleKeys = resourceBundleCache != null ? resourceBundleCache.keys() : resourceBundleNoCache.stringPropertyNames();
	return Collections.enumeration(handleKeys);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.I18nMessages#getMessage(java.lang.String)
    */
   @Override
   public String getMessage(final String key) throws MissingResourceException {
	return getMessage(key, (Object[]) null);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.I18nMessages#getMessage(java.lang.String, java.lang.Object[])
    */
   @Override
   public String getMessage(final String key, final Object... array) throws MissingResourceException {
	String msg = null;

	try {
	   msg = getString(key);
	} catch (MissingResourceException mre) {
	}

	if (msg == null) {
	   // it can't be an i18n message, where are inside the bundle currently instantiate
	   throw new MissingResourceException("Cannot find message key '" + key + "' in resource '" + resourceName + "'", DefaultMessageBundle.class.getName(), key);
	}

	// with locale specifics
	return (new MessageFormat(msg, getLocale())).format(array, new StringBuffer(), null).toString();
	// return MessageFormat.format(msg, array);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.MessageBundle#getParent()
    */
   @Override
   public ResourceBundle getParent() {
	return parent;
   }

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#setParent(java.util.ResourceBundle)
    */
   @Override
   public void setParent(final ResourceBundle parent) {
	this.parent = parent;
	super.setParent(parent);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.MessageBundle#getResourceName()
    */
   @Override
   public String getResourceName() {
	return resourceName;
   }

}
