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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.kaleidofoundry.core.lang.annotation.Review;

/**
 * @author Jerome RADUGET
 */
@XmlRootElement(name = "property")
@Entity(name = "ConfigurationPropety")
@Table(name = "CONFIGURATION_PROPERTY")
@Review(comment = "Audit information (locale zone for the date, user information...)")
public class ConfigurationProperty {

   @Id
   @XmlTransient
   private ConfigurationEntity configuration;
   @Id
   private String name;
   private String description;

   /**
    * 
    */
   public ConfigurationProperty() {
	this(null, null, null);
   }

   /**
    * @param configuration the configuration of the property
    * @param name property name
    */
   public ConfigurationProperty(final ConfigurationEntity configuration, final String name) {
	this(configuration, name, null);
   }

   /**
    * @param configuration the configuration of the property
    * @param name property name
    * @param description optional description
    */
   public ConfigurationProperty(final ConfigurationEntity configuration, final String name, final String description) {
	super();
	this.configuration = configuration;
	this.name = name;
	this.description = description;
   }

   /**
    * @return the configuration of the property
    */
   public ConfigurationEntity getConfiguration() {
	return configuration;
   }

   /**
    * @return property name
    */
   public String getName() {
	return name;
   }

   /**
    * @return property description
    */
   public String getDescription() {
	return description;
   }

   /**
    * @param configuration the configuration to set
    */
   public void setConfiguration(final ConfigurationEntity configuration) {
	this.configuration = configuration;
   }

   /**
    * @param name the property to set
    */
   public void setName(final String name) {
	this.name = name;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(final String description) {
	this.description = description;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
	result = prime * result + ((description == null) ? 0 : description.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof ConfigurationProperty)) { return false; }
	ConfigurationProperty other = (ConfigurationProperty) obj;
	if (configuration == null) {
	   if (other.configuration != null) { return false; }
	} else if (!configuration.equals(other.configuration)) { return false; }
	if (description == null) {
	   if (other.description != null) { return false; }
	} else if (!description.equals(other.description)) { return false; }
	if (name == null) {
	   if (other.name != null) { return false; }
	} else if (!name.equals(other.name)) { return false; }
	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "ConfigurationProperty [configuration=" + (configuration != null ? configuration.getName() : "") + ", name=" + name + ", description="
		+ description + "]";
   }

}
