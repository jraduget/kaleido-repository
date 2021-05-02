/*
 * Copyright 2008-2021 the original author or authors
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
package org.kaleidofoundry.core.env.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.cache.CacheProvidersEnum;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.store.FileStore;

/**
 * Environment properties and constants used to configure and initialize kaleido.
 * The value of this properties could be define as :
 * <ul>
 * <li>java system variables</li>
 * <li>web.xml context-param</li>
 * <li>...</li>
 * </ul>
 * <p>
 * <b>java system usage :</b> -Dkaleido.configurations=file:/${basedir}/applications.properties,context=file:/${basedir}/context.properties
 * -Dkaleido.locale=en
 * </p>
 * <p>
 * <b>web.xml context parameter example :</b> <code>
 *   	<context-param>
 * 		<param-name>kaleido.locale</param-name>
 * 		<param-value>en</param-value>
 * 	</context-param>
 *   	<context-param>
 * 		<param-name>kaleido.configurations</param-name>
 * 		<param-value>
 * 			configuration=file:/${basedir}/applications.properties
 * 			context=file:/${basedir}/context.properties			
 * 		</param-value>
 * 	</context-param>
 * </code>
 * </p>
 * 
 * @author jraduget
 */
public interface EnvironmentConstants {

   /**
    * The value of this property is used to define the current basedir of the application.<br/>
    * You can use this variable as a variable ${basedir.default} in your {@link FileStore} resource uri.
    */
   String DEFAULT_BASE_DIR_PROPERTY = "basedir.default";

   /**
    * The value of this property is a class name, that will be used to extract some meta information
    * (to load configurations, default logger...)
    * This class name that could contains some initializer annotations like {@link NamedConfigurations} or {@link NamedConfiguration} to
    * load and
    * process.
    */
   String INITIALIZER_CLASS_PROPERTY = "kaleido.initializer.class";

   /**
    * The value of this property will defined the default cache provider to use
    * 
    * @see {@link CacheProvidersEnum}
    */
   String CACHE_PROVIDER_PROPERTY = "kaleido.cacheprovider";

   /**
    * The value of this property is used to define the configurations to load. <br/>
    * Syntax of the property value :
    * 
    * <pre>
    * configurationId01:configurationUri01,configurationId02:configurationUri02,...
    * </pre>
    * 
    * Example :
    * 
    * <pre>
    * java -Dkaleido.configurations=dsConfig=classpath:/datasource.properties,otherResource=http:/host/path/otherResource,...  YourMainClass
    * </pre>
    */
   String CONFIGURATIONS_PROPERTY = "kaleido.configurations";

   /**
    * The value of this property is used to define a default local settings
    */
   String LOCAL_PROPERTY = "kaleido.locale";

   /** Enable or disable jpa entity manager resolution for resourceBundle */
   String I18N_JPA_ACTIVATION_PROPERTY = "kaleido.i18n.jpa.enabled";

   /**
    * default name for internal kaleidofoundry persistent context unit name
    * (declare into persitence.xml)
    */
   String KALEIDO_PERSISTENT_UNIT_NAME = "kaleido";

   /** Some static environments / configurations parameters that could be shared */
   Map<String, String> STATIC_ENV_PARAMETERS = new ConcurrentHashMap<String, String>();

   /** Configurations item separator */
   String CONFIGURATIONS_PROPERTY_SEPARATOR = ",";

   /** Configuration name / value separator */
   String CONFIGURATIONS_PROPERTY_VALUE_SEPARATOR = "=";

}
