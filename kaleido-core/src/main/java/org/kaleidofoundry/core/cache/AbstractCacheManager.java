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
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreRef;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

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

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreContextBuilder;
import org.kaleidofoundry.core.store.FileStoreFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceNotFoundException;
import org.kaleidofoundry.core.store.SingleFileStore;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractCacheManager implements CacheManager {

   /** CacheManager default logger */
   static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

   /** force the use of this cache configuration instead of the context one (if defined) */
   protected final String forcedConfiguration;

   /** internal runtime context */
   protected final RuntimeContext<CacheManager> context;

   /** external persistent singleStore */
   @Nullable
   protected final SingleFileStore singleFileStore;

   /** Local cache instances */
   @SuppressWarnings("rawtypes")
   protected final transient Registry<String, Cache> cachesByName;

   /**
    * constructor will use runtime context to get configuration uri
    * 
    * @param context
    */
   public AbstractCacheManager(@NotNull final RuntimeContext<CacheManager> context) {
	this(null, context);
   }

   /**
    * @param configuration override the context configuration file (if defined)
    * @param context
    * @throws CacheConfigurationException cache configuration resource exception
    */
   @SuppressWarnings("rawtypes")
   public AbstractCacheManager(final String configuration, @NotNull final RuntimeContext<CacheManager> context) {

	forcedConfiguration = StringHelper.isEmpty(configuration) ? null : configuration;

	// no need of configuration
	if (forcedConfiguration == null && StringHelper.isEmpty(context.getString(FileStoreUri))) {
	   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.loading.default", getMetaInformations(), getDefaultConfiguration()));
	   singleFileStore = null;
	}
	// configuration is given
	else {

	   final String fileStoreRef = context.getString(FileStoreRef);
	   final FileStore fileStore;
	   String fileStoreUri = StringHelper.isEmpty(forcedConfiguration) ? context.getString(FileStoreUri) : forcedConfiguration;

	   try {
		if (!StringHelper.isEmpty(fileStoreRef)) {
		   RuntimeContext<FileStore> fileStoreContext = new RuntimeContext<FileStore>(fileStoreRef, FileStore.class, context);
		   String baseUri = fileStoreContext.getString(FileStoreContextBuilder.BaseUri);
		   if (!fileStoreUri.contains(baseUri)) {
			fileStoreUri = baseUri + fileStoreUri;
		   }
		   fileStore = FileStoreFactory.provides(baseUri, fileStoreContext);
		} else {
		   fileStore = FileStoreFactory.provides(fileStoreUri);
		}

		LOGGER.info(CacheMessageBundle.getMessage("cachemanager.loading.custom", getMetaInformations(), fileStoreUri));

		singleFileStore = new SingleFileStore(fileStoreUri, fileStore);
	   } catch (final ProviderException pe) {
		if (pe.getCause() instanceof ResourceNotFoundException) { throw new CacheConfigurationNotFoundException("cache.configuration.notfound",
			getMetaInformations(), getCurrentConfiguration()); }
		if (pe.getCause() instanceof ResourceException) { throw new CacheConfigurationException("cache.configuration.error", pe.getCause(),
			getMetaInformations(), getCurrentConfiguration()); }
		throw pe;
	   }
	}

	this.context = context;
	cachesByName = new Registry<String, Cache>();
   }

   /**
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   AbstractCacheManager() {
	context = null;
	singleFileStore = null;
	cachesByName = null;
	forcedConfiguration = null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.Class)
    */
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull final Class<V> cl) {
	return getCache(cl, new RuntimeContext<Cache<K, V>>());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.Class, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(final Class<V> cl, final RuntimeContext<Cache<K, V>> context) {
	return getCache(cl.getName(), context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String)
    */
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(final String name) {
	return getCache(name, new RuntimeContext<Cache<K, V>>());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getName()
    */
   @Override
   public String getName() {
	return context.getName();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCurrentConfiguration()
    */
   @Override
   public String getCurrentConfiguration() {
	return !StringHelper.isEmpty(forcedConfiguration) ? forcedConfiguration : (StringHelper.isEmpty(context.getString(FileStoreUri)) ? context
		.getString(FileStoreUri) : getDefaultConfiguration());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroy(java.lang.Class)
    */
   @Override
   public void destroy(@NotNull final Class<?> cl) {
	destroy(cl.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroyAll()
    */
   @Override
   public void destroyAll() {
	try {
	   if (singleFileStore != null) {
		singleFileStore.unload();
	   }
	} catch (final ResourceException rse) {
	   LOGGER.error(InternalBundleHelper.CacheMessageBundle.getMessage("cachemanager.destroyall.store.error"), rse);
	} finally {
	   // unregister cacheManager from registry
	   CacheManagerFactory.getRegistry().remove(context.getName());
	   LOGGER.debug("Cache manager registry after destroyAll: {}", CacheManagerFactory.getRegistry());
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
   @Override
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
   @Override
   public void clearStatistics() {
	for (final String cacheName : getCacheNames()) {
	   clearStatistics(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#printStatistics(java.io.OutputStream)
    */
   @Override
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
   @Override
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
		writer.append(StringHelper.rightPad("-", tableWidths.get(column) - 1, '-')).append(" ");
	   }
	   writer.append("\n");

	   // columns name
	   for (final String column : tableWidths.keySet()) {
		writer.append(StringHelper.rightPad(StringHelper.truncate(column, tableWidths.get(column)), tableWidths.get(column), ' '));
	   }
	   writer.append("\n");

	   // second line of separator
	   for (final String column : tableWidths.keySet()) {
		writer.append(StringHelper.rightPad("-", tableWidths.get(column) - 1, '-')).append(" ");
	   }
	   writer.append("\n");

	   if (stats != null) {

		// for each cache
		for (final String cacheName : stats.keySet()) {

		   // first column is cache name
		   writer.append(StringHelper.rightPad(StringHelper.truncate(String.valueOf(cacheName), tableWidths.get("CacheName")),
			   tableWidths.get("CacheName"), ' '));

		   // create a column for each stats of the current cache
		   final Map<String, Object> statsValue = stats.get(cacheName);

		   // append each column value to the writer
		   for (final String statKey : statsValue.keySet()) {
			writer.append(StringHelper.rightPad(String.valueOf(statsValue.get(statKey)), tableWidths.get(statKey), ' '));

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
   @Override
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
    * <b>Don't forget to close the inputStream</b>
    * 
    * @param configurationUri
    * @return InputStream of the input configuration url, or null if none found
    * @throws CacheException
    */
   @SuppressWarnings("resource")
   protected InputStream getConfiguration(final String configurationUri) throws CacheException {
	if (singleFileStore != null) {
	   try {
		return singleFileStore.get().getInputStream();
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
	   } finally {
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

	final String contextClassLoader = context.getString(Classloader);
	ClassLoader currentClassLoader = null;

	if (!StringHelper.isEmpty(contextClassLoader)) {
	   try {
		currentClassLoader = Class.forName(context.getString(Classloader)).getClass().getClassLoader();
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
