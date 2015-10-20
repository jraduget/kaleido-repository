/*
 * Copyright 2008-2013 the original author or authors
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
package org.kaleidofoundry.core.store;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.HeaderTokenizer;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Expose a {@link HttpServletRequest} as a {@link FileStore}, in order to provide user-friendly accessor to get the uploaded attachments
 * 
 * @author jraduget
 */
public class UploadRequestFileStore extends AbstractFileStore {

   public static final String MULTIPART_DATA = "multipart/form-data";

   private final Map<String, ResourceHandler> uploadedResourceByName;

   /**
    * @param request
    */
   public UploadRequestFileStore(final HttpServletRequest request) {
	// unnamed context will not be register in the file store registry
	this(request, new FileStoreContextBuilder().withBaseUri(FileStoreTypeEnum.request.name() + ":/").build());
   }

   /**
    * @param request
    * @param context
    */
   public UploadRequestFileStore(final HttpServletRequest request, @NotNull RuntimeContext<FileStore> context) {
	super(context);
	try {
	   uploadedResourceByName = new HashMap<String, ResourceHandler>();
	   buildUploadedResources(request);
	} catch (Exception e) {
	   throw new IllegalStateException("Error creating servlet request FileStore", e);
	}
   }

   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.request };
   }

   @Override
   protected ResourceHandler doGet(@NotNull URI resourceUri) throws ResourceException {
	return uploadedResourceByName.get(resourceUri.getPath().substring(1));
   }

   @Override
   protected void doRemove(@NotNull URI resourceUri) throws ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

   @Override
   protected void doStore(@NotNull URI resourceUri, @NotNull ResourceHandler resource) throws ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

   /**
    * @param request the request from which we extract the uploaded files
    * @return uploaded file content by field name
    * @throws java.io.IOException
    * @throws javax.mail.MessagingException
    */
   private void buildUploadedResources(final ServletRequest request) throws IOException, MessagingException {

	InputStream inRequest;
	MimeMultipart mimeContent;

	String contentType = request.getContentType();
	// int contentLength = request.getContentLength();

	if (containsMultipartContent(request)) {

	   inRequest = request.getInputStream();
	   mimeContent = new MimeMultipart(new UploadRequestDataSource(contentType, inRequest));

	   for (int i = 0; i < mimeContent.getCount(); i++) {

		BodyPart part = mimeContent.getBodyPart(i);
		String headerValues[] = part != null ? part.getHeader("Content-Disposition") : null;

		if (headerValues != null) {

		   String pName = null;
		   HeaderTokenizer hdrTzr = new HeaderTokenizer(headerValues[0]);

		   for (HeaderTokenizer.Token token; (token = hdrTzr.next()).getType() != -4;) {
			if ("name=".equalsIgnoreCase(token.getValue())) {
			   pName = hdrTzr.peek().getValue();
			   break;
			}
		   }

		   // named upload field, having data
		   if (pName != null && part.getSize() > 0 && part.getFileName() != null) {
			ResourceHandler resource = createResourceHandler(pName, part.getInputStream());
			if (resource instanceof ResourceHandlerBean) {
			   ((ResourceHandlerBean) resource).setCharset(request.getCharacterEncoding()); // TODO part.getHeader("Accept-Charset")
			   ((ResourceHandlerBean) resource).setMimeType(part.getContentType());
			   ((ResourceHandlerBean) resource).setLength(request.getContentLength());			   
			   ((ResourceHandlerBean) resource).setLastModified(new GregorianCalendar(LocaleFactory.getDefaultFactory().getCurrentLocale())
				   .getTimeInMillis());
			}
			uploadedResourceByName.put(pName, resource);
		   }
		}
	   }
	}

   }

   /**
    * @return set of uri of the uploaded items
    */
   public Set<String> resourceUriSet() {
	return uploadedResourceByName.keySet();
   }

   /**
    * @return uploaded resources by name
    */
   public Map<String, ResourceHandler> getResourcesByName() {
	return Collections.unmodifiableMap(uploadedResourceByName);
   }

   /**
    * @param request the request to analyse
    * @return do the request contains a multipart content
    */
   public static boolean containsMultipartContent(final ServletRequest request) {
	String contentType = request.getContentType();
	return contentType != null && contentType.toLowerCase().startsWith(MULTIPART_DATA);
   }
}
