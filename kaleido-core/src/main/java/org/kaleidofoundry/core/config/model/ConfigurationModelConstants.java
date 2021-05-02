/*
 *  Copyright 2008-2021 the original author or authors.
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
package org.kaleidofoundry.core.config.model;

/**
 * @author jraduget
 */
public interface ConfigurationModelConstants {

   /** {@link ConfigurationModel} entity name */
   String Entity_Configuration = "Configuration";

   /** {@link ConfigurationProperty} entity name */
   String Entity_Property = "ConfigurationProperty";

   /** {@link ConfigurationModel} entity table name */
   String Table_Configuration = "CONFIGURATION";

   /** {@link ConfigurationProperty} entity table name */
   String Table_Property = "CONFIGURATION_PROPERTY";

   /** {@link ConfigurationProperty} association table name */
   String Table_ConfigurationProperties = "CONFIGURATION_PROPERTIES";

   /**
    * Query static final informations<br/>
    * Find configuration by name
    */
   public static interface Query_FindConfigurationByName {
	String Name = "config.findConfigurationByName";
	String Parameter_ConfigurationName = "configName";
	String Jql = "SELECT c FROM " + Entity_Configuration + " c WHERE c.name = :" + Parameter_ConfigurationName;
   }

   /**
    * Query static final informations<br/>
    * Find configuration using a text token
    */
   public static interface Query_FindConfigurationByText {
	String Name = "config.findConfigurationByText";
	String Parameter_Text = "text";
	String Jql = "SELECT c FROM " + Entity_Configuration + " c  WHERE " + "(c.name like :" + Parameter_Text + ") or (c.uri like :" + Parameter_Text
		+ ") or (c.description like :" + Parameter_Text + ") or (c.labels.items like :" + Parameter_Text + ")";
   }

   /**
    * Query static final informations<br/>
    * Find all configuration
    */
   public static interface Query_FindAllConfiguration {
	String Name = "config.findAllConfiguration";
	String Jql = "SELECT c FROM " + Entity_Configuration + " c";
   }

   /**
    * Query static final informations<br/>
    * Find configuration property by name
    */
   public static interface Query_FindPropertyByName {
	String Name = "config.findPropertyByName";
	String Parameter_ConfigurationName = "configName";
	String Parameter_Name = "name";
	String Jql = "SELECT cp FROM " + Entity_Property + " cp JOIN cp.configurations c  WHERE cp.name = :" + Parameter_Name + " AND c.name = :"
		+ Parameter_ConfigurationName;
   }

   /**
    * Query static final informations<br/>
    * Find configuration property by name
    */
   public static interface Query_FindPropertyByText {
	String Name = "config.findPropertyByName";
	String Parameter_ConfigurationName = "configName";
	String Parameter_Text = "text";
	String Parameter_Name = "name";
	String Jql = "SELECT cp FROM " + Entity_Property + " cp JOIN cp.configurations c  WHERE " + "(c.name = :" + Parameter_ConfigurationName + " or :"
		+ Parameter_ConfigurationName + " IS NULL) " + "and ((cp.name like :" + Parameter_Text + ") or (cp.value like :" + Parameter_Text
		+ ") or (cp.description like :" + Parameter_Text + ") or (cp.labels.items like :" + Parameter_Text + "))";
   }

}
