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

   /**
    * Configuration java environment name which specify the default configurations to load <br/>
    * <br/>
    * Syntax of the property value :
    * 
    * <pre>
    * configurationId01:configurationUri01;configurationId02:configurationUri02;...
    * </pre>
    * 
    * Example :
    * 
    * <pre>
    * java -Dkaleido.configurations=datasource:classpath:/datasource.properties;otherResource:http:/host/path/otherResource;...  YourMainClass
    * </pre>
    */
   String JavaEnvProperties = "kaleido.configuration";
   /** Configuration item separator */
   String JavaEnvPropertiesSeparator = ";";
   /** Configuration value separator */
   String JavaEnvPropertiesValueSeparator = "=";

   // ** Plugin part ***************************************************************************************************

   /**
    * configuration resource store handled extension<br/>
    */
   static enum Extension {
	// if you change enum name, please report changed to plugin name
	properties,
	xmlproperties,
	xml,
	javasystem,
	osenv,
	mainargs
   }

   /** configuration interface plugin name */
   String ConfigurationPluginName = "configuration";
   /** classic properties implementation configuration plugin name - configuration resource uri have to end with '.properties' */
   String PropertiesConfigurationPluginName = "configuration.properties";
   /** xml properties implementation configuration plugin name - configuration resource uri have to end with '.xmlproperties' */
   String XmlPropertiesConfigurationPluginName = "configuration.properties.xml";
   /** xml implementation configuration plugin name - configuration resource uri have to end with '.xml' */
   String XmlConfigurationPluginName = "configuration.xml";
   /** java env variable implementation configuration plugin name - configuration resource uri have to end with '.javasystem' */
   String JavaSystemConfigurationPluginName = "configuration.javasystem";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.osenv' */
   String OsEnvConfigurationPluginName = "configuration.osenv";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.mainargs' */
   String MainArgsConfigurationPluginName = "configuration.mainargs";

   // ** Key and property serialization part ***************************************************************************

   /** Separator to specify root */
   String KeyRoot = "//";
   /** Default separator for key name */
   String KeySeparator = "/";
   /** Separator to specify root for properties keys */
   String KeyPropertiesRoot = "";
   /** Default separator for properties name */
   String KeyPropertiesSeparator = ".";
   /** Multiple value separator */
   String MultiValDefaultSeparator = " ";
   /** String Date Formatter */
   String StrDateFormat = "yyyy-MM-dd'T'hh:mm:ss"; // yyyy-MM-ddThh:mm:ss
   /** String Number Formatter */
   String StrNumberFormat = "##0.0####";

}
