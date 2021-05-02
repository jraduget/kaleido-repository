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

import static org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Entity_Configuration;
import static org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Table_Configuration;
import static org.kaleidofoundry.core.config.model.ConfigurationModelConstants.Table_ConfigurationProperties;
import static org.kaleidofoundry.core.lang.annotation.TaskLabel.Enhancement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.label.model.LabelCategory;
import org.kaleidofoundry.core.lang.label.model.Labels;

/**
 * @author jraduget
 */
@Entity(name = Entity_Configuration)
// @Access(AccessType.PROPERTY)
@Table(name = Table_Configuration, uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME" }), @UniqueConstraint(columnNames = { "URI" }) })
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@Task(comment = "Audit information (locale zone for the date, user information...)", labels = Enhancement)
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
   @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @JoinTable(name = Table_ConfigurationProperties, joinColumns = @JoinColumn(name = "CONFIGURATION_ID"), inverseJoinColumns = @JoinColumn(name = "PROPERTY_ID"))
   private Set<ConfigurationProperty> properties;
   private Labels labels;
   private boolean loaded;
   private boolean storable;
   private boolean updateable;
   @Version
   // @XmlTransient
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
	this(name, uri, description, null);
   }

   /**
    * @param name
    * @param uri
    * @param description
    * @param labels
    */
   public ConfigurationModel(final String name, final String uri, final String description, final Labels labels) {
	super();
	this.name = name;
	this.uri = uri;
	this.description = description;
	this.properties = new HashSet<ConfigurationProperty>();
	setLabels(labels);
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
    * @return the labels
    */
   public Labels getLabels() {
	return labels;
   }

   /**
    * @param labels the labels to set
    */
   public void setLabels(final Labels labels) {
	this.labels = labels;
	if (this.labels != null) {
	   this.labels.setCategory(LabelCategory.Configuration);
	}
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

   /**
    * configuration is loaded ?
    * 
    * @return <code>true|false</code>
    */
   public boolean isLoaded() {
	return loaded;
   }

   /**
    * @param loaded the loaded to set
    */
   public void setLoaded(final boolean loaded) {
	this.loaded = loaded;
   }

   /**
    * the configuration allows to be stored ?
    * 
    * @return <code>true|false</code>
    */
   public boolean isStorable() {
	return storable;
   }

   /**
    * @param storable the storable to set
    */
   public void setStorable(final boolean storable) {
	this.storable = storable;
   }

   /**
    * the configuration allows update ?
    * 
    * @return <code>true|false</code>
    */
   public boolean isUpdateable() {
	return updateable;
   }

   /**
    * @param updateable the updateable to set
    */
   public void setUpdateable(final boolean updateable) {
	this.updateable = updateable;
   }

   /**
    * @return properties by name
    */
   public Map<String, ConfigurationProperty> getPropertiesByName() {
	Map<String, ConfigurationProperty> map = new HashMap<String, ConfigurationProperty>();
	if (properties != null) {
	   for (ConfigurationProperty cp : properties) {
		map.put(cp.getName(), cp);
	   }
	}
	return map;
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
	StringBuilder result = new StringBuilder();
	result.append("ConfigurationModel [");
	result.append("name=").append(name).append(",");
	result.append("uri=").append(uri).append(",");
	result.append("description=").append(description).append(",");
	result.append("id=").append(id).append("");
	result.append("]");
	return result.toString();
   }

}
