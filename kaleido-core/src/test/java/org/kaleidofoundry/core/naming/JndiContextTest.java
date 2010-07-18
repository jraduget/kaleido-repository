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
package org.kaleidofoundry.core.naming;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test client Jndi
 * 
 * @author Jerome RADUGET
 */
public class JndiContextTest {

   private static Logger logger = LoggerFactory.getLogger(JndiContext.class);

   private static final String CurrentPath = FileHelper.getRelativeClassPath(JndiContextTest.class);

   public static void main(final String[] args) {

	final String classpathRessource = CurrentPath + "jndi.properties";
	final String lineSep = StringHelper.replicate("*", 80);
	Configuration config = null;

	JndiContext<?> fullContext = null;
	JndiContext<?> firstContext = null;
	JndiContext<?> secondContext = null;
	// JndiContext<?> extractContext = null;

	try {
	   // Configuration instance and load
	   logger.info("config.create(\"" + classpathRessource + "\")");
	   config = ConfigurationFactory.provideConfiguration("jndiTest", "classpath:/" + classpathRessource, new RuntimeContext<Configuration>());
	   logger.info("config.load()");
	   config.load();
	   logger.info("config.toString() : " + config.toString());
	   logger.info(lineSep);

	   // JndiContext par défaut (prefix jndi.context, sans nom)
	   fullContext = new JndiContext<Object>(config);
	   logger.info("fullContext.getName() : " + fullContext.getName());
	   logger.info("fullContext.getJavaEnvNamePrefix() : " + fullContext.getJavaEnvNamePrefix());
	   logger.info("fullContext.getInitialContextFactory() : " + fullContext.getInitialContextFactory());
	   logger.info("fullContext.getProviderUrl() : " + fullContext.getProviderUrl());
	   logger.info("fullContext.getUrlPkgPrefixes() : " + fullContext.getUrlPkgPrefixes());
	   logger.info("fullContext.getDnsUrl() : " + fullContext.getDnsUrl());
	   logger.info("fullContext.toString() : " + fullContext.toString());
	   logger.info(lineSep);

	   // JndiContext jboss (prefix par défaut jndi.context)
	   firstContext = new JndiContext<Object>("jboss", config);
	   logger.info("firstContext.getName() : " + firstContext.getName());
	   logger.info("firstContext.getJavaEnvNamePrefix() : " + fullContext.getJavaEnvNamePrefix());
	   logger.info("firstContext.getInitialContextFactory() : " + firstContext.getInitialContextFactory());
	   logger.info("firstContext.getProviderUrl() : " + firstContext.getProviderUrl());
	   logger.info("firstContext.getUrlPkgPrefixes() : " + firstContext.getUrlPkgPrefixes());
	   logger.info("firstContext.getDnsUrl() : " + firstContext.getDnsUrl());
	   logger.info("firstContext.toString() : " + firstContext.toString());
	   logger.info(lineSep);

	   // JndiContext was (prefix par défaut jndi.context)
	   secondContext = new JndiContext<Object>("was5", config);
	   logger.info("secondContext.getName() : " + secondContext.getName());
	   logger.info("secondContext.getJavaEnvNamePrefix() : " + fullContext.getJavaEnvNamePrefix());
	   logger.info("secondContext.getInitialContextFactory() : " + secondContext.getInitialContextFactory());
	   logger.info("secondContext.getProviderUrl() : " + secondContext.getProviderUrl());
	   logger.info("secondContext.getUrlPkgPrefixes() : " + secondContext.getUrlPkgPrefixes());
	   logger.info("secondContext.getDnsUrl() : " + secondContext.getDnsUrl());
	   logger.info("secondContext.toString() : " + secondContext.toString());
	   logger.info(lineSep);

	   // Test de l'extractContext
	   // extractContext = new JndiContext<Object>("jboss-extract", firstContext.extractContext());
	   // logger.info("extractContext.getName() : " + extractContext.getName());
	   // logger.info("extractContext.getJavaEnvNamePrefix() : " + fullContext.getJavaEnvNamePrefix());
	   // logger.info("extractContext.getInitialContextFactory() : " + extractContext.getInitialContextFactory());
	   // logger.info("extractContext.getProviderUrl() : " + extractContext.getProviderUrl());
	   // logger.info("extractContext.getUrlPkgPrefixes() : " + extractContext.getUrlPkgPrefixes());
	   // logger.info("extractContext.getDnsUrl() : " + extractContext.getDnsUrl());
	   // logger.info("extractContext.toString() : " + extractContext.toString());
	   // logger.info(lineSep);

	} catch (final StoreException cfe) {
	   logger.error("configuration error", cfe);
	}
   }
}
