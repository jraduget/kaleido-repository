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
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.Classloader;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ResourceStoreRef;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ResourceUri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceNotFoundException;
import org.kaleidofoundry.core.store.ResourceStore;
import org.kaleidofoundry.core.store.ResourceStoreFactory;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractCacheManager implements CacheManager {

   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCacheManager.class);

   /** force the use of this cache configuration */
   private final String forcedConfiguration;

   /** internal runtime context */
   protected final RuntimeContext<CacheManager> context;

   /** external persistent singleStore */
   @Nullable
   protected final SingleResourceStore singleResourceStore;

   /** Local cache instances */
   @SuppressWarnings("unchecked")
   protected final transient Registry<String, Cache> cachesByName;

   /**
    * @param context
    */
   public AbstractCacheManager(@NotNull final RuntimeContext<CacheManager> context) {
	this(null, context);
   }

   /**
    * @param configuration force the use of this cache configuration. if context specify a configuration, it will be ignored for this value.
    * @param context
    * @throws CacheConfigurationException cache configuration resource exception
    */
   @SuppressWarnings("unchecked")
   public AbstractCacheManager(final String configuration, @NotNull final RuntimeContext<CacheManager> context) {
	super();

	// no need of configuration
	if (StringHelper.isEmpty(configuration)) {
	   this.singleResourceStore = null;
	}
	// internal resource store instantiation
	else {
	   final String resourceStoreRef = context.getProperty(ResourceStoreRef);
	   final ResourceStore resourceStore;

	   try {
		if (!StringHelper.isEmpty(resourceStoreRef)) {
		   resourceStore = ResourceStoreFactory.provides(configuration, new RuntimeContext<ResourceStore>(resourceStoreRef, ResourceStore.class, context));
		} else {
		   resourceStore = ResourceStoreFactory.provides(configuration);
		}
		this.singleResourceStore = new SingleResourceStore(URI.create(configuration), resourceStore);
	   } catch (final ResourceNotFoundException rse) {
		throw new CacheConfigurationNotFoundException("cache.configuration.notfound", getMetaInformations(), getCurrentConfiguration());
	   } catch (final ResourceException rse) {
		throw new CacheConfigurationException("cache.configuration.error", rse, getMetaInformations(), getCurrentConfiguration());
	   }
	}

	this.context = context;
	this.forcedConfiguration = configuration;
	this.cachesByName = new Registry<String, Cache>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.Class)
    */
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final Class<V> cl) {
	return getCache(cl.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCurrentConfiguration()
    */
   @Override
   public String getCurrentConfiguration() {
	return !StringHelper.isEmpty(forcedConfiguration) ? forcedConfiguration : (StringHelper.isEmpty(context.getProperty(ResourceUri)) ? context
		.getProperty(ResourceUri) : getDefaultConfiguration());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroy(java.lang.Class)
    */
   public void destroy(@NotNull final Class<?> cl) {
	destroy(cl.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroyAll()
    */
   public void destroyAll() {

	try {
	   if (singleResourceStore != null) {
		singleResourceStore.unload();
	   }
	} catch (final ResourceException rse) {
	   LOGGER.error(InternalBundleHelper.CacheMessageBundle.getMessage("cache.destroyall.resourcestore.error"), rse);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheFactory#getCacheNames()
    */
   @Override
   public Set<String> getCacheNames() {
	return cachesByName.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#dumpStatistics()
    */
   @NotNull
   public Map<String, Map<String, Object>> dumpStatistics() {
	final Map<String, Map<String, Object>> dumpStatistics = new LinkedHashMap<String, Map<String, Object>>();
	for (final String cacheName : getCacheNames()) {
	   dumpStatistics.put(cacheName, dumpStatistics(cacheName));
	}
	return dumpStatistics;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#clearStatistics()
    */
   public void clearStatistics() {
	for (final String cacheName : getCacheNames()) {
	   clearStatistics(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#printStatistics(java.io.OutputStream)
    */
   public void printStatistics(@NotNull final OutputStream out) throws IOException {
	Writer writer = null;
	writer = new OutputStreamWriter(out);
	printStatistics(writer);
	writer.flush();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#printStatistics(java.io.Writer)
    */
   public void printStatistics(@NotNull final Writer writer) throws IOException {

	final Map<String, Map<String, Object>> stats = dumpStatistics();

	if (stats != null && !stats.isEmpty()) {

	   writer.append("\n");

	   // create table width informations
	   final Map<String, Integer> tableWidths = new LinkedHashMap<String, Integer>();
	   tableWidths.put("CacheName", 80);

	   // column compute width label and store it in tableWidths
	   final Map<String, Object> firstCacheInfo = stats.values().iterator().next();
	   for (final String column : firstCacheInfo.keySet()) {
		tableWidths.put(column, column.length() + 4);
	   }

	   // first line of separator
	   for (final String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad("-", tableWidths.get(column) - 1, "-")).append(" ");
	   }
	   writer.append("\n");

	   // columns name
	   for (final String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad(StringUtils.abbreviate(column, tableWidths.get(column)), tableWidths.get(column), " "));
	   }
	   writer.append("\n");

	   // second line of separator
	   for (final String column : tableWidths.keySet()) {
		writer.append(StringUtils.rightPad("-", tableWidths.get(column) - 1, "-")).append(" ");
	   }
	   writer.append("\n");

	   if (stats != null) {

		// for each cache
		for (final String cacheName : stats.keySet()) {

		   // first column is cache name
		   writer.append(StringUtils.rightPad(StringUtils.abbreviate(String.valueOf(cacheName), tableWidths.get("CacheName")),
			   tableWidths.get("CacheName"), " "));

		   // create a column for each stats of the current cache
		   final Map<String, Object> statsValue = stats.get(cacheName);

		   // append each column value to the writer
		   for (final String statKey : statsValue.keySet()) {
			writer.append(StringUtils.rightPad(String.valueOf(statsValue.get(statKey)), tableWidths.get(statKey), " "));

		   }
		   writer.append("\n");
		}
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#printStatistics()
    */
   @NotNull
   public String printStatistics() throws IOException {
	final StringWriter writer = new StringWriter();
	printStatistics(writer);
	return writer.toString();
   }

   /**
    * Search configuration file
    * <ul>
    * <li>first via URI if correct</li>
    * <li>second on file system</li>
    * <li>second on classpath (and jars resources)</li>
    * </ul>
    * 
    * @param configurationUri
    * @return InputStream of the input configuration url, or null if none found
    * @throws CacheException
    */
   protected InputStream getConfiguration(final String configurationUri) throws CacheException {
	if (singleResourceStore != null) {
	   try {
		return singleResourceStore.get().getInputStream();
	   } catch (final ResourceNotFoundException rse) {
		throw new CacheConfigurationNotFoundException("cache.configuration.notfound", getMetaInformations(), getCurrentConfiguration());
	   } catch (final ResourceException rse) {
		throw new CacheConfigurationException("cache.configuration.error", rse, getMetaInformations(), getCurrentConfiguration());
	   }
	} else {
	   File fconf = null;
	   InputStream fconfIn = null;

	   // 1. if file exist via URI first
	   try {
		final URI uri = new URI(configurationUri);
		fconf = new File(uri);
	   } catch (final URISyntaxException use) {
	   } catch (final IllegalArgumentException iae) {
	   }

	   // 2. if file exist on file system
	   if (fconf == null || !fconf.exists()) {
		final URL url = currentClassLoader().getResource(configurationUri);
		if (url != null) {
		   fconf = new File(url.getPath());
		}
	   }

	   // create inputStream if file was found
	   if (fconf != null && fconf.exists()) {
		try {
		   fconfIn = new FileInputStream(fconf);
		} catch (final FileNotFoundException fnfe) {
		}
	   }
	   // 3. search in classpath (jars) resources
	   else {
		fconfIn = currentClassLoader().getResourceAsStream(configurationUri);
	   }

	   return fconfIn;
	}
   }

   /**
    * @return current classLoader instance (thread instance, or application instance) (never null will be return)
    */
   protected ClassLoader currentClassLoader() {

	final String contextClassLoader = context.getProperty(Classloader);
	ClassLoader currentClassLoader = null;

	if (!StringHelper.isEmpty(contextClassLoader)) {
	   try {
		currentClassLoader = Class.forName(context.getProperty(Classloader)).getClass().getClassLoader();
	   } catch (final ClassNotFoundException cnfe) {
	   }
	}

	if (currentClassLoader == null) {
	   currentClassLoader = new ThreadLocal<Object>().getClass().getClassLoader();
	   if (currentClassLoader == null) {
		currentClassLoader = AbstractCacheManager.class.getClassLoader();
	   }
	}

	return currentClassLoader;
   }
}
