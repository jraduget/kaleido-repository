package org.kaleidofoundry.core.i18n.entity;

import static org.kaleidofoundry.core.i18n.entity.I18nMessageConstants.DefaultMessageGroup;
import static org.kaleidofoundry.core.i18n.entity.I18nMessageConstants.Table_I18nMessage;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * @author Jerome RADUGET
 */
@Entity
@Table(name = Table_I18nMessage, uniqueConstraints = { @UniqueConstraint(columnNames = { "CODE", "GROUP_CODE" }) })
public class I18nMessage implements Serializable {

   private static final long serialVersionUID = 1585648396936219771L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Integer id;
   @Column(name = "CODE")
   private String code;
   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinColumn(name = "GROUPE_CODE", referencedColumnName = "CODE")
   private I18nMessageGroup group;
   private String description;
   private I18nMessageType type;
   @OneToMany(cascade = CascadeType.ALL)
   private Set<I18nMessageLanguage> messageLanguages;
   @Temporal(TemporalType.TIMESTAMP)
   @Column(insertable = false, updatable = true, nullable = true)
   private Date lastUsed;
   private boolean enabled;

   // TODO Audit information (locale zone for the date, user information...)

   public I18nMessage() {
	this(null);
   }

   /**
    * @param code
    */
   public I18nMessage(final String code) {
	this(code, (I18nMessageGroup) null);
   }

   /**
    * @param code
    * @param group if null a DEFAULT group will be used
    */
   public I18nMessage(final String code, final I18nMessageGroup group) {
	this.code = code;
	this.group = group == null ? DefaultMessageGroup : group;
	messageLanguages = new HashSet<I18nMessageLanguage>();
	enabled = true;
   }

   /**
    * @param code
    * @param description
    */
   public I18nMessage(final String code, final String description) {
	this(code);
	this.description = description;
   }

   /**
    * @param code
    * @param description
    * @param group
    */
   public I18nMessage(final String code, final String description, final I18nMessageGroup group) {
	this(code, group);
	this.description = description;
   }

   // GETTER & SETTERS FOR POJO ***************************************************************************************

   /**
    * @return unique system identifier
    */
   public Integer getId() {
	return id;
   }

   /**
    * @return message code (unique for the given group)
    */
   public String getCode() {
	return code;
   }

   /**
    * @return group (define by the user functional ) of the message
    */
   public I18nMessageGroup getGroup() {
	return group;
   }

   /**
    * @return user description
    */
   public String getDescription() {
	return description;
   }

   /**
    * @return the type of the message
    */
   public I18nMessageType getType() {
	return type;
   }

   /**
    * @return set of translated message
    */
   public Set<I18nMessageLanguage> getMessageLanguages() {
	return messageLanguages;
   }

   /**
    * @return date of the last usage
    */
   public Date getLastUsed() {
	return lastUsed;
   }

   /**
    * @return <code>true</code> if message is active, <code>false</code> otherwise
    */
   public boolean isEnabled() {
	return enabled;
   }

   public void setCode(final String code) {
	this.code = code;
   }

   public void setGroup(final I18nMessageGroup group) {
	this.group = group;
   }

   public void setDescription(final String description) {
	this.description = description;
   }

   public void setType(final I18nMessageType type) {
	this.type = type;
   }

   public void setMessageLanguages(final Set<I18nMessageLanguage> messageLanguages) {
	this.messageLanguages = messageLanguages;
   }

   public void setLastUsed(final Date lastUsed) {
	this.lastUsed = lastUsed;
   }

   public void setEnabled(final boolean enabled) {
	this.enabled = enabled;
   }

   // EQUALS / HASHCODE / TOSTRING / COMPARE / CLONE... FOR POJO ******************************************************

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (code == null ? 0 : code.hashCode());
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (enabled ? 1231 : 1237);
	result = prime * result + (group == null ? 0 : group.hashCode());
	result = prime * result + (id == null ? 0 : id.hashCode());
	result = prime * result + (lastUsed == null ? 0 : lastUsed.hashCode());
	result = prime * result + (type == null ? 0 : type.hashCode());
	return result;
   }

   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof I18nMessage)) { return false; }
	I18nMessage other = (I18nMessage) obj;
	if (code == null) {
	   if (other.code != null) { return false; }
	} else if (!code.equals(other.code)) { return false; }
	if (description == null) {
	   if (other.description != null) { return false; }
	} else if (!description.equals(other.description)) { return false; }
	if (enabled != other.enabled) { return false; }
	if (group == null) {
	   if (other.group != null) { return false; }
	} else if (!group.equals(other.group)) { return false; }
	if (id == null) {
	   if (other.id != null) { return false; }
	} else if (!id.equals(other.id)) { return false; }
	if (lastUsed == null) {
	   if (other.lastUsed != null) { return false; }
	} else if (!lastUsed.equals(other.lastUsed)) { return false; }
	if (type == null) {
	   if (other.type != null) { return false; }
	} else if (!type.equals(other.type)) { return false; }
	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return "I18nMessage [id=" + id + ",\tcode=" + code + ",\tgroup=" + (group != null ? group.getCode() : "null") + ",\tenabled=" + enabled + ",\tlastUsed="
		+ lastUsed + ",\ttype=" + type + ",\tdescription=" + description + "]";
   }

}