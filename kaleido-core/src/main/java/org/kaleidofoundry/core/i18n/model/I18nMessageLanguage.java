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

import static org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Table_I18nMessageLanguage;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.kaleidofoundry.core.i18n.model.I18nMessageConstants.Query_MessagesByLocale;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * a translated message
 * 
 * @author Jerome RADUGET
 */
@Entity
// @Access(AccessType.PROPERTY)
@Table(name = Table_I18nMessageLanguage, uniqueConstraints = { @UniqueConstraint(columnNames = { "MESSAGE_ID", "LOCALE" }) })
@NamedQueries({ @NamedQuery(name = Query_MessagesByLocale.Name, query = Query_MessagesByLocale.Jql) })
@XmlRootElement(name = "i18n")
@XmlAccessorType(XmlAccessType.FIELD)
@Task(comment = "Audit information (locale zone for the date, user information...)")
public class I18nMessageLanguage implements Serializable {

   private static final long serialVersionUID = -212656122786380270L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   @XmlID
   private Integer id;
   @ManyToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID")
   @XmlTransient
   private I18nMessage message;
   @Column(name = "LOCALE_ID")
   private String localeId;
   @Transient
   @XmlTransient
   private Locale locale;
   private String isoLanguage;
   private String content;
   @Version
   Integer version;

   public I18nMessageLanguage() {
   }

   /**
    * @param message the i18n message
    * @param content message translation for the given locale country
    * @param locale the user locale. {@link Locale#getISO3Language()} value will be used as language Identifier
    */
   public I18nMessageLanguage(@NotNull final I18nMessage message, @NotNull final String content, @NotNull final Locale locale) {
	this.message = message;
	this.content = content;
	setLocale(locale);
   }

   // GETTER & SETTERS FOR POJO ***************************************************************************************
   /**
    * @return the id
    */
   public Integer getId() {
	return id;
   }

   /**
    * @return {@link Locale} persistence id
    */
   protected String getLocaleId() {
	return localeId;
   }

   /**
    * @return message locale
    */
   @Transient
   public Locale getLocale() {
	return locale;
   }

   /**
    * @param locale the locale of the message to set
    */
   public void setLocale(final Locale locale) {
	this.locale = locale;
	if (locale != null) {
	   localeId = locale.toString();
	   isoLanguage = StringHelper.isEmpty(locale.getISO3Language()) ? "ROOT" : locale.getISO3Language();
	} else {
	   this.locale = null;
	   localeId = null;
	}
   }

   /**
    * @return iso code of the language of the message
    */
   public String getIsoLanguage() {
	return isoLanguage;
   }

   /**
    * @return the parent i18n message
    */
   public I18nMessage getMessage() {
	return message;
   }

   /**
    * @return the message content (for the specify language)
    */
   public String getContent() {
	return content;
   }

   protected void setLocaleId(final String localeId) {
	this.localeId = localeId;
   }

   protected void setId(final Integer id) {
	this.id = id;
   }

   void setIsoLanguage(final String isoLanguage) {
	this.isoLanguage = isoLanguage;
   }

   public void setMessage(final I18nMessage message) {
	this.message = message;
   }

   public void setContent(final String content) {
	this.content = content;
   }

   // EQUALS / HASHCODE / TOSTRING / COMPARE / CLONE... FOR POJO ******************************************************

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "I18nMessageLanguage [id=" + id + "\t,localeId=" + localeId + ",\tmessage=" + (message != null ? message.getCode() : "null") + ",\tcontent='"
		+ content + "']";
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (content == null ? 0 : content.hashCode());
	result = prime * result + (id == null ? 0 : id.hashCode());
	result = prime * result + (isoLanguage == null ? 0 : isoLanguage.hashCode());
	result = prime * result + (localeId == null ? 0 : localeId.hashCode());
	result = prime * result + (message == null ? 0 : message.hashCode());
	return result;
   }

   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof I18nMessageLanguage)) { return false; }
	I18nMessageLanguage other = (I18nMessageLanguage) obj;
	if (content == null) {
	   if (other.content != null) { return false; }
	} else if (!content.equals(other.content)) { return false; }
	if (id == null) {
	   if (other.id != null) { return false; }
	} else if (!id.equals(other.id)) { return false; }
	if (isoLanguage == null) {
	   if (other.isoLanguage != null) { return false; }
	} else if (!isoLanguage.equals(other.isoLanguage)) { return false; }
	if (localeId == null) {
	   if (other.localeId != null) { return false; }
	} else if (!localeId.equals(other.localeId)) { return false; }
	if (message == null) {
	   if (other.message != null) { return false; }
	} else if (!message.equals(other.message)) { return false; }
	return true;
   }

}
