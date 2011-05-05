/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.config.entity;

/**
 * @author Jerome RADUGET
 */
public interface ConfigurationEntityConstants {

   /** {@link ConfigurationEntity} entity name */
   String Entity_Configuration = "Configuration";

   /** {@link ConfigurationProperty} entity name */
   String Entity_Property = "ConfigurationProperty";

   /** {@link ConfigurationEntity} entity table name */
   String Table_Configuration = "CONFIGURATION";

   /** {@link ConfigurationProperty} entity table name */
   String Table_Property = "CONFIGURATION_PROPERTY";

   /** {@link ConfigurationProperty} association table name */
   String Table_ConfigurationProperties = "CONFIGURATION_PROPERTIES";

   /**
    * Query static final informations
    */
   public static interface Query_ConfigurationPropertyByName {
	String Name = "config.findPropertyByName";
	String Parameter_ConfigurationName = "configName";
	String Parameter_Name = "name";
	String Jql = "SELECT cp FROM " + Entity_Property + " cp JOIN cp.configurations c  WHERE cp.name = :" + Parameter_Name + " AND c.name = :"
		+ Parameter_ConfigurationName;
   }
   // SELECT cp FROM ConfigurationProperty cp JOIN Configuration c.properties WHERE cp.name = :name AND c.name = :configName
   /*
    * select distinct t
    * from Trade t
    * join t.combo c
    * join c.legs l
    * join l.exchange e
    * where e.exchangeShortName = 'whatever'
    */

}
