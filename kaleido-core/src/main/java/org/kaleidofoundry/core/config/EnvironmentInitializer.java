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
package org.kaleidofoundry.core.config;

import static org.kaleidofoundry.core.config.ConfigurationConstants.STATIC_ENV_PARAMETERS;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CoreMessageBundle;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.kaleidofoundry.core.cache.CacheConstants;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheManagerProvider;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesProvider;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreConstants;
import org.kaleidofoundry.core.store.FileStoreProvider;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.system.OsEnvironment;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Environment Initializer :<br/>
 * The sequence to call is :
 * <ul>
 * <li>{@link #load()} : load environments properties (Operating system variables / java system variables ...)</li>
 * <li>{@link #start()} : start the init of some components like : {@link FileStore} / {@link Configuration} / {@link CacheManager}</li>
 * <li>{@link #stop()} : stop and free the components started</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class EnvironmentInitializer {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentInitializer.class);

   private final Logger logger;


   protected EnvironmentInitializer() {
	this(LOGGER);
   }

   public EnvironmentInitializer(final Logger logger) {
	this.logger = logger == null ? LOGGER : logger;
   }

   /**
    * Load environment settings before {@link #start()} <br/>
    * It can be overloaded
    */
   public void load() {

	// Plugin registry loading
	PluginFactory.getInterfaceRegistry();
	logger.info(StringHelper.replicate("*", 120));

	// First load system env variables
	try {
	   final OsEnvironment environment = new OsEnvironment();
	   for (final String key : environment.stringPropertyNames()) {
		final String value = environment.getProperty(key);
		STATIC_ENV_PARAMETERS.put(key, value != null ? value : "");
	   }
	} catch (IOException ioe) {
	   logger.error("Unable to load operating system environment", ioe);
	}

	// Then, load java system variables
	final Properties javaEnvVariables = System.getProperties();
	for (final String key : javaEnvVariables.stringPropertyNames()) {
	   STATIC_ENV_PARAMETERS.put(key, javaEnvVariables.getProperty(key));
	}

   }

   /**
    * @return map of parameters by name
    */
   public Map<String, String> getEnvironments() {
	return STATIC_ENV_PARAMETERS;
   }

   /**
    * Initialize I18n / FileStore / CacheManager / Configuration loading <br/>
    * it can be overloaded
    */
   public void start() {

	// Merge static environment variable values, to have the final value
	for (Entry<String, String> entry : STATIC_ENV_PARAMETERS.entrySet()) {
	   LOGGER.debug("Define environment variable {}={}", entry.getKey(), entry.getValue());
	   STATIC_ENV_PARAMETERS.put(entry.getKey(), StringHelper.resolveExpression(entry.getValue(), STATIC_ENV_PARAMETERS));
	}

	// I18n JPA enable or not
	I18nMessagesFactory.disableJpaControl();
	String enableI18nJpa = STATIC_ENV_PARAMETERS.get(I18nMessagesProvider.ENABLE_JPA_PROPERTY);
	enableI18nJpa = StringHelper.isEmpty(enableI18nJpa) ? Boolean.FALSE.toString() : enableI18nJpa;

	if (Boolean.valueOf(enableI18nJpa)) {
	   LOGGER.info(CoreMessageBundle.getMessage("loader.define.i18n.jpa.enabled"));
	   I18nMessagesFactory.enableJpaControl();
	} else {
	   LOGGER.info(CoreMessageBundle.getMessage("loader.define.i18n.jpa.disabled"));
	   I18nMessagesFactory.disableJpaControl();
	}

	// Parse and set default locale if needed
	final String defaultLocale = STATIC_ENV_PARAMETERS.get(LocaleFactory.JavaEnvProperties);
	if (!StringHelper.isEmpty(defaultLocale)) {
	   LOGGER.info(CoreMessageBundle.getMessage("loader.define.locale", defaultLocale));
	   final Locale setDefaultLocale = LocaleFactory.parseLocale(defaultLocale);
	   Locale.setDefault(setDefaultLocale);
	   LOGGER.info(StringHelper.replicate("*", 120));
	}

	// FileStore init (basedir ...)
	FileStoreProvider.init(STATIC_ENV_PARAMETERS.get(FileStoreConstants.DEFAULT_BASE_DIR_PROP));

	// Cache provider init (default cache provider ...)
	CacheManagerProvider.init(STATIC_ENV_PARAMETERS.get(CacheConstants.CACHE_PROVIDER_ENV));

	// Parse the default configurations and load it if needed
	final String kaleidoConfigurations = STATIC_ENV_PARAMETERS.get(ConfigurationConstants.JavaEnvProperties);
	if (!StringHelper.isEmpty(kaleidoConfigurations)) {
	   LOGGER.info(CoreMessageBundle.getMessage("loader.define.configurations",
		   StringHelper.replaceAll(kaleidoConfigurations, "\n", ",").replaceAll("\\s+", "")));
	   // load and register given configurations ids / url
	   try {
		ConfigurationFactory.init(StringHelper.replaceAll(kaleidoConfigurations, "\n", ConfigurationConstants.JavaEnvPropertiesSeparator));
	   } catch (final ResourceException rse) {
		throw new IllegalStateException(CoreMessageBundle.getMessage("loader.define.configurations.error", kaleidoConfigurations), rse);
	   }
	   LOGGER.info(StringHelper.replicate("*", 120));
	}
   }

   /**
    * destroy and free
    */
   public void stop() {
	// unload and unregister given configurations ids / url
	try {
	   ConfigurationFactory.unregisterAll();
	} catch (final ResourceException rse) {
	   throw new IllegalStateException(rse);
	} finally {
	   // unload all cache manager instances
	   CacheManagerFactory.destroyAll();
	}

   }
}
