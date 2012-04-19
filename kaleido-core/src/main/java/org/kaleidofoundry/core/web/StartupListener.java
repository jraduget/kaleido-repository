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
package org.kaleidofoundry.core.web;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.WebMessageBundle;

import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kaleidofoundry.core.config.ConfigurationConstants;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesProvider;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.util.locale.LocaleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Startup listener used to initialize some webapp resource like :
 * <ul>
 * <li>the plugin registry</li>
 * <li>the default user locale</li>
 * <li>the configuration resources to load</li>
 * <li>the current servlet context</li>
 * <li>the jpa support for i18n messages bundles</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class StartupListener implements ServletContextListener {

   private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(final ServletContextEvent sce) {
	ServletContextProvider.init(sce.getServletContext());

	// I18n JPA enable or not
	I18nMessagesFactory.disableJpaControl();
	String enableI18nJpa = sce.getServletContext().getInitParameter(I18nMessagesProvider.ENABLE_JPA_PROPERTY);
	enableI18nJpa = StringHelper.isEmpty(enableI18nJpa) ? Boolean.FALSE.toString() : enableI18nJpa;

	if (Boolean.valueOf(enableI18nJpa)) {
	   LOGGER.info(WebMessageBundle.getMessage("web.filter.start.i18n.jpa.enabled"));
	   I18nMessagesFactory.enableJpaControl();
	} else {
	   LOGGER.info(WebMessageBundle.getMessage("web.filter.start.i18n.jpa.disabled"));
	   I18nMessagesFactory.disableJpaControl();
	}

	// Static load of the plugin registry
	PluginFactory.getInterfaceRegistry();
	LOGGER.info(StringHelper.replicate("*", 120));

	// Parse and set default locale if needed
	final String webappDefaultLocale = sce.getServletContext().getInitParameter(LocaleFactory.JavaEnvProperties);
	if (!StringHelper.isEmpty(webappDefaultLocale)) {
	   LOGGER.info(WebMessageBundle.getMessage("web.filter.start.locale", webappDefaultLocale));
	   final Locale setDefaultLocale = LocaleFactory.parseLocale(webappDefaultLocale);
	   Locale.setDefault(setDefaultLocale);
	   LOGGER.info(StringHelper.replicate("*", 120));
	}

	// Parse the default configurations and load it if needed
	final String kaleidoConfigurations = sce.getServletContext().getInitParameter(ConfigurationConstants.JavaEnvProperties);
	if (!StringHelper.isEmpty(kaleidoConfigurations)) {
	   LOGGER.info(WebMessageBundle.getMessage("web.filter.start.configurations",
		   StringHelper.replaceAll(kaleidoConfigurations, "\n", ",").replaceAll("\\s+", "")));
	   System.getProperties().put(ConfigurationConstants.JavaEnvProperties,
		   StringHelper.replaceAll(kaleidoConfigurations, "\n", ConfigurationConstants.JavaEnvPropertiesSeparator));
	   // load and register given configurations ids / url
	   try {
		ConfigurationFactory.init();
	   } catch (final ResourceException rse) {
		throw new IllegalStateException(WebMessageBundle.getMessage("web.filter.start.configurations.error", kaleidoConfigurations),
			rse);
	   }
	   LOGGER.info(StringHelper.replicate("*", 120));
	}
   }

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(final ServletContextEvent sce) {
	// unload and unregister given configurations ids / url
	try {
	   LOGGER.info(WebMessageBundle.getMessage("web.filter.stop.configurations"));
	   ConfigurationFactory.unregisterAll();
	} catch (final ResourceException rse) {
	   throw new IllegalStateException(rse);
	}
   }

}