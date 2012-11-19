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

import java.util.Enumeration;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kaleidofoundry.core.config.EnvironmentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup listener used to initialize some webapp resource like :
 * <ul>
 * <li>the plugin registry</li>
 * <li>the default user locale</li>
 * <li>the configuration resources to load</li>
 * <li>the current servlet context</li>
 * <li>the current cache provider</li>
 * <li>the jpa support for i18n messages bundles</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class StartupListener implements ServletContextListener {

   private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

   private final EnvironmentInitializer initializer = new EnvironmentInitializer(LOGGER);

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(final ServletContextEvent sce) {
	ServletContextProvider.init(sce.getServletContext());

	// load operating system and java system env variables
	initializer.init();

	// Then, load web application init parameters
	@SuppressWarnings("unchecked")
	Enumeration<String> names = sce.getServletContext().getInitParameterNames();
	while (names != null && names.hasMoreElements()) {
	   String name = names.nextElement();
	   initializer.getEnvironments().put(name, sce.getServletContext().getInitParameter(name));
	}

	// Then load web application environment parameters
	try {
	   Context initCtx = new InitialContext();
	   NamingEnumeration<Binding> bindingsEnum = initCtx.listBindings("java:comp/env");
	   while (bindingsEnum.hasMore()) {
		Binding binding = bindingsEnum.next();
		initializer.getEnvironments().put(binding.getName(), binding.getObject().toString());
		LOGGER.info(WebMessageBundle.getMessage("loader.define.environment.parameter", binding.getName(), binding.getObject().toString()));
	   }
	} catch (NamingException e) {
	   if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Loading webapp environment entries is not enabled", e);
	   } else {
		LOGGER.warn("Loading webapp environment entries is not enabled");
	   }
	}

	initializer.start();
   }

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(final ServletContextEvent sce) {
	initializer.stop();
   }

}