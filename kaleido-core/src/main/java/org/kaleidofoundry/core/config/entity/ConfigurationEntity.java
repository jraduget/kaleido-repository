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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.kaleidofoundry.core.lang.annotation.Review;

/**
 * @author Jerome RADUGET
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(name = "Configuration")
@Table(name = "CONFIGURATION")
@Review(comment = "Audit information (locale zone for the date, user information...)")
public class ConfigurationEntity {

   @Id
   @XmlID
   private String name;
   private String uri;
   private String description;
   @XmlElementWrapper(name = "properties")
   @XmlElement(name = "property")
   private List<ConfigurationProperty> properties;

   public ConfigurationEntity() {
	this(null);
   }

   /**
    * @param name
    */
   public ConfigurationEntity(final String name) {
	this(name, null, null);
   }

   /**
    * @param name
    * @param uri
    */
   public ConfigurationEntity(final String name, final String uri) {
	this(name, uri, null);
   }

   /**
    * @param name
    * @param uri
    * @param description
    */
   public ConfigurationEntity(final String name, final String uri, final String description) {
	super();
	this.name = name;
	this.uri = uri;
	this.description = description;
	this.properties = new ArrayList<ConfigurationProperty>();
   }

   /**
    * @return the name
    */
   public String getName() {
	return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(final String name) {
	this.name = name;
   }

   /**
    * @return the description
    */
   public String getDescription() {
	return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(final String description) {
	this.description = description;
   }

   /**
    * @return the uri
    */
   public String getUri() {
	return uri;
   }

   /**
    * @param uri the uri to set
    */
   public void setUri(final String uri) {
	this.uri = uri;
   }

   /**
    * @return the properties
    */
   public List<ConfigurationProperty> getProperties() {
	return properties;
   }

   /**
    * @param properties the properties to set
    */
   public void setProperties(final List<ConfigurationProperty> properties) {
	this.properties = properties;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((uri == null) ? 0 : uri.hashCode());
	result = prime * result + ((description == null) ? 0 : description.hashCode());
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
	if (!(obj instanceof ConfigurationEntity)) { return false; }
	ConfigurationEntity other = (ConfigurationEntity) obj;
	if (description == null) {
	   if (other.description != null) { return false; }
	} else if (!description.equals(other.description)) { return false; }
	if (name == null) {
	   if (other.name != null) { return false; }
	} else if (!name.equals(other.name)) { return false; }
	if (uri == null) {
	   if (other.uri != null) { return false; }
	} else if (!uri.equals(other.uri)) { return false; }

	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "ConfigurationEntity [name=" + name + ", uri=" + uri + ", description=" + description + "]";
   }

}
