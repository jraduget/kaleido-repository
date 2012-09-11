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

import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_CHARSET;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Charset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.persistence.Transient;

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
class ResourceHandlerBean implements ResourceHandler, Serializable {

   private static final long serialVersionUID = 1L;

   @Transient
   private final transient AbstractFileStore store;

   private final String resourceUri;
   private long lastModified;
   private String mimeType;

   private boolean closed;

   private final byte[] bytes;
   private final String text;

   @Transient
   private transient InputStream input;
   @Transient
   private transient Reader reader;

   /**
    * @param store
    * @param resourceUri
    * @param datas
    */
   ResourceHandlerBean(final AbstractFileStore store, final String resourceUri, @NotNull final byte[] content) {
	this.store = store;
	this.resourceUri = resourceUri;
	input = null;
	text = null;
	reader = null;
	bytes = content;
	closed = false;
	lastModified = 0;

   }

   /**
    * @param store
    * @param resourceUri
    * @param input
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   ResourceHandlerBean(final AbstractFileStore store, final String resourceUri, @NotNull final String content) {
	this.store = store;
	this.resourceUri = resourceUri;
	input = null;
	text = content;
	reader = null;
	bytes = null;
	closed = false;
	lastModified = 0;
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
	text = null;
	reader = null;
	bytes = null;
	closed = false;
	lastModified = 0;
   }

   @Override
   public String getResourceUri() {
	return resourceUri;
   }

   @Override
   public InputStream getInputStream() {

	if (input != null) {
	   return input;
	} else if (bytes != null) {
	   input = new ByteArrayInputStream(bytes);
	   return input;
	} else {
	   String charset = store.context.getString(Charset, DEFAULT_CHARSET.getCode());
	   try {
		input = new ByteArrayInputStream(text.getBytes(charset));
		return input;
	   } catch (UnsupportedEncodingException uee) {
		throw new IllegalStateException("Invalid charset encoding " + charset, uee);
	   }
	}

   }

   @Override
   public byte[] getBytes() throws ResourceException {

	if (bytes != null) {
	   return bytes;
	} else if (text != null) {
	   String charset = store.context.getString(Charset, DEFAULT_CHARSET.getCode());
	   try {
		return text.getBytes(charset);
	   } catch (UnsupportedEncodingException uee) {
		throw new IllegalStateException("Invalid charset encoding " + charset, uee);
	   }
	} else {
	   try {
		return IOHelper.toByteArray(input);
	   } catch (final IOException ioe) {
		throw new ResourceException(ioe, resourceUri);
	   } finally {
		// free resource handler
		close();
	   }
	}
   }

   @Override
   public Reader getReader() throws ResourceException {
	return getReader(store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
   }

   @Override
   public Reader getReader(final String charset) throws ResourceException {

	if (reader != null) {
	   return reader;
	} else if (text != null) {
	   reader = new StringReader(text);
	   return reader;
	} else {
	   try {
		reader = new InputStreamReader(getInputStream(), charset);
		return reader;
	   } catch (final IOException ioe) {
		throw new ResourceException(ioe, resourceUri);
	   }
	}
   }

   @Override
   public String getText() throws ResourceException {

	if (text != null) {
	   return text;
	} else {
	   return getText(store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
	}
   }

   @Override
   public String getText(final String charset) throws ResourceException {

	if (text != null) {
	   return text;
	} else {
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
   }

   @Override
   public void close() {
	if (input != null && !closed) {
	   try {
		input.close();
		closed = true;
		if (store != null) {
		   store.unregisterOpenedResource(resourceUri);
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.inputstream.close.error", ioe.getMessage(), ioe), ioe);
	   }
	}

	if (reader != null && !closed) {
	   try {
		reader.close();
		closed = true;
		if (store != null) {
		   store.unregisterOpenedResource(resourceUri);
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.StoreMessageBundle.getMessage("store.inputstream.close.error", ioe.getMessage(), ioe), ioe);
	   }
	}
   }

   @Override
   public long getLastModified() {
	return lastModified;
   }

   void setLastModified(final long lastModified) {
	this.lastModified = lastModified;
   }

   @Override
   public String getMimeType() {
	return mimeType;
   }

   void setMimeType(final String mimeType) {
	this.mimeType = mimeType;
   }

}
