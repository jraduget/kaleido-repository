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
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.StoreMessageBundle;
import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_CHARSET;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Charset;
import static org.kaleidofoundry.core.util.ObjectHelper.firstNonNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Default {@link ResourceHandler} implementation <br/>
 * If the class is built using constructor with a {@link Reader} or an {@link InputStream}, the instance will not be thread safe ! <br/>
 * If the class is built with the raw data of the resource, there is no problem.
 * 
 * @author jraduget
 */
@Immutable
@NotThreadSafe(comment = "for the instances built by calling constructor with an inputstream or a reader")
class ResourceHandlerBean implements ResourceHandler, Serializable {

   private static final long serialVersionUID = 1L;

   private final transient AbstractFileStore store;

   private final String uri;
   private long lastModified;
   private String mimeType;
   private String charset;

   private boolean closed;   
   private byte[] bytes;   
   private String text;
   private long length;

   private transient InputStream input;
   private transient Reader reader;

   /**
    * needed by REST javax.ws.rs controller
    */
   ResourceHandlerBean() {	
	this(null, null, (InputStream)null);
   }
   
   /**
    * @param store
    * @param uri
    * @param content
    */
   ResourceHandlerBean(final AbstractFileStore store, final String uri, @NotNull final byte[] content) {
	this.store = store;
	this.uri = uri;
	input = null;
	text = null;
	reader = null;
	bytes = content;
	closed = false;
	lastModified = -1;
	length = -1;
   }

   /**
    * @param store
    * @param uri
    * @param content
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   ResourceHandlerBean(final AbstractFileStore store, final String uri, @NotNull final String content) {
	this.store = store;
	this.uri = uri;
	input = null;
	text = content;
	reader = null;
	bytes = null;
	closed = false;
	lastModified = -1;
	length = -1;
   }

   /**
    * @param store
    * @param uri
    * @param content
    * @param charset
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   ResourceHandlerBean(final AbstractFileStore store, final String uri, @NotNull final String content, final String charset) {
	this.store = store;
	this.uri = uri;
	input = null;
	text = content;
	this.charset = charset;
	reader = null;
	bytes = null;
	closed = false;
	lastModified = -1;
	length = -1;
   }

   /**
    * @param store
    * @param uri
    * @param reader
    * @param charset
    * @throws UnsupportedEncodingException default text encoding is UTF-8
    */
   ResourceHandlerBean(final AbstractFileStore store, final String uri, @NotNull final Reader reader, final String charset) {
	this.store = store;
	this.uri = uri;
	input = null;
	text = null;
	this.charset = charset;
	this.reader = reader;
	bytes = null;
	closed = false;
	lastModified = -1;
	length = -1;
   }

   /**
    * @param store
    * @param uri
    * @param input
    */
   ResourceHandlerBean(final AbstractFileStore store, final String uri, final InputStream input) {
	this.store = store;
	this.uri = uri;
	this.input = input;
	text = null;
	reader = null;
	bytes = null;
	closed = false;
	lastModified = -1;
	length = -1;
   }


   @Override
   public String getUri() {
	return uri;
   }

   @Override
   public String getPath() {
	if (store != null && store.getBaseUri() != null) {
	   return uri.substring(store.getBaseUri().length());
	} else {
	   return uri;
	}
   }
   
   @Override
   public boolean isEmpty() {
	return input == null && reader == null && text == null && bytes == null;
   }

   @Override
   public boolean isClosed() {
	return closed;
   }

   @Override
   public InputStream getInputStream() throws ResourceException {

	if (input != null) {
	   return input;
	} else if (bytes != null) {
	   input = new ByteArrayInputStream(bytes);
	   return input;
	} else {
	   String charset = firstNonNull(this.charset, store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
	   try {
		input = new ByteArrayInputStream(getText().getBytes(charset));
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
	   String charset = firstNonNull(this.charset, store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
	   try {
		return text.getBytes(charset);
	   } catch (UnsupportedEncodingException uee) {
		throw new IllegalStateException("Invalid charset encoding " + charset, uee);
	   }
	} else {
	   try {
		bytes = IOHelper.toByteArray(input);
		return bytes;
	   } catch (final IOException ioe) {
		throw new ResourceException(ioe, uri);
	   } finally {
		// free resource handler
		close();
	   }
	}
   }

   @Override
   public Reader getReader() throws ResourceException {
	String charset = firstNonNull(this.charset, store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
	return getReader(charset);
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
		throw new ResourceException(ioe, uri);
	   }
	}
   }

   @Override
   public String getText() throws ResourceException {

	if (text != null) {
	   return text;
	} else {
	   String charset = firstNonNull(this.charset, store.context.getString(Charset, DEFAULT_CHARSET.getCode()));
	   return getText(charset);
	}
   }

   @Override
   public String getText(final String charset) throws ResourceException {

	if (text != null) {
	   return text;
	} else {
	   BufferedReader buffReader = null;
	   String inputLine;

	   try {
		final StringBuilder stb = new StringBuilder();
		buffReader = new BufferedReader(getReader(charset));
		while ((inputLine = buffReader.readLine()) != null) {
		   stb.append(inputLine.trim()).append("\n");
		}
		if (stb.length() <= 0) {
		   text = "";
		} else {
		   text = stb.toString().substring(0, stb.length() - 1);
		}
		return text;
	   } catch (final IOException ioe) {
		throw new ResourceException(ioe, uri);
	   } finally {
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
		   store.unregisterOpenedResource(uri);
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(StoreMessageBundle.getMessage("store.resource.close.error", ioe.getMessage(), ioe), ioe);
	   }
	}

	if (reader != null && !closed) {
	   try {
		reader.close();
		closed = true;
		if (store != null) {
		   store.unregisterOpenedResource(uri);
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(StoreMessageBundle.getMessage("store.resource.close.error", ioe.getMessage(), ioe), ioe);
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

   @Override
   public String getCharset() {
	return charset;
   }

   void setCharset(final String charset) {
	this.charset = charset;
   }

   @Override
   public long getLength() {
      return length;
   }

   void setLength(long length) {
      this.length = length;
   }

   /**
    * this method is only used by for caching resource handler (if caching is enable)
    * 
    * @param inputStream
    */
   void setInputStream(final InputStream inputStream) {
	input = inputStream;
   }
}
