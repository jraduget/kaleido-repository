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
package org.kaleidofoundry.core.env;

import static org.kaleidofoundry.core.env.model.EnvironmentConstants.CACHE_PROVIDER_PROPERTY;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.CONFIGURATIONS_PROPERTY;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.CONFIGURATIONS_PROPERTY_SEPARATOR;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.DEFAULT_BASE_DIR_PROPERTY;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.KALEIDO_PERSISTENT_UNIT_NAME;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.LOCAL_PROPERTY;
import static org.kaleidofoundry.core.env.model.EnvironmentConstants.STATIC_ENV_PARAMETERS;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.CoreMessageBundle;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheManagerProvider;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationException;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurationProcessor;
import org.kaleidofoundry.core.env.model.EnvironmentConstants;
import org.kaleidofoundry.core.env.model.EnvironmentEntry;
import org.kaleidofoundry.core.env.model.EnvironmentInfo;
import org.kaleidofoundry.core.env.model.EnvironmentStatus;
import org.kaleidofoundry.core.env.model.EnvironmentStatus.Status;
import org.kaleidofoundry.core.env.model.EnvironmentVersions;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.store.FileStore;
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
 * @see EnvironmentConstants
 * @author jraduget
 */
@Stateless
// @Singleton
public class EnvironmentInitializer {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentInitializer.class);

   static EnvironmentInitializer instance;

   private final Class<?> applicationClass;
   private final Logger logger;

   // static shared variables, because of the javax rest controller create multiple instances at each request
   private static Status status;
   private static Throwable error;

   // configuration load by annotation
   private NamedConfigurationProcessor configurationInitializer;

   /** injected entity manager */
   @PersistenceContext(unitName = KALEIDO_PERSISTENT_UNIT_NAME)
   EntityManager em;

   public EnvironmentInitializer() {
	this(EnvironmentInitializer.class);
   }

   public EnvironmentInitializer(final Class<?> applicationClass) {

	if (instance != null) { throw new IllegalStateException("environment have already been initialized"); }

	this.applicationClass = applicationClass;
	this.logger = applicationClass == null ? LOGGER : LoggerFactory.getLogger(applicationClass);
	EnvironmentInitializer.status = Status.STOPPED;
   }

   /**
    * @return application status
    */
   public EnvironmentInfo getInfo() {

	String applicationVersion = applicationClass != null ? applicationClass.getPackage().getImplementationVersion() : null;
	String kaleidoVersion = this.getClass().getPackage().getImplementationVersion();
	String runnerVersion = Thread.currentThread().getClass().getPackage().getImplementationVersion();
	if (runnerVersion == null) {
	   runnerVersion = Thread.currentThread().getClass().getPackage().getSpecificationVersion();
	}

	EnvironmentVersions versions = new EnvironmentVersions(applicationVersion, runnerVersion, kaleidoVersion);

	List<EnvironmentEntry> manifestInfos = new ArrayList<EnvironmentEntry>();
	List<EnvironmentEntry> runnerManifestInfos = new ArrayList<EnvironmentEntry>();
	List<EnvironmentEntry> osInfos = new ArrayList<EnvironmentEntry>();
	List<EnvironmentEntry> systemInfos = new ArrayList<EnvironmentEntry>();
	List<Plugin<?>> plugins = new ArrayList<Plugin<?>>();
	List<Plugin<?>> pluginImpls = new ArrayList<Plugin<?>>();

	plugins.addAll(PluginFactory.getInterfaceRegistry().values());
	pluginImpls.addAll(PluginFactory.getImplementationRegistry().values());

	try {
	   final OsEnvironment environment = new OsEnvironment();
	   for (final String key : environment.stringPropertyNames()) {
		osInfos.add(new EnvironmentEntry(key, environment.getProperty(key)));
	   }
	} catch (Throwable th) {
	   LOGGER.error("Error retrieving the OS environment informations", th);
	}

	try {
	   for (String propName : System.getProperties().stringPropertyNames()) {
		systemInfos.add(new EnvironmentEntry(propName, System.getProperty(propName)));
	   }
	} catch (Throwable th) {
	   LOGGER.error("Error retrieving the java system properties informations", th);
	}

	try {
	   InputStream inputStream = applicationClass.getResourceAsStream("classpath:/META-INF/MANIFEST.MF");
	   if (inputStream != null) {
		Manifest manifest = new Manifest(inputStream);
		for (Entry<Object, Object> entry : manifest.getMainAttributes().entrySet()) {
		   manifestInfos.add(new EnvironmentEntry(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
		}
		for (Entry<String, Attributes> entry : manifest.getEntries().entrySet()) {
		   manifestInfos.add(new EnvironmentEntry(entry.getKey(), entry.getValue().toString()));
		}
	   }
	} catch (Throwable th) {
	   LOGGER.error("Error retrieving the manifest informations", th);
	}

	try {
	   InputStream inputStream = Thread.currentThread().getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
	   if (inputStream != null) {
		Manifest manifest = new Manifest(inputStream);
		for (Entry<Object, Object> entry : manifest.getMainAttributes().entrySet()) {
		   runnerManifestInfos.add(new EnvironmentEntry(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
		}
		for (Entry<String, Attributes> entry : manifest.getEntries().entrySet()) {
		   runnerManifestInfos.add(new EnvironmentEntry(entry.getKey(), entry.getValue().toString()));
		}
	   }
	} catch (Throwable th) {
	   LOGGER.error("Error retrieving the manifest informations of the runner", th);
	}

	return new EnvironmentInfo(versions, manifestInfos, runnerManifestInfos, systemInfos, osInfos, plugins, pluginImpls);
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
   @PostConstruct
   public synchronized void init() {

	// em will be injected by by java ee container or if not by aspectj
	try {
	   if (em == null) {
		em = UnmanagedEntityManagerFactory.currentEntityManager(KALEIDO_PERSISTENT_UNIT_NAME);
	   }
	} catch (PersistenceException pe) {
	   em = null;
	}

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

	// Then, load java system properties
	final Properties javaProperties = System.getProperties();
	for (final String key : javaProperties.stringPropertyNames()) {
	   STATIC_ENV_PARAMETERS.put(key, javaProperties.getProperty(key));
	}

	status = Status.INITIALIZED;

	// memorize current instance
	instance = this;

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

	if (status == Status.INITIALIZED) {
	   try {
		// Merge static environment variable values, to have the final value
		for (Entry<String, String> entry : STATIC_ENV_PARAMETERS.entrySet()) {
		   LOGGER.debug("Define environment variable {}={}", entry.getKey(), entry.getValue());
		   STATIC_ENV_PARAMETERS.put(entry.getKey(), StringHelper.resolveExpression(entry.getValue(), STATIC_ENV_PARAMETERS));
		}

		// I18n JPA enable or no
		if (org.kaleidofoundry.core.i18n.I18nMessagesProvider.isJpaEnabledForI18n()) {
		   LOGGER.info(CoreMessageBundle.getMessage("loader.define.i18n.jpa.enabled"));
		} else {
		   LOGGER.info(CoreMessageBundle.getMessage("loader.define.i18n.jpa.disabled"));
		}

		// Parse and set default locale if needed
		final String defaultLocale = STATIC_ENV_PARAMETERS.get(LOCAL_PROPERTY);
		if (!StringHelper.isEmpty(defaultLocale)) {
		   LOGGER.info(CoreMessageBundle.getMessage("loader.define.locale", defaultLocale));
		   final Locale setDefaultLocale = LocaleFactory.parseLocale(defaultLocale);
		   Locale.setDefault(setDefaultLocale);
		   LOGGER.info(StringHelper.replicate("*", 120));
		}

		// FileStore init (basedir ...)
		FileStoreProvider.init(STATIC_ENV_PARAMETERS.get(DEFAULT_BASE_DIR_PROPERTY));

		// Cache provider init (default cache provider ...)
		CacheManagerProvider.init(STATIC_ENV_PARAMETERS.get(CACHE_PROVIDER_PROPERTY));

		// Configurations to load
		final String kaleidoConfigurations = STATIC_ENV_PARAMETERS.get(CONFIGURATIONS_PROPERTY);
		if (!StringHelper.isEmpty(kaleidoConfigurations)) {
		   LOGGER.info(CoreMessageBundle.getMessage("loader.define.configurations",
			   StringHelper.replaceAll(kaleidoConfigurations, "\n", ",").replaceAll("\\s+", "")));
		   // load and register given configurations ids / url
		   try {
			ConfigurationFactory.init(StringHelper.replaceAll(kaleidoConfigurations, "\n", CONFIGURATIONS_PROPERTY_SEPARATOR));
		   } catch (final ResourceException rse) {
			throw new IllegalStateException(CoreMessageBundle.getMessage("loader.define.configurations.error", kaleidoConfigurations), rse);
		   }
		   LOGGER.info(StringHelper.replicate("*", 120));
		}

		// load the configuration annotations present in the main class (if defined)
		if (applicationClass != null) {
		   configurationInitializer = new NamedConfigurationProcessor(applicationClass);

		   for (NamedConfiguration namedConfig : configurationInitializer.getConfigurations()) {
			if (StringHelper
				.isEmpty(namedConfig.name())) { throw new ConfigurationException("config.annotation.illegal.name", applicationClass.getName()); }
			if (StringHelper
				.isEmpty(namedConfig.uri())) { throw new ConfigurationException("config.annotation.illegal.uri", applicationClass.getName()); }
			if (!ConfigurationFactory.getRegistry().contains(namedConfig.name())) {
			   ConfigurationFactory.provides(namedConfig.name(), namedConfig.uri());
			}
		   }

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
		// TODO Find an elegant way to do that...
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

		instance = null;

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
