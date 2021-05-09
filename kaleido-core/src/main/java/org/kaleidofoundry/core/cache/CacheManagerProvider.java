/*
 *  Copyright 2008-2021 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.cache;

import static org.kaleidofoundry.core.cache.AbstractCacheManager.LOGGER;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.FileStoreUri;
import static org.kaleidofoundry.core.cache.CacheManagerContextBuilder.ProviderCode;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CacheMessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Registry & provider of {@link CacheManager} instances.
 * Please see {@link CacheManagerFactory} javadoc for more details.
 * 
 * @author jraduget
 * @see CacheManagerFactory
 */
@ThreadSafe
public class CacheManagerProvider extends AbstractProviderService<CacheManager> {

   // default cache provider code to use
   static String DEFAULT_CACHE_PROVIDER = CacheProvidersEnum.local.name();
   // set of reserved cache name
   static Set<String> RESERVED_CACHE_NAME = Collections.synchronizedSet(new HashSet<String>());;

   // main configuration registry instance (shared between providers instances)
   private static final Registry<String, CacheManager> REGISTRY = new Registry<String, CacheManager>();

   // does default init have occurred
   private static boolean INIT_LOADED = false;

   /**
    * load the default cache provider to use
    * 
    * @param cacheProviderCode
    * @see CacheProvidersEnum
    */
   public static synchronized void init(final String cacheProviderCode) {

	if (!INIT_LOADED) {

	   Plugin<CacheManager> defaultCachePlugin = null;
	   boolean userCacheImplNotFound = false;

	   if (cacheProviderCode != null && !"".equals(cacheProviderCode.trim())) {
		defaultCachePlugin = findCacheManagerByPluginCode(CacheConstants.CacheManagerPluginName + "." + cacheProviderCode);
		if (defaultCachePlugin == null) {
		   userCacheImplNotFound = true;
		}
	   }
	   DEFAULT_CACHE_PROVIDER = defaultCachePlugin != null ? defaultCachePlugin.getName() : CacheConstants.CacheManagerPluginName + "."
		   + CacheProvidersEnum.local.name();

	   if (userCacheImplNotFound && StringHelper.isEmpty(cacheProviderCode)) {
		LOGGER.warn(CacheMessageBundle.getMessage("cacheprovider.notfound", cacheProviderCode));
	   }

	   LOGGER.debug(CacheMessageBundle.getMessage("cacheprovider.customize"));
	   LOGGER.info(CacheMessageBundle.getMessage("cacheprovider.default", DEFAULT_CACHE_PROVIDER));
	   INIT_LOADED = true;
	}
   }

   /**
    * @return code of the default cache provider (that have been set)
    */
   public static String getDefaultCacheProvider() {
	return DEFAULT_CACHE_PROVIDER;
   }

   /**
    * @param genericClass
    */
   public CacheManagerProvider(final Class<CacheManager> genericClass) {
	super(genericClass);
   }

   /**
    * @return default cache manager provider (java system env. will be used, see class javadoc header)
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides() throws ProviderException {
	return provides(DEFAULT_CACHE_PROVIDER);
   }

   /**
    * @param providerCodeCode
    * @return cache manager using specific providerCodeCode
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCodeCode) throws ProviderException {
	return provides(providerCodeCode, null, new RuntimeContext<CacheManager>(providerCodeCode, CacheManager.class));
   }

   /**
    * @param context
    * @return cache manager if context specify a specific provider, it will use it, otherwise default cache manager
    *         provider (java system env. will be used, see class java-doc header)
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   @Override
   public CacheManager _provides(final RuntimeContext<CacheManager> context) throws ProviderException {
	final String providerCode = context.getString(ProviderCode, DEFAULT_CACHE_PROVIDER);
	final String providerConfiguration = context.getString(FileStoreUri);
	return provides(providerCode, providerConfiguration, context);
   }

   /**
    * @param providerCode
    * @param configuration
    * @return cache manager instance
    * @throws CacheConfigurationException cache configuration resource exception
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCode, final String configuration) throws ProviderException {
	return provides(providerCode, configuration, new RuntimeContext<CacheManager>(providerCode, CacheManager.class));
   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return cache manager instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   @NotNull
   public CacheManager provides(@NotNull final String providerCode, final String configuration, @NotNull final RuntimeContext<CacheManager> context)
	   throws ProviderException {

	RuntimeContext<CacheManager> newNamedContext = context;

	if (StringHelper.isEmpty(context.getName())) {
	   newNamedContext = new RuntimeContext<CacheManager>(providerCode, CacheManager.class);
	   RuntimeContext.copyFrom(context, newNamedContext);
	}

	CacheManager cacheManager = getRegistry().get(newNamedContext.getName());

	if (cacheManager == null) {
	   cacheManager = create(providerCode, configuration, newNamedContext);
	   getRegistry().put(newNamedContext.getName(), cacheManager);
	}

	return cacheManager;
   }

   /**
    * @return cache manager registry. each instance provided will be registered here
    */
   @Override
   public Registry<String, CacheManager> getRegistry() {
	return REGISTRY;
   }

   /**
    * @return set of some reserved cache name<br/>
    *         You can add your own as you need
    */
   public static Set<String> getReservedCacheName() {
	return RESERVED_CACHE_NAME;
   }

   /**
    * @param providerCode
    * @param configuration
    * @param context
    * @return new cache manager instance
    * @throws ProviderException encapsulate class implementation constructor call error (like {@link NoSuchMethodException},
    *            {@link InstantiationException}, {@link IllegalAccessException}, {@link InvocationTargetException})
    */
   protected static synchronized CacheManager create(@NotNull final String providerCode, final String configuration,
	   @NotNull final RuntimeContext<CacheManager> context) throws ProviderException {

	// only for optimization reasons
	if (providerCode.equalsIgnoreCase(CacheProvidersEnum.local.name())) { return new LocalCacheManagerImpl(configuration, context); }

	// dynamic lookup into register plugin
	final Set<Plugin<CacheManager>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(CacheManager.class);

	// scan each @Declare file store implementation, to get one which handle the given implementation code
	for (final Plugin<CacheManager> pi : pluginImpls) {
	   final Class<? extends CacheManager> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (declarePlugin.value().endsWith(providerCode)) {
		   final Constructor<? extends CacheManager> constructor = impl.getConstructor(String.class, RuntimeContext.class);
		   return constructor.newInstance(configuration, context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(),
			"String configuration, RuntimeContext<CacheManager> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(),
			"String configuration, RuntimeContext<CacheManager> context");
	   } catch (final InvocationTargetException e) {
		if (e.getCause() instanceof CacheConfigurationException) {
		   throw (CacheConfigurationException) e.getCause();
		} else {
		   throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(),
			   "String configuration, RuntimeContext<CacheManager> context", e.getCause().getClass().getName(), e.getMessage());
		}
	   }
	}
	throw new ProviderException(new CacheException("cacheprovider.illegal", providerCode));

   }

   /*
    * find cache manager by plugin code
    */
   @SuppressWarnings("unchecked")
   private static Plugin<CacheManager> findCacheManagerByPluginCode(@NotNull final String code) {
	final Plugin<?> plugin = PluginFactory.getImplementationRegistry().get(code);
	try {
	   return (Plugin<CacheManager>) plugin;
	} catch (final ClassCastException cce) {
	   throw new CacheException("cacheprovider.classcasterror", code, plugin.getClass().getTypeParameters()[0].getClass().getName(),
		   CacheManager.class.getName());
	}
   }

   /**
    * Free all cache managers instances
    */
   public void destroyAll() {
	if (INIT_LOADED) {
	   for (Entry<String, CacheManager> entry : REGISTRY.entrySet()) {
		try {
		   if (InternalBundleEnum.isInternalBundle(entry.getValue().getName())) {
			entry.getValue().destroyAll();
		   }
		} catch (Throwable th) {
		   LOGGER.info(CacheMessageBundle.getMessage("cachemanager.destroyall.error", entry.getKey()), th);
		}
	   }
	}
	INIT_LOADED = false;
	DEFAULT_CACHE_PROVIDER = CacheProvidersEnum.local.name();
   }

}
