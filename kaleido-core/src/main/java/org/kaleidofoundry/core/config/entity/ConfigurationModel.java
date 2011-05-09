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

import static org.kaleidofoundry.core.config.entity.ConfigurationModelConstants.Entity_Configuration;
import static org.kaleidofoundry.core.config.entity.ConfigurationModelConstants.Table_Configuration;
import static org.kaleidofoundry.core.config.entity.ConfigurationModelConstants.Table_ConfigurationProperties;
import static org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum.Improvement;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.kaleidofoundry.core.lang.annotation.Review;

/**
 * @author Jerome RADUGET
 */
@Entity(name = Entity_Configuration)
// @Access(AccessType.PROPERTY)
@Table(name = Table_Configuration, uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME" }), @UniqueConstraint(columnNames = { "URI" }) })
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@Review(comment = "Audit information (locale zone for the date, user information...)", category = Improvement)
public class ConfigurationModel implements Serializable {

   private static final long serialVersionUID = -2875384104892173181L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************

   @Id
   @GeneratedValue
   private Long id;
   @Column(name = "URI", unique = true)
   private String uri;
   @XmlID
   @Column(name = "NAME", unique = true)
   private String name;
   private String description;
   @XmlElementWrapper(name = "properties")
   @XmlElement(name = "property")
   @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinTable(name = Table_ConfigurationProperties, joinColumns = @JoinColumn(name = "CONFIGURATION_ID"), inverseJoinColumns = @JoinColumn(name = "PROPERTY_ID"))
   private Set<ConfigurationProperty> properties;
   @Version
   @XmlTransient
   Integer version;

   public ConfigurationModel() {
	this(null);
   }

   /**
    * @param name
    */
   public ConfigurationModel(final String name) {
	this(name, null, null);
   }

   /**
    * @param name
    * @param uri
    */
   public ConfigurationModel(final String name, final String uri) {
	this(name, uri, null);
   }

   /**
    * @param name
    * @param uri
    * @param description
    */
   public ConfigurationModel(final String name, final String uri, final String description) {
	super();
	this.name = name;
	this.uri = uri;
	this.description = description;
	this.properties = new HashSet<ConfigurationProperty>();
   }

   /**
    * @return persistent identifier
    */
   public Long getId() {
	return id;
   }

   /**
    * @return the name
    */
   public String getName() {
	return name;
   }

   /**
    * @param id the persistent id to set
    */
   public void setId(final Long id) {
	this.id = id;
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
   public Set<ConfigurationProperty> getProperties() {
	return properties;
   }

   /**
    * @param properties the properties to set
    */
   public void setProperties(final Set<ConfigurationProperty> properties) {
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
	result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
	if (!(obj instanceof ConfigurationModel)) { return false; }
	ConfigurationModel other = (ConfigurationModel) obj;
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
	return "ConfigurationEntity [uri=" + uri + ", name=" + name + ", description=" + description + "]";
   }

}
