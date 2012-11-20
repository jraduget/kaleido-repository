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
package org.kaleidofoundry.core.i18n.model;

import static org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Table_I18nMessageGroupe;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.kaleidofoundry.core.lang.annotation.Task;

/**
 * Group of message<br/>
 * It can be a functional or technical group<br/>
 * <p>
 * Example of technical groups :
 * <ul>
 * <li>org/kaleidofoundry/i18n
 * <li>org/kaleidofoundry/cache
 * <li>org/kaleidofoundry/config
 * <li>...
 * <li>application/module/...
 * <li>...
 * </ul>
 * Example of functional groups :
 * <ul>
 * <li>sales
 * <li>marketing
 * <li>administration
 * </ul>
 * </p>
 * <br/>
 * You can also mix it :<br/>
 * 
 * <pre>
 * 	sales/customer/error
 * 	marketing/campaign/error
 * 	marketing/campaign/information
 * </pre>
 * 
 * @author Jerome RADUGET
 */
@Entity
// @Access(AccessType.PROPERTY)
@Table(name = Table_I18nMessageGroupe)
@Task(comment = "Audit information (locale zone for the date, user information...)")
public class I18nMessageGroup implements Serializable {

   private static final long serialVersionUID = 2526191394171289538L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************
   @Id
   @Column(name = "CODE")
   private String code;
   @ManyToOne(cascade = CascadeType.ALL, optional = true)
   private I18nMessage description;
   @ManyToOne(optional = true)
   private I18nMessageGroup parent;
   @Version
   Integer version;

   /**
    * default constructor
    */
   public I18nMessageGroup() {
   }

   /**
    * @param code
    */
   public I18nMessageGroup(final String code) {
	super();
	this.code = code;
   }

   /**
    * @param code
    * @param description
    */
   public I18nMessageGroup(final String code, final I18nMessage description) {
	this(code);
	this.description = description;
   }

   /**
    * @param code
    * @param description
    * @param parent
    */
   public I18nMessageGroup(final String code, final I18nMessage description, final I18nMessageGroup parent) {
	this(code, description);
	this.parent = parent;
   }

   // GETTER & SETTERS FOR POJO ***************************************************************************************

   /**
    * @return code identifier like :
    * 
    *         <pre>
    *   	"org/kaleidofoundry/i18n"
    *   	"org/kaleidofoundry/cache"
    *   	"org/kaleidofoundry/config"
    *   
    * 	"sales/customer/error"
    * 	"marketing/campaign/error"
    * 	"marketing/campaign/information"
    * </pre>
    */
   public String getCode() {
	return code;
   }

   /**
    * @return i18n description
    */
   public I18nMessage getDescription() {
	return description;
   }

   /**
    * @return the parent group
    */
   public I18nMessageGroup getParent() {
	return parent;
   }

   public void setCode(final String code) {
	this.code = code;
   }

   public void setDescription(final I18nMessage description) {
	this.description = description;
   }

   public void setParent(final I18nMessageGroup parent) {
	this.parent = parent;
   }

   // EQUALS / HASHCODE / TOSTRING / COMPARE / CLONE... FOR POJO ******************************************************

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (code == null ? 0 : code.hashCode());
	result = prime * result + (parent == null ? 0 : parent.hashCode());
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
	if (!(obj instanceof I18nMessageGroup)) { return false; }
	I18nMessageGroup other = (I18nMessageGroup) obj;
	if (description == null) {
	   if (other.description != null) { return false; }
	} else if (!description.equals(other.description)) { return false; }
	if (code == null) {
	   if (other.code != null) { return false; }
	} else if (!code.equals(other.code)) { return false; }
	if (parent == null) {
	   if (other.parent != null) { return false; }
	} else if (!parent.equals(other.parent)) { return false; }

	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "I18nMessageGroup [code=" + code + "]";
   }

}
