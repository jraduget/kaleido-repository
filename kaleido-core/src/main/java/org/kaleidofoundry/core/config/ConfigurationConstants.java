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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration package constants
 * 
 * @author Jerome RADUGET
 */
public interface ConfigurationConstants {

   /** Common configuration Logger */
   Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

   /** Some static environments / configurations parameters that could be shared */
   Map<String, String> STATIC_ENV_PARAMETERS = new ConcurrentHashMap<String, String>();

   /**
    * Configuration java environment name which specify the default configurations to load <br/>
    * <br/>
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
   String JavaEnvProperties = "kaleido.configurations";
   /** Configuration item separator */
   String JavaEnvPropertiesSeparator = ",";
   /** Configurations name / value separator */
   String JavaEnvPropertiesValueSeparator = "=";

   // ** Plugin part ***************************************************************************************************

   /**
    * configuration file store handled extension<br/>
    */
   static enum Extension {
	// if you change enum name, please report changed to plugin name
	properties,
	xmlproperties,
	xml,
	json,
	yaml,
	model,
	javasystem,
	osenv,
	mainargs
   }

   /** configuration interface plugin name */
   String ConfigurationPluginName = "configurations";
   /** classic properties implementation configuration plugin name - configuration resource uri have to end with '.properties' */
   String PropertiesConfigurationPluginName = "configurations.properties";
   /** xml properties implementation configuration plugin name - configuration resource uri have to end with '.xmlproperties' */
   String XmlPropertiesConfigurationPluginName = "configurations.xmlproperties";
   /** xml implementation configuration plugin name - configuration resource uri have to end with '.xml' */
   String XmlConfigurationPluginName = "configurations.xml";
   /** jpa model implementation for configuration, plugin name */
   String JpaModelConfigurationPluginName = "configurations.model";
   /** json implementation for configuration, plugin name */
   String JsonConfigurationPluginName = "configurations.json";
   /** yaml implementation for configuration, plugin name */
   String YamlConfigurationPluginName = "configurations.yaml";
   /** java env variable implementation configuration plugin name - configuration resource uri have to end with '.javasystem' */
   String JavaSystemConfigurationPluginName = "configurations.javasystem";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.osenv' */
   String OsEnvConfigurationPluginName = "configurations.osenv";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.mainargs' */
   String MainArgsConfigurationPluginName = "configurations.mainargs";

   // ** Key and property serialization part ***************************************************************************

   /** Separator to specify root */
   String KeyRoot = "//";
   /** Default separator for key name */
   String KeySeparator = "/";
   /** Separator to specify root for properties keys */
   String KeyPropertiesRoot = "";
   /** Default separator for properties name */
   String KeyPropertiesSeparator = ".";
   /** Root of an xml configuration */
   String XmlRootElement = "configuration";

}
