/*  
 * Copyright 2008-2014 the original author or authors 
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
   private String contentURI;
   private InputStream contentInputstream;

   public MailAttachmentBean() {
   }

   /**
    * @param name
    * @param contentType
    * @param contentURI
    * @param contentInputstream
    */
   public MailAttachmentBean(String name, String contentType, String contentURI, InputStream contentInputstream) {
	super();
	this.name = name;
	this.contentType = contentType;
	this.contentURI = contentURI;
	this.contentInputstream = contentInputstream;
   }

   public String getContentType() {
	return contentType;
   }

   public String getName() {
	return name;
   }

   public MailAttachmentBean withContentType(final String contentType) {
	this.contentType = contentType;
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

   public InputStream getContentInputStream() {
	return contentInputstream;
   }

   public MailAttachmentBean withContentIntputStream(InputStream contentIn) {
	this.contentInputstream = contentIn;
	return this;
   }

}
