/*  
 * Copyright 2008-2021 the original author or authors 
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolver;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.Charsets;
import org.kaleidofoundry.core.store.FileStoreProvider;
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
	if (attach.getInputStream() == null) { throw new IllegalArgumentException("Mail attachment inpustream is null"); }
	attachmentsByName.put(attach.getName(), attach);
	return this;
   }

   @Override
   public MailMessage attach(String name, ResourceHandler attach) throws ResourceException {
	attach(new MailAttachmentBean(name, attach.getInputStream(), attach.getUri(), attach.getMimeType(), attach.getCharset()));
	return this;
   }

   @Override
   public MailMessage attach(final String attachName, final String fileURI, final String charset) throws FileNotFoundException, IOException {
	String mergedFileURI = FileStoreProvider.buildFullResourceURi(fileURI);
	MimeTypeResolver mimesSrv = MimeTypeResolverFactory.getService();
	String fileExt = FileHelper.getFileNameExtension(mergedFileURI);

	MailAttachmentBean attach = new MailAttachmentBean(attachName, new FileInputStream(mergedFileURI), mergedFileURI,
		fileExt != null ? mimesSrv.getMimeType(fileExt) : null, !StringHelper.isEmpty(charset) ? charset : null);
	return attach(attach);
   }

   @Override
   public MailMessage attach(final String attachName, final String fileURI) throws FileNotFoundException, IOException {
	return attach(attachName, fileURI, null);
   }

   @Override
   public MailMessage attach(String attachName, InputStream attachIn, String mimeType, String charset) {
	return attach(new MailAttachmentBean(attachName, attachIn, null, mimeType, charset));
   }

   @Override
   public MailMessage attach(String attachName, InputStream attachIn, String mimeType) {
	return attach(attachName, attachIn, mimeType, null);
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
   public List<String> getBccAddresses() {
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
   public MailMessage withBodyAs(boolean html) {
	bodyContentType = html ? HTML_CONTENT_TYPE : TEXT_CONTENT_TYPE;
	return this;
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

   @Override
   public MailMessage withToAddresses(String... addresses) {
	this.toAddresses = new ArrayList<String>(Arrays.asList(addresses));
	return this;
   }

   @Override
   public MailMessage withCcAddresses(String... addresses) {
	this.ccAddresses = new ArrayList<String>(Arrays.asList(addresses));
	return this;
   }

   @Override
   public MailMessage withBccAddresses(String... addresses) {
	this.cciAddresses = new ArrayList<String>(Arrays.asList(addresses));
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
	str.append("CCI=").append(getBccAddresses().toString()).append("\n");
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

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((attachmentsByName == null) ? 0 : attachmentsByName.hashCode());
	result = prime * result + ((body == null) ? 0 : body.hashCode());
	result = prime * result + ((bodyCharset == null) ? 0 : bodyCharset.hashCode());
	result = prime * result + ((bodyContentType == null) ? 0 : bodyContentType.hashCode());
	result = prime * result + ((ccAddresses == null) ? 0 : ccAddresses.hashCode());
	result = prime * result + ((cciAddresses == null) ? 0 : cciAddresses.hashCode());
	result = prime * result + ((fromAddress == null) ? 0 : fromAddress.hashCode());
	result = prime * result + priority;
	result = prime * result + ((subject == null) ? 0 : subject.hashCode());
	result = prime * result + ((toAddresses == null) ? 0 : toAddresses.hashCode());
	return result;
   }

   @Override
   public boolean equals(Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof MailMessageBean)) { return false; }
	MailMessageBean other = (MailMessageBean) obj;
	if (attachmentsByName == null) {
	   if (other.attachmentsByName != null) { return false; }
	} else if (!attachmentsByName.equals(other.attachmentsByName)) { return false; }
	if (body == null) {
	   if (other.body != null) { return false; }
	} else if (!body.equals(other.body)) { return false; }
	if (bodyCharset == null) {
	   if (other.bodyCharset != null) { return false; }
	} else if (!bodyCharset.equals(other.bodyCharset)) { return false; }
	if (bodyContentType == null) {
	   if (other.bodyContentType != null) { return false; }
	} else if (!bodyContentType.equals(other.bodyContentType)) { return false; }
	if (ccAddresses == null) {
	   if (other.ccAddresses != null) { return false; }
	} else if (!ccAddresses.equals(other.ccAddresses)) { return false; }
	if (cciAddresses == null) {
	   if (other.cciAddresses != null) { return false; }
	} else if (!cciAddresses.equals(other.cciAddresses)) { return false; }
	if (fromAddress == null) {
	   if (other.fromAddress != null) { return false; }
	} else if (!fromAddress.equals(other.fromAddress)) { return false; }
	if (priority != other.priority) { return false; }
	if (subject == null) {
	   if (other.subject != null) { return false; }
	} else if (!subject.equals(other.subject)) { return false; }
	if (toAddresses == null) {
	   if (other.toAddresses != null) { return false; }
	} else if (!toAddresses.equals(other.toAddresses)) { return false; }
	return true;
   }

}
