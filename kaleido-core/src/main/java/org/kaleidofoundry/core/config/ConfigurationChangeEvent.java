/*  
 * Copyright 2008-2014 the original author or authors 
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

import java.beans.PropertyChangeEvent;
import java.io.Serializable;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * The event which is propagated when a configuration changes occurred
 * 
 * @author jraduget
 */
public class ConfigurationChangeEvent extends PropertyChangeEvent {

   private static final long serialVersionUID = -5595187652171425845L;

   /**
    * Enumeration of a kind of configuration changes
    * 
    * @author jraduget
    */
   public static enum ConfigurationChangeType {
	CREATE,
	UPDATE,
	REMOVE
   }

   private final ConfigurationChangeType configurationChangeType;

   /**
    * @param source
    * @param propertyName
    * @param oldValue
    * @param newValue
    * @param configurationChangeType
    */
   private ConfigurationChangeEvent(final Configuration source, final String propertyName, final Serializable oldValue, final Serializable newValue,
	   @NotNull final ConfigurationChangeType configurationChangeType) {
	super(source, propertyName, oldValue, newValue);
	this.configurationChangeType = configurationChangeType;

   }

   /**
    * @param source
    * @param propertyName
    * @param newValue
    * @return new configuration change event
    */
   public static ConfigurationChangeEvent newCreateEvent(final Configuration source, final String propertyName, final Serializable newValue) {
	return new ConfigurationChangeEvent(source, propertyName, null, newValue, ConfigurationChangeType.CREATE);
   }

   /**
    * @param source
    * @param propertyName
    * @param oldValue
    * @param newValue
    * @return update configuration change event
    */
   public static ConfigurationChangeEvent newUpdateEvent(final Configuration source, final String propertyName, final Serializable oldValue,
	   final Serializable newValue) {
	return new ConfigurationChangeEvent(source, propertyName, oldValue, newValue, ConfigurationChangeType.UPDATE);
   }

   /**
    * @param source
    * @param propertyName
    * @param oldValue
    * @return remove configuration change event
    */
   public static ConfigurationChangeEvent newRemoveEvent(final Configuration source, final String propertyName, final Serializable oldValue) {
	return new ConfigurationChangeEvent(source, propertyName, oldValue, null, ConfigurationChangeType.REMOVE);
   }

   /**
    * @return kind of change
    * @see ConfigurationChangeType
    */
   public ConfigurationChangeType getConfigurationChangeType() {
	return configurationChangeType;
   }

}
