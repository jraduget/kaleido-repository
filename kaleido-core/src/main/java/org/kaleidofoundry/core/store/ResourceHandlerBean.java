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

import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Charset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.Charsets;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Default {@link ResourceHandler} implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
@NotThreadSafe
class ResourceHandlerBean implements ResourceHandler {

   private final static Charsets DEFAULT_CHARSET = Charsets.UTF_8;

   private final AbstractFileStore store;
   private final String resourceUri;
   private final InputStream input;

   private boolean closed;

   /**
    * @param store
    * @param resourceUri
    * @param input
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   ResourceHandlerBean(final AbstractFileStore store, final String resourceUri, final String input) throws UnsupportedEncodingException {
	this(store, resourceUri, new ByteArrayInputStream(input.getBytes(store.context.getString(Charset, DEFAULT_CHARSET.getCode()))));
   }

   /**
    * @param store
    * @param resourceUri
    * @param input
    */
   ResourceHandlerBean(final AbstractFileStore store, final String resourceUri, final InputStream input) {
	this.store = store;
	this.resourceUri = resourceUri;
	this.input = input;
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
   public InputStream getInputStream() {
	return input;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getBytes()
    */
   @Override
   public byte[] getBytes() throws ResourceException {
	try {
	   return IOHelper.toByteArray(getInputStream());
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri);
	} finally {
	   // free resource handler
	   close();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader()
    */
   @Override
   public Reader getReader() throws ResourceException {
	return getReader(store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader(java.lang.String)
    */
   @Override
   public Reader getReader(final String charset) throws ResourceException {
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
   public String getText() throws ResourceException {
	return getText(store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getText(java.lang.String)
    */
   @Override
   public String getText(final String charset) throws ResourceException {
	BufferedReader reader = null;
	String inputLine;

	try {
	   final StringBuilder stb = new StringBuilder();
	   reader = new BufferedReader(getReader(charset));
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
		throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.inputstream.close.error", ioe.getMessage(), ioe), ioe);
	   }

	   // free resource handler
	   close();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#release()
    */
   @Override
   public void close() {
	if (input != null && !closed) {
	   try {
		input.close();
		closed = true;
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.inputstream.close.error", ioe.getMessage(), ioe), ioe);
	   }
	}
   }

}
