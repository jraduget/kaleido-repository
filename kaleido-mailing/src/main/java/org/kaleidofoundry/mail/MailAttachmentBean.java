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
import java.net.URL;

/**
 * Impl�m�ntation MailAttachment
 * 
 * @author jraduget
 */
public class MailAttachmentBean implements MailAttachment {

   private static final long serialVersionUID = 6032673070918414344L;

   private String name;
   private String contentType;
   private InputStream content;
   private String contentPath;
   private URL contentURL;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContent()
    */
   public InputStream getContent() {
	return content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentType()
    */
   public String getContentType() {
	return contentType;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getName()
    */
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContent(java.io.InputStream)
    */
   public void setContent(final InputStream in) {
	this.content = in;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentType(java.lang.String)
    */
   public void setContentType(final String contentType) {
	this.contentType = contentType;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setName(java.lang.String)
    */
   public void setName(final String name) {
	this.name = name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentPath()
    */
   public String getContentPath() {
	return contentPath;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentPath(java.lang.String)
    */
   public void setContentPath(final String contentPath) {
	this.contentPath = contentPath;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentURL()
    */
   public URL getContentURL() {
	return contentURL;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentURL(java.net.URL)
    */
   public void setContentURL(final URL contentURL) {
	this.contentURL = contentURL;
   }

}
