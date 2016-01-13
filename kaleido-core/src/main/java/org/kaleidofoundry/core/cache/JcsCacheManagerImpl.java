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
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.CacheConstants.JcsCacheManagerPluginName;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.jcs.JCS;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.FileStoreTypeEnum;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Apache JCS provider
 * 
 * @see CacheManagerFactory
 * @author jraduget
 */
@Declare(value = JcsCacheManagerPluginName)
public class JcsCacheManagerImpl extends AbstractCacheManager {

   /** Default cache configuration */
   private static final String DefaultCacheConfiguration = "cache.ccf";

   /**
    * @param context
    */
   public JcsCacheManagerImpl(@NotNull RuntimeContext<CacheManager> context) {
	this(context.getString(FileStoreUri), context);
   }

   /**
    * @param configuration
    * @param context
    */
   public JcsCacheManagerImpl(String configuration, @NotNull RuntimeContext<CacheManager> context) {
	super(configuration, context);

	if (!StringHelper.isEmpty(configuration)) {

	   // apache JCS configuration can only be loaded from the classpath :(...
	   if (configuration.startsWith(FileStoreTypeEnum.classpath.name())) {
		JCS.setConfigFilename(configuration.substring((FileStoreTypeEnum.classpath.name() + ":").length()));
		return;
	   } else {
		// create a temporary file, and write the configuration content to it
		final InputStream inConf = getConfiguration(configuration);
		if (inConf != null) {
		   File tmpConfigFile;
		   try {
			tmpConfigFile = File.createTempFile("apache-jcs-config-", String.valueOf(System.currentTimeMillis()) + ".ccf");
			byte[] tmpConfigBytes = IOHelper.toByteArray(inConf);
			// copy the configuration content into this temporary file
			FileOutputStream tmpConfigOut = new FileOutputStream(tmpConfigFile, false);
			tmpConfigOut.write(tmpConfigBytes);
			tmpConfigOut.flush();
			tmpConfigOut.close();
			// add the configuration resource to the system classpath
			try {
			   Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			   method.setAccessible(true);
			   method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { tmpConfigFile.toURI().toURL() });
			} catch (Throwable th) {
			   throw new CacheException("cache.configuration.ioe.error", th);
			}
			// set the apache JCS file to the temporary file
			JCS.setConfigFilename(tmpConfigFile.getPath());
		   } catch (IOException e) {
			throw new CacheException("cache.configuration.ioe.error", e);
		   }

		} else {
		   throw new CacheException("cache.configuration.notfound", new String[] { JcsCacheManagerPluginName, configuration });
		}
	   }
	}
   }

   /**
    * 
    */
   JcsCacheManagerImpl() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDefaultConfiguration()
    */
   @Override
   public String getDefaultConfiguration() {
	return DefaultCacheConfiguration;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getMetaInformations()
    */
   @Override
   public String getMetaInformations() {
	return "ApacheJCS[1.3]";
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getCache(java.lang.String, org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public <K extends Serializable, V extends Serializable> Cache<K, V> getCache(@NotNull String name, @NotNull RuntimeContext<Cache<K, V>> context) {
	@SuppressWarnings("unchecked")
	Cache<K, V> cache = cachesByName.get(name);
	if (cache == null) {
	   cache = new JcsCacheImpl<K, V>(name, this, context);
	   // registered it to cache manager
	   cachesByName.put(name, cache);
	}
	return cache;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#destroy(java.lang.String)
    */
   @Override
   public void destroy(@NotNull String cacheName) {
	final Cache<?, ?> cache = cachesByName.get(cacheName);
	if (cache != null) {
	   // custom ehcache destroy
	   ((JcsCacheImpl<?, ?>) cache).destroy();
	   cachesByName.remove(cacheName);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#dumpStatistics(java.lang.String)
    */
   @Override
   public Map<String, Object> dumpStatistics(@NotNull String cacheName) {
	// TODO
	return new HashMap<String, Object>();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#clearStatistics(java.lang.String)
    */
   @Override
   public void clearStatistics(@NotNull String cacheName) {
	// TODO
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.cache.CacheManager#getDelegate()
    */
   @Override
   public Object getDelegate() {
	// there is not an accessible cache manager on top JCS ... null
	return null;
   }

   /**
    * @param name
    * @return cache provider
    */
   protected JCS createCache(final String name) {

	traceCacheCreation(name);

	try {
	   return JCS.getInstance(name);
	} catch (final org.apache.jcs.access.exception.CacheException jcse) {
	   throw new CacheConfigurationException("cache.configuration.error", jcse, JcsCacheManagerPluginName, getCurrentConfiguration());
	} catch (NullPointerException npe) {
	   // cache is not found in the configuration file
	   throw new CacheConfigurationNotFoundException("cache.configuration.error", npe, JcsCacheManagerPluginName, getCurrentConfiguration());
	}
   }

}
