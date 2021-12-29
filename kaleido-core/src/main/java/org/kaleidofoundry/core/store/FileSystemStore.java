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
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_BUFFER_SIZE;
import static org.kaleidofoundry.core.store.FileStoreConstants.FileSystemStorePluginName;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.BufferSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * File system store implementation (Windows, Linux, MacOS, ...) <br/>
 * <p>
 * You can use following variable in the file system uri:
 * <ul>
 * <li><code>file:/${basedir.default}</code> - the default application basedir (it can be override using the java system properties
 * -Dbasedir.default=...)</li>
 * <li><code>file:/${yourapp.basedir}</code> - your custom application basedir (it can be override using the java system properties
 * -Dyourapp.basedir=...)</li>
 * <li><code>file:/.</code> - the current system path</li>
 * <li><code>file:/..</code> - the parent of the current system path</li>
 * </ul>
 * </p>
 * 
 * @author jraduget
 * @see FileStoreContextBuilder enum of context configuration properties available
 */
@Immutable
@Declare(FileSystemStorePluginName)
public class FileSystemStore extends AbstractFileStore implements FileStore {

   /**
    * @param context
    */
   public FileSystemStore(@NotNull final RuntimeContext<FileStore> context) {
	super(context);
   }

   /**
    * @param baseUri
    * @param context
    */
   public FileSystemStore(final String baseUri, final RuntimeContext<FileStore> context) {
	super(baseUri, context);
   }

   /**
    * @see AbstractFileStore#AbstractFileStore()
    */
   FileSystemStore() {
	super();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws ResourceNotFoundException, ResourceException {
	try {
	   File file = new File(resourceUri.getPath());
	   ResourceHandler resource = createResourceHandler(resourceUri.toString(), new FileInputStream(file));

	   // Set some meta datas
	   if (resource instanceof ResourceHandlerBean) {
		((ResourceHandlerBean) resource).setLastModified(file.lastModified());
		((ResourceHandlerBean) resource).setLength(file.length());
		((ResourceHandlerBean) resource).setMimeType(MimeTypeResolverFactory.getService().getMimeType(
			FileHelper.getFileNameExtension(resourceUri.getPath())));
	   }

	   return resource;
	} catch (final FileNotFoundException fnfe) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws ResourceNotFoundException, ResourceException {

	final File file = new File(resourceUri.getPath());
	if (file.isDirectory()) {
	   throw new ResourceException("store.remove.directory", resourceUri.toString());
	} else {
	   if (!file.delete()) { throw new ResourceException("store.remove.illegal", resourceUri.toString()); }
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {

	File file;
	FileOutputStream out = null;

	try {
	   file = new File(resourceUri.getPath());
	   out = new FileOutputStream(file, false);

	   final byte[] buff = new byte[context.getInteger(BufferSize, DEFAULT_BUFFER_SIZE)];
	   int lengthToWrite = resource.getInputStream().read(buff);
	   while (lengthToWrite != -1) {
		out.write(buff, 0, lengthToWrite);
		lengthToWrite = resource.getInputStream().read(buff);
	   }

	   out.flush();

	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri.toString());
	} finally {
	   if (out != null) {
		try {
		   out.close();
		} catch (final IOException ioe) {
		   throw new ResourceException(ioe, resourceUri.toString());
		}
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#getStoreType()
    */
   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.file };
   }

   /**
    * @param filePath
    * @param fileName
    * @return Normalize the filename and path separator, depending the current OS
    */
   protected String buildFilePath(final String filePath, final String fileName) {
	return FileHelper.buildPath(filePath) + String.valueOf(fileName);
   }
}
