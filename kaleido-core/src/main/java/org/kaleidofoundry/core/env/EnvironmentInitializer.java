/*
 * Copyright 2008-2012 the original author or authors
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
package org.kaleidofoundry.core.env;

import static org.kaleidofoundry.core.config.ConfigurationConstants.STATIC_ENV_PARAMETERS;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CoreMessageBundle;
import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.kaleidofoundry.core.cache.CacheConstants;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheManagerProvider;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.env.model.EnvironmentInfo;
import org.kaleidofoundry.core.env.model.EnvironmentStatus;
import org.kaleidofoundry.core.env.model.EnvironmentStatus.Status;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesProvider;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.store.FileStore;
import org.kaleidofoundry.core.store.FileStoreConstants;
import org.kaleidofoundry.core.store.FileStoreProvider;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.system.OsEnvironment;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.util.ThrowableHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Environment Initializer :<br/>
 * The sequence to call is :
 * <ul>
 * <li>{@link #init()} : load environments properties (Operating system variables / java system variables ...)</li>
 * <li>{@link #start()} : start the init of some components like : {@link FileStore} / {@link Configuration} / {@link CacheManager}</li>
 * <li>{@link #stop()} : stop and free the components started</li>
 * </ul>
 * 
 * @see EnvironmentStatus
 * @see EnvironmentInfo
 * 
 * @author Jerome RADUGET
 */
public class EnvironmentInitializer {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentInitializer.class);

   static EnvironmentInitializer instance;

   private final Logger logger;

   // static shared variables, because of the javax rest controller create multiple instances at each request
   private static Status status;
   private static Throwable error;

   /** injected entity manager */
   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   EntityManager em;

   public EnvironmentInitializer() {
	this(LOGGER);
   }

   public EnvironmentInitializer(final Logger logger) {

	if (instance != null) {
	   throw new IllegalStateException("environment have already been initialized");
	} else {
	   instance = this;
	}

	this.logger = logger == null ? LOGGER : logger;
	status = Status.STOPPED;
	try {
	   // em will be injected by by java ee container or if not by aspectj
	   if (em == null) {
		em = UnmanagedEntityManagerFactory.currentEntityManager(KaleidoPersistentContextUnitName);
	   }
	} catch (PersistenceException pe) {
	   em = null;
	}
   }

   /**
    * @return application status
    */
   public EnvironmentInfo getInfo() {
	StringBuilder pluginInfo = new StringBuilder();
	pluginInfo.append(PluginFactory.getInterfaceRegistry().toString());
	pluginInfo.append("\n");
	pluginInfo.append(PluginFactory.getImplementationRegistry().toString());

	StringBuilder osInfo = new StringBuilder();
	try {
	   final OsEnvironment environment = new OsEnvironment();
	   for (final String key : environment.stringPropertyNames()) {
		osInfo.append(key).append("=").append(environment.getProperty(key)).append("\n");
	   }
	} catch (IOException ioe) {
	   osInfo.append("Error retrieving this info: " + ioe.getMessage());
	}

	StringBuilder manifestInfo = new StringBuilder();
	InputStream inputStream = Thread.currentThread().getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
	if (inputStream != null) {
	   try {
		Manifest manifest = new Manifest(inputStream);
		manifestInfo.append(manifest.toString());
	   } catch (IOException ioe) {
	   }
	}

	return new EnvironmentInfo(manifestInfo.toString(), System.getProperties().toString(), osInfo.toString(), pluginInfo.toString());
   }

   /**
    * @return application environment
    */
   public EnvironmentStatus getStatus() {
	return new EnvironmentStatus(status, ThrowableHelper.getStackTrace(error));
   }

   /**
    * Load environment settings before {@link #start()} <br/>
    * It can be overloaded
    */
   public synchronized void init() {

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

	status = Status.INIT;

   }

   /**
    * @return map of parameters by name
    */
   public Map<String, String> getEnvironments() {
	return STATIC_ENV_PARAMETERS;
   }

   /**
    * start and load I18n / FileStore / CacheManager / Configuration <br/>
    * 
    * @throws IllegalStateException if you try to start it from an invalid state (started, error)
    */
   public synchronized EnvironmentInitializer start() throws IllegalStateException {

	if (status == Status.STOPPED) {
	   init();
	}

	if (status == Status.INIT) {
	   try {
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

		status = Status.STARTED;

		return this;

	   } catch (RuntimeException re) {
		status = Status.ERROR;
		error = re;
		throw re;
	   }
	} else {
	   throw new IllegalStateException("Environment could not be started. The current state is " + status.name());
	}

   }

   /**
    * stop and free resources
    * 
    * @throws IllegalStateException if you try to start it from an invalid state (started, error)
    */
   public synchronized EnvironmentInitializer stop() {

	if (status == Status.STARTED || status == Status.ERROR) {
	   try {
		// unload messaging resources if it is in the classpath
		try {
		   Class<?> transportClass = Class.forName("org.kaleidofoundry.messaging.TransportFactory");
		   Method closeAllMethod = ReflectionHelper.getMethodWithNoArgs(transportClass, "closeAll");
		   ReflectionHelper.invokeStaticMethodSilently(closeAllMethod);
		} catch (ClassNotFoundException cnfe) {
		}
		// unload and unregister given configurations ids / url
		ConfigurationFactory.unregisterAll();

		// unload all cache manager instances
		CacheManagerFactory.destroyAll();

		// if unmanaged entity manager used, clean all
		UnmanagedEntityManagerFactory.closeAll();

		status = Status.STOPPED;

		return this;

	   } catch (final ResourceException rse) {
		error = rse;
		status = Status.ERROR;
		throw new IllegalStateException(rse);
	   } catch (RuntimeException re) {
		error = re;
		status = Status.ERROR;
		throw re;
	   }
	} else {
	   throw new IllegalStateException("Environment could not be stop. The current state is " + status.name());
	}
   }

   Logger getLogger() {
      return logger;
   }
   
}
