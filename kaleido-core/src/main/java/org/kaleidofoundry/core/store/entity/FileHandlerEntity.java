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
package org.kaleidofoundry.core.store.entity;

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

import org.kaleidofoundry.core.lang.annotation.Review;

/**
 * Default entity to store a file content in a blob / clob ...<br/>
 * You can extend it and override default mapping on getter / setter, to custom it to your need, otherwise you can have your own
 * persistent.xml
 * 
 * @author Jerome RADUGET
 */
@Entity
@Review(comment = "Audit information (locale zone for the date, user information...)")
public class FileHandlerEntity implements Serializable {

   private static final long serialVersionUID = 6158960255569565876L;

   @Id
   private String uri;
   private String name;
   private String path;
   private Integer contentSize;
   @Lob
   @Basic(fetch = FetchType.LAZY)
   private byte[] content;
   @Column(insertable = true, updatable = false, nullable = false)
   @Temporal(TemporalType.TIMESTAMP)
   private Date creationDate;
   @Temporal(TemporalType.TIMESTAMP)
   @Column(insertable = false, updatable = true, nullable = true)
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

   public byte[] getContent() {
	return content;
   }

   /**
    * @return entity creation date
    */
   public Date getCreationDate() {
	return creationDate;
   }

   /**
    * @return last entity update date
    */

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
	if (!(obj instanceof FileHandlerEntity)) { return false; }
	final FileHandlerEntity other = (FileHandlerEntity) obj;
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
