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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolver;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.Charsets;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * MailMessage implementation
 * 
 * @author jraduget
 */
public class MailMessageBean implements MailMessage {

   private static final long serialVersionUID = -2884511473461328082L;

   protected final static String TEXT_CONTENT_TYPE = "text/plain";
   protected final static String HTML_CONTENT_TYPE = "text/html";
   protected final static String DEFAULT_CONTENT_TYPE = HTML_CONTENT_TYPE;

   protected final static String DEFAULT_CHARSET = Charsets.UTF_8.getCode();

   private String subject;
   private String body;
   private String fromAddress;

   private ArrayList<String> toAddresses;
   private ArrayList<String> ccAddresses;
   private ArrayList<String> cciAddresses;

   private int priority;

   private final Map<String, MailAttachment> attachmentsByName;
   private String bodyContentType;
   private String bodyCharset;

   public MailMessageBean() {
	attachmentsByName = new HashMap<String, MailAttachment>();
	toAddresses = new ArrayList<String>();
	bodyCharset = DEFAULT_CHARSET;
	bodyContentType = DEFAULT_CONTENT_TYPE;
	priority = 0;
   }

   @Override
   public MailMessage attach(final MailAttachment attach) {
	if (attach == null) { throw new IllegalArgumentException("Mail attachment is null"); }
	if (StringHelper.isEmpty(attach.getName())) { throw new IllegalArgumentException("Mail attachment name is mandatory"); }
	if (attach.getContentInputStream() == null) { throw new IllegalArgumentException("Mail attachment inpustream is null"); }
	attachmentsByName.put(attach.getName(), attach);
	return this;
   }

   @Override
   public MailMessage attach(String name, ResourceHandler attach) throws ResourceException {
	attach(new MailAttachmentBean(name, attach.getMimeType(), attach.getUri(), attach.getInputStream()));
	return this;
   }

   @Override
   public MailMessage attach(final String attachName, final String fileURI) throws FileNotFoundException, IOException {
	MimeTypeResolver mimesSrv = MimeTypeResolverFactory.getService();
	String fileExt = FileHelper.getFileNameExtension(fileURI);
	MailAttachmentBean attach = new MailAttachmentBean(attachName, (fileExt != null ? mimesSrv.getMimeType(fileExt) : null), fileURI, new FileInputStream(
		fileURI));
	return attach(attach);
   }

   @Override
   public MailMessage attach(String attachName, String mimeType, InputStream attachIn) {
	return attach(new MailAttachmentBean(attachName, mimeType, null, attachIn));
   }

   @Override
   public String getBody() {
	return body;
   }

   @Override
   public List<String> getCcAddresses() {
	if (ccAddresses == null) {
	   ccAddresses = new ArrayList<String>();
	}
	return ccAddresses;
   }

   @Override
   public List<String> getCciAddresses() {
	if (cciAddresses == null) {
	   cciAddresses = new ArrayList<String>();
	}
	return cciAddresses;
   }

   @Override
   public String getFromAddress() {
	return fromAddress;
   }

   @Override
   public int getPriority() {
	return priority;
   }

   @Override
   public String getSubject() {
	return subject;
   }

   @Override
   public List<String> getToAddresses() {
	return toAddresses;
   }

   @Override
   public MailMessage withBodyAsHtml() {
	bodyContentType = HTML_CONTENT_TYPE;
	return this;
   }

   @Override
   public MailMessage withBodyAsText() {
	bodyContentType = TEXT_CONTENT_TYPE;
	return this;
   }

   @Override
   public MailMessage withBodyCharSet(final String charset) {
	this.bodyCharset = charset;
	return this;
   }

   @Override
   public String getBodyCharSet() {
	return bodyCharset != null ? bodyCharset : DEFAULT_CHARSET;
   }

   @Override
   public String getBodyContentType() {
	return bodyContentType != null ? bodyContentType : DEFAULT_CONTENT_TYPE;
   }

   protected void setContentType(final String contentType) {
	this.bodyContentType = contentType;
   }

   @Override
   public MailAttachment getAttachment(final String attachName) {
	return attachmentsByName.get(attachName);
   }

   @Override
   public Set<String> getAttachmentNames() {
	return attachmentsByName.keySet();
   }

   @Override
   public MailMessage withFromAddress(final String fromAdress) {
	this.fromAddress = fromAdress;
	return this;
   }

   @Override
   public MailMessage withBody(final String content) {
	this.body = content;
	return this;
   }

   @Override
   public MailMessage withSubject(final String subject) {
	this.subject = subject;
	return this;
   }

   @Override
   public MailMessage withPriority(final int priority) {
	this.priority = priority;
	return this;
   }

   protected void setCcAdress(final ArrayList<String> ccAdress) {
	this.ccAddresses = ccAdress;
   }

   protected void setCciAdress(final ArrayList<String> cciAdress) {
	this.cciAddresses = cciAdress;
   }

   protected void setToAdress(final ArrayList<String> toAdress) {
	this.toAddresses = toAdress;
   }

   @Override
   public String toString() {
	final StringBuffer str = new StringBuffer();

	str.append("Subject=").append(getSubject()).append("\n");
	str.append("FROM=").append(getFromAddress()).append("\n");
	str.append("TO=").append(getToAddresses().toString()).append("\n");
	str.append("CC=").append(getCcAddresses().toString()).append("\n");
	str.append("CCI=").append(getCciAddresses().toString()).append("\n");
	str.append("Body=").append(getBody()).append("\n");

	return super.toString();
   }

   @Override
   @SuppressWarnings("unchecked")
   public MailMessage clone() {
	try {
	   final MailMessageBean message = (MailMessageBean) super.clone();
	   if (toAddresses != null) {
		message.setToAdress((ArrayList<String>) toAddresses.clone());
	   }
	   if (ccAddresses != null) {
		message.setCcAdress((ArrayList<String>) ccAddresses.clone());
	   }
	   if (cciAddresses != null) {
		message.setCciAdress((ArrayList<String>) cciAddresses.clone());
	   }
	   return message;
	} catch (final CloneNotSupportedException cne) {
	   throw new IllegalStateException(cne);
	}
   }

}
