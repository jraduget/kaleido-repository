/*  
 * Copyright 2008-2016 the original author or authors 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.kaleidofoundry.core.lang.annotation.NotNull;

import com.google.appengine.api.files.FileReadChannel;

/**
 * Resource handler for google application engine
 * 
 * @author jraduget
 */
public class GaeResourceHandlerBean extends ResourceHandlerBean {

   private static final long serialVersionUID = 3788713169684887718L;

   private FileReadChannel readChannel;

   /**
    * @param store
    * @param resourceUri
    * @param content
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, @NotNull byte[] content) {
	super(store, resourceUri, content);
   }

   /**
    * @param store
    * @param resourceUri
    * @param content
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, @NotNull String content) {
	super(store, resourceUri, content);
   }

   /**
    * @param store
    * @param resourceUri
    * @param content
    * @param charset
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, @NotNull String content, String charset) {
	super(store, resourceUri, content, charset);
   }

   /**
    * @param store
    * @param resourceUri
    * @param readChannel
    * @param reader
    * @param charset
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, FileReadChannel readChannel, @NotNull Reader reader, String charset) {
	super(store, resourceUri, reader, charset);
	this.readChannel = readChannel;
   }

   /**
    * @param store
    * @param resourceUri
    * @param reader
    * @param charset
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, @NotNull Reader reader, String charset) {
	super(store, resourceUri, reader, charset);
   }

   /**
    * @param store
    * @param resourceUri
    * @param input
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, FileReadChannel readChannel, InputStream input) {
	super(store, resourceUri, input);
	this.readChannel = readChannel;
   }

   /**
    * @param store
    * @param resourceUri
    * @param input
    */
   public GaeResourceHandlerBean(AbstractFileStore store, String resourceUri, InputStream input) {
	super(store, resourceUri, input);
   }

   @Override
   public void close() {
	if (readChannel != null && !isClosed()) {
	   try {
		readChannel.close();
	   } catch (final IOException ioe) {
		throw new IllegalStateException(StoreMessageBundle.getMessage("store.resource.close.error", ioe.getMessage(), ioe), ioe);
	   }
	}
	super.close();
   }

}
