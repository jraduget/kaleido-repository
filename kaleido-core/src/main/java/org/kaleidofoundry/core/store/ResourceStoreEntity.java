package org.kaleidofoundry.core.store;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Default ResourceStore entity to store a resource in a blob / clob ...<br/>
 * You can extend it and override default mapping on getter / setter, to custom it to your need, otherwise you can have your own
 * persistent.xml
 * 
 * @author Jerome RADUGET
 */
@Entity
public class ResourceStoreEntity implements Serializable {

   private static final long serialVersionUID = 6158960255569565876L;

   private String uri;
   private String name;
   private String path;
   private Integer contentSize;
   private byte[] content;
   private Date creationDate;
   private Date updatedDate;

   @PreUpdate
   protected void preUpdate() {
	if (updatedDate == null) {
	   updatedDate = GregorianCalendar.getInstance().getTime();
	   contentSize = content != null ? content.length : 0;
	}
   }

   @PrePersist
   protected void preCreation() {
	if (creationDate == null) {
	   creationDate = GregorianCalendar.getInstance().getTime();
	   contentSize = content != null ? content.length : 0;
	}
   }

   /**
    * @return identifier of the entity
    */
   @Id
   public String getUri() {
	return uri;
   }

   /**
    * @return name of the resource
    */
   public String getName() {
	return name;
   }

   /**
    * @return size in byte of the data
    */
   public Integer getContentSize() {
	return contentSize;
   }

   /**
    * @return binary content
    */
   @Lob
   @Basic(fetch = FetchType.LAZY)
   public byte[] getContent() {
	return content;
   }

   /**
    * @return entity creation date
    */
   @Column(insertable = true, updatable = false, nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   public Date getCreationDate() {
	return creationDate;
   }

   /**
    * @return last entity update date
    */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(insertable = false, updatable = true, nullable = true)
   public Date getUpdatedDate() {
	return updatedDate;
   }

   /**
    * @param name
    */
   public void setName(final String name) {
	this.name = name;
   }

   /**
    * @param uri
    */
   public void setUri(final String uri) {
	this.uri = uri;
   }

   /**
    * @param path
    */
   public void setPath(final String path) {
	this.path = path;
   }

   /**
    * @return path of the resource
    */
   public String getPath() {
	return path;
   }

   /**
    * @param size
    */
   void setContentSize(final Integer size) {
	contentSize = size;
   }

   /**
    * @param content
    */
   public void setContent(final byte[] content) {
	this.content = content;
	contentSize = content != null ? content.length : 0;
   }

   /**
    * @param creationDate
    */
   public void setCreationDate(final Date creationDate) {
	this.creationDate = creationDate;
   }

   /**
    * @param updatedDate
    */
   public void setUpdatedDate(final Date updatedDate) {
	this.updatedDate = updatedDate;
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(content);
	result = prime * result + (creationDate == null ? 0 : creationDate.hashCode());
	result = prime * result + (uri == null ? 0 : uri.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (path == null ? 0 : path.hashCode());
	result = prime * result + (contentSize == null ? 0 : contentSize.hashCode());
	result = prime * result + (updatedDate == null ? 0 : updatedDate.hashCode());
	return result;
   }

   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof ResourceStoreEntity)) { return false; }
	ResourceStoreEntity other = (ResourceStoreEntity) obj;
	if (!Arrays.equals(content, other.content)) { return false; }
	if (creationDate == null) {
	   if (other.creationDate != null) { return false; }
	} else if (!creationDate.equals(other.creationDate)) { return false; }
	if (uri == null) {
	   if (other.uri != null) { return false; }
	} else if (!uri.equals(other.uri)) { return false; }
	if (name == null) {
	   if (other.name != null) { return false; }
	} else if (!name.equals(other.name)) { return false; }
	if (path == null) {
	   if (other.path != null) { return false; }
	} else if (!path.equals(other.path)) { return false; }
	if (contentSize == null) {
	   if (other.contentSize != null) { return false; }
	} else if (!contentSize.equals(other.contentSize)) { return false; }
	if (updatedDate == null) {
	   if (other.updatedDate != null) { return false; }
	} else if (!updatedDate.equals(other.updatedDate)) { return false; }
	return true;
   }

}
