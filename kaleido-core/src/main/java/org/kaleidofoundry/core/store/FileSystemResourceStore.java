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

import static org.kaleidofoundry.core.store.ResourceStoreConstants.FileSystemStorePluginName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * File system resource store implementation (windows, linux, ...) <br/>
 * This implementation is only for read only use<br/>
 * You can extends it and override {@link #doRemove(URI)} and {@link #doStore(URI, ResourceHandler)} to your need, and {@link Declare}
 * your implementation to use it
 * 
 * @author Jerome RADUGET
 */
@Immutable
@Declare(FileSystemStorePluginName)
public class FileSystemResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * @param context
    */
   public FileSystemResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws ResourceException {
	try {
	   return new ResourceHandlerBean(new FileInputStream(new File(resourceUri)));
	} catch (FileNotFoundException fnfe) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws ResourceException {

	File file = new File(resourceUri);
	if (file.isDirectory()) {
	   throw new ResourceException("store.resource.remove.directory", resourceUri.toString());
	} else {
	   if (file.delete()) { throw new ResourceException("store.resource.remove.illegal", resourceUri.toString()); }
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI,
    * org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {

	File file;
	FileOutputStream out = null;

	try {
	   file = new File(resourceUri);
	   out = new FileOutputStream(file, false);

	   byte[] buff = new byte[128];
	   while (resource.getInputStream().read(buff) != -1) {
		out.write(buff);
	   }

	} catch (IOException ioe) {
	   throw new ResourceException(ioe);
	} finally {
	   if (out != null) {
		try {
		   out.close();
		} catch (IOException ioe) {
		   throw new IllegalStateException("error closing outputstream", ioe);
		}
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.file };
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
