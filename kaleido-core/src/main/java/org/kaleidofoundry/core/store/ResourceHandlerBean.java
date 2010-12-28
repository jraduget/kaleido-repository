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
package org.kaleidofoundry.core.store;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Default {@link ResourceHandler} implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
@NotThreadSafe
public class ResourceHandlerBean implements ResourceHandler {

   private final String resourceUri;
   private final InputStream input;

   private boolean closed;

   /**
    * @param resourceUri
    * @param input
    */
   public ResourceHandlerBean(@NotNull final String resourceUri, @NotNull final InputStream input) {
	this.input = input;
	this.resourceUri = resourceUri;
	this.closed = false;
   }

   /**
    * @param resourceUri
    * @param input default encoding is UTF-8
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    * @see Charset
    */
   public ResourceHandlerBean(@NotNull final String resourceUri, @NotNull final String input) throws UnsupportedEncodingException {
	this(resourceUri, input, "UTF-8");
   }

   /**
    * @param resourceUri
    * @param input
    * @param charset
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   public ResourceHandlerBean(@NotNull final String resourceUri, @NotNull final String input, @NotNull final String charset)
	   throws UnsupportedEncodingException {
	this.input = new ByteArrayInputStream(input.getBytes(charset));
	this.resourceUri = resourceUri;
	this.closed = false;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getResourceUri()
    */
   @Override
   public String getResourceUri() {
	return resourceUri;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStream()
    */
   @Override
   @NotNull
   public InputStream getInputStream() {
	return input;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getBytes()
    */
   @Override
   @NotNull
   public byte[] getBytes() throws ResourceException {
	try {
	   return IOHelper.toByteArray(getInputStream());
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri);
	} finally {
	   // free resource handler
	   release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader()
    */
   @Override
   @NotNull
   public InputStreamReader getInputStreamReader() throws ResourceException {
	return getInputStreamReader("UTF-8");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader(java.lang.String)
    */
   @Override
   @NotNull
   public InputStreamReader getInputStreamReader(final String charset) throws ResourceException {
	try {
	   return new InputStreamReader(getInputStream(), charset);
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getText()
    */
   @Override
   @NotNull
   public String getText() throws ResourceException {
	return getText("UTF-8");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getText(java.lang.String)
    */
   @Override
   @NotNull
   public String getText(final String charset) throws ResourceException {
	BufferedReader reader = null;
	String inputLine;

	try {
	   final StringBuilder stb = new StringBuilder();
	   reader = new BufferedReader(getInputStreamReader(charset));
	   while ((inputLine = reader.readLine()) != null) {
		stb.append(inputLine.trim()).append("\n");
	   }
	   if (stb.length() <= 0) {
		return "";
	   } else {
		return stb.toString().substring(0, stb.length() - 1);
	   }

	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri);
	} finally {
	   // free buffered reader
	   try {
		if (reader != null) {
		   reader.close();
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.ResourceStoreMessageBundle.getMessage("store.resource.inputstream.error", ioe.getMessage(),
			ioe), ioe);
	   }

	   // free resource handler
	   release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#release()
    */
   @Override
   public void release() {
	if (input != null && !closed) {
	   try {
		input.close();
		closed = true;
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.ResourceStoreMessageBundle.getMessage("store.resource.inputstream.error", ioe.getMessage(),
			ioe), ioe);
	   }
	}
   }

}
