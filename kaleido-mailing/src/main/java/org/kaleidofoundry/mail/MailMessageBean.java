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
package org.kaleidofoundry.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolver;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;

/**
 * MailMessage implementation
 * 
 * @author Jerome RADUGET
 */
public class MailMessageBean implements MailMessage {

   private static final long serialVersionUID = -2884511473461328082L;

   protected final static String TEXT_CONTENT_TYPE = "text/plain";
   protected final static String HTML_CONTENT_TYPE = "text/html";
   protected final static String DEFAULT_CONTENT_TYPE = TEXT_CONTENT_TYPE;

   protected final static String DEFAULT_CHARSET = "iso-8859-1";

   private String subject;
   private String content;
   private String fromAdress;

   private ArrayList<String> toAdress;
   private ArrayList<String> ccAdress;
   private ArrayList<String> cciAdress;

   private int priority;

   private final Map<String, MailAttachment> attachments;
   private String contentType;
   private String charset;

   public MailMessageBean() {
	attachments = new HashMap<String, MailAttachment>();
	toAdress = new ArrayList<String>();
	charset = DEFAULT_CHARSET;
	contentType = TEXT_CONTENT_TYPE;
	priority = 0;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#addAttachment(java.lang.String, java.lang.String)
    */
   public void addAttachment(final String attachName, final String filename) throws FileNotFoundException, IOException {
	MimeTypeResolver mimesSrv = MimeTypeResolverFactory.getService();
	MailAttachment attach = null;
	String fileExt = null;

	fileExt = FileHelper.getFileNameExtension(filename);
	if (fileExt == null) { throw new IOException("Filename " + filename + " have an unknown mime type"); }

	attach = new MailAttachmentBean();
	attach.setName(attachName);
	attach.setContentPath(filename);
	attach.setContentType(mimesSrv.getMimeType(fileExt));

	attachments.put(attachName, attach);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#addAttachment(java.lang.String, java.net.URL)
    */
   public void addAttachment(final String attachName, final URL fileURL) throws IOException {
	MailAttachment attach = null;
	MimeTypeResolver mimesSrv = MimeTypeResolverFactory.getService();
	String fileExt = null;

	fileExt = FileHelper.getFileNameExtension(fileURL.toExternalForm());
	if (fileExt == null) { throw new IOException("Filename " + fileURL.toExternalForm() + " have an unknown mime type"); }

	attach = new MailAttachmentBean();
	attach.setName(attachName);
	attach.setContentURL(fileURL);
	attach.setContentType(mimesSrv.getMimeType(fileExt));

	attachments.put(attachName, attach);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getContent()
    */
   public String getContent() {
	return content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getCcAdress()
    */
   public List<String> getCcAdress() {
	if (ccAdress == null) {
	   ccAdress = new ArrayList<String>();
	}
	return ccAdress;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getCciAdress()
    */
   public List<String> getCciAdress() {
	if (cciAdress == null) {
	   cciAdress = new ArrayList<String>();
	}
	return cciAdress;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getFromAdress()
    */
   public String getFromAdress() {
	return fromAdress;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getPriority()
    */
   public int getPriority() {
	return priority;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getSubject()
    */
   public String getSubject() {
	return subject;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getToAdress()
    */
   public List<String> getToAdress() {
	return toAdress;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setBodyContentHtml()
    */
   public void setBodyContentHtml() {
	contentType = HTML_CONTENT_TYPE;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setBodyContentText()
    */
   public void setBodyContentText() {
	contentType = TEXT_CONTENT_TYPE;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setCharSet(java.lang.String)
    */
   public void setCharSet(final String charset) {
	this.charset = charset;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getCharSet()
    */
   public String getCharSet() {
	return charset != null ? charset : DEFAULT_CHARSET;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getContentType()
    */
   public String getContentType() {
	return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
   }

   protected void setContentType(final String contentType) {
	this.contentType = contentType;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getAttachmentFilename(java.lang.String)
    */
   public MailAttachment getAttachmentFilename(final String attachName) {
	return attachments.get(attachName);
   }

   public void addAttachment(final MailAttachment attach) {
	if (attach != null && attach.getName() != null) {
	   attachments.put(attach.getName(), attach);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#getAttachments()
    */
   public Set<String> getAttachments() {
	return attachments.keySet();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setFromAdress(java.lang.String)
    */
   public void setFromAdress(final String fromAdress) {
	this.fromAdress = fromAdress;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setContent(java.lang.String)
    */
   public void setContent(final String content) {
	this.content = content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setSubject(java.lang.String)
    */
   public void setSubject(final String subject) {
	this.subject = subject;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailMessage#setPriority(int)
    */
   public void setPriority(final int priority) {
	this.priority = priority;
   }

   protected void setCcAdress(final ArrayList<String> ccAdress) {
	this.ccAdress = ccAdress;
   }

   protected void setCciAdress(final ArrayList<String> cciAdress) {
	this.cciAdress = cciAdress;
   }

   protected void setToAdress(final ArrayList<String> toAdress) {
	this.toAdress = toAdress;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	final StringBuffer str = new StringBuffer();

	str.append("Subject=").append(getSubject()).append("\n");
	str.append("FROM=").append(getFromAdress()).append("\n");
	str.append("TO=").append(getToAdress().toString()).append("\n");
	str.append("CC=").append(getCcAdress().toString()).append("\n");
	str.append("CCI=").append(getCciAdress().toString()).append("\n");
	str.append("Content=").append(getContent()).append("\n");

	return super.toString();
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   @Override
   @SuppressWarnings("unchecked")
   public MailMessage clone() {
	try {
	   final MailMessageBean message = (MailMessageBean) super.clone();
	   if (toAdress != null) {
		message.setToAdress((ArrayList<String>) toAdress.clone());
	   }
	   if (ccAdress != null) {
		message.setCcAdress((ArrayList<String>) ccAdress.clone());
	   }
	   if (cciAdress != null) {
		message.setCciAdress((ArrayList<String>) cciAdress.clone());
	   }
	   return message;
	} catch (final CloneNotSupportedException cne) {
	   throw new IllegalStateException(cne);
	}
   }

}
