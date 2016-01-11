/*  
 * Copyright 2008-2016 the original author or authors 
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
package org.kaleidofoundry.mail;

import java.io.InputStream;

/**
 * MailAttachment bean
 * 
 * @author jraduget
 */
public class MailAttachmentBean implements MailAttachment {

   private static final long serialVersionUID = 6032673070918414344L;

   private String name;
   private String contentType;
   private String contentCharset;
   private String contentURI;
   private InputStream contentInputstream;

   public MailAttachmentBean() {
   }

   /**
    * @param name
    * @param contentInputstream
    * @param contentURI
    * @param contentType
    * @param contentInputstream
    */
   public MailAttachmentBean(String name, InputStream contentInputstream, String contentURI, String contentType, String contentCharset) {
	super();
	this.name = name;
	this.contentType = contentType;
	this.contentCharset = contentCharset;
	this.contentInputstream = contentInputstream;
	this.contentURI = contentURI;
   }

   public String getName() {
	return name;
   }

   public String getContentType() {
	return contentType;
   }

   public MailAttachmentBean withContentType(final String contentType) {
	this.contentType = contentType;
	return this;
   }

   public String getContentCharset() {
	return contentCharset;
   }

   public MailAttachmentBean withContentCharset(final String contentCharset) {
	this.contentCharset = contentCharset;
	return this;
   }

   public MailAttachmentBean withName(final String name) {
	this.name = name;
	return this;
   }

   public String getContentURI() {
	return contentURI;
   }

   public MailAttachmentBean withContentURI(final String contentURI) {
	this.contentURI = contentURI;
	return this;
   }

   public InputStream getInputStream() {
	return contentInputstream;
   }

   public MailAttachmentBean withContentIntputStream(InputStream contentIn) {
	this.contentInputstream = contentIn;
	return this;
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((contentCharset == null) ? 0 : contentCharset.hashCode());
	result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
	result = prime * result + ((contentURI == null) ? 0 : contentURI.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
   }

   @Override
   public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	MailAttachmentBean other = (MailAttachmentBean) obj;
	if (contentCharset == null) {
	   if (other.contentCharset != null) return false;
	} else if (!contentCharset.equals(other.contentCharset)) return false;
	if (contentType == null) {
	   if (other.contentType != null) return false;
	} else if (!contentType.equals(other.contentType)) return false;
	if (contentURI == null) {
	   if (other.contentURI != null) return false;
	} else if (!contentURI.equals(other.contentURI)) return false;
	if (name == null) {
	   if (other.name != null) return false;
	} else if (!name.equals(other.name)) return false;
	return true;
   }

}
