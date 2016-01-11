/*
 *  Copyright 2008-2016 the original author or authors.
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
package org.kaleidofoundry.core.config;

import java.util.ArrayList;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * This class is used to load some configurations at startup (for java main, junit launcher...) <br/>
 * The annotations {@link NamedConfiguration} presents on the class are scan and use to load the needed {@link Configuration} resources<br/>
 * <br/>
 * 
 * @author jraduget
 */
public class NamedConfigurationInitializer {

   private final Class<?> annotatedClass;
   private final List<NamedConfiguration> configurations;

   /**
    * @param annotatedClass
    */
   public NamedConfigurationInitializer(@NotNull final Class<?> annotatedClass) {
	configurations = new ArrayList<NamedConfiguration>();
	this.annotatedClass = annotatedClass;
   }

   /**
    * load declared configurations (using {@link NamedConfiguration})
    */
   public void init() {

	// register declared configurations
	if (annotatedClass.isAnnotationPresent(NamedConfiguration.class)) {
	   configurations.add(annotatedClass.getAnnotation(NamedConfiguration.class));
	}
	if (annotatedClass.isAnnotationPresent(NamedConfigurations.class)) {
	   for (NamedConfiguration namedConfig : annotatedClass.getAnnotation(NamedConfigurations.class).value()) {
		configurations.add(namedConfig);
	   }
	}

	for (NamedConfiguration namedConfig : configurations) {
	   if (StringHelper.isEmpty(namedConfig.name())) { throw new ConfigurationException("config.annotation.illegal.name", annotatedClass.getName()); }
	   if (StringHelper.isEmpty(namedConfig.uri())) { throw new ConfigurationException("config.annotation.illegal.uri", annotatedClass.getName()); }
	   if (!ConfigurationFactory.getRegistry().contains(namedConfig.name())) {
		ConfigurationFactory.provides(namedConfig.name(), namedConfig.uri());
	   }
	}
   }

   /**
    * unload all configurations that have been initialized in the init phase
    * 
    * @throws ResourceException
    */
   public void shutdown() throws ResourceException {
	// cleanup at end
	for (NamedConfiguration configuration : configurations) {
	   ConfigurationFactory.unregister(configuration.name());
	}
   }
}
