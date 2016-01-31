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

/**
 * This class is used to load some configurations at startup (for java main, junit launcher...) <br/>
 * The annotations {@link NamedConfiguration} presents on the class are scan and use to load the needed {@link Configuration} resources<br/>
 * <br/>
 * 
 * @author jraduget
 */
public class NamedConfigurationProcessor {

   private final Class<?> annotatedClass;
   private final List<NamedConfiguration> configurations;

   /**
    * @param annotatedClass
    */
   public NamedConfigurationProcessor(@NotNull final Class<?> annotatedClass) {
	configurations = new ArrayList<NamedConfiguration>();
	this.annotatedClass = annotatedClass;

	// register declared configurations
	if (annotatedClass.isAnnotationPresent(NamedConfiguration.class)) {
	   configurations.add(annotatedClass.getAnnotation(NamedConfiguration.class));
	}
	if (annotatedClass.isAnnotationPresent(NamedConfigurations.class)) {
	   for (NamedConfiguration namedConfig : annotatedClass.getAnnotation(NamedConfigurations.class).value()) {
		configurations.add(namedConfig);
	   }
	}
   }

   public Class<?> getAnnotatedClass() {
	return annotatedClass;
   }

   public List<NamedConfiguration> getConfigurations() {
	return configurations;
   }

}
