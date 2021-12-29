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

import static org.kaleidofoundry.core.store.FileStoreConstants.ClasspathStorePluginName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;

import org.kaleidofoundry.core.context.IllegalContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.system.JavaSystemHelper;

/**
 * Classpath file store implementation<br/>
 * <br/>
 * <b>This implementation is only for read only use</b> - the methods store, remove, move will throws {@link ResourceException}<br/>
 * 
 * @author jraduget
 * @see FileStoreContextBuilder enum of context configuration properties available
 */
@Immutable
@Declare(ClasspathStorePluginName)
public class ClasspathFileStore extends AbstractFileStore implements FileStore {

   /**
    * @param context
    */
   public ClasspathFileStore(@NotNull final RuntimeContext<FileStore> context) {
	super(context);
   }

   /**
    * @param baseUri
    * @param context
    */
   public ClasspathFileStore(final String baseUri, final RuntimeContext<FileStore> context) {
	super(baseUri, context);
   }

   /**
    * @see AbstractFileStore#AbstractFileStore()
    */
   ClasspathFileStore() {
	super();
   }

   /**
    * @return class classLoader to use
    */
   @NotNull
   protected ClassLoader getClassLoader() {
	final ClassLoader classLoader;
	final String strClass = context.getString(FileStoreContextBuilder.Classloader);

	if (strClass == null) {
	   classLoader = Thread.currentThread().getContextClassLoader();
	} else {
	   try {
		classLoader = Class.forName(strClass).getClassLoader();
	   } catch (final ClassNotFoundException cnfe) {
		throw new IllegalContextParameterException(FileStoreContextBuilder.Classloader, strClass, context, cnfe);
	   }
	}
	return classLoader;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#getStoreType()
    */
   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.classpath };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#isReadOnly()
    */
   @Override
   public boolean isReadOnly() {
	return true;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceBinding) throws ResourceNotFoundException, ResourceException {
	final StringBuilder localPath = new StringBuilder();

	if (resourceBinding.getHost() != null) {
	   localPath.append(resourceBinding.getHost()).append("/");
	}
	localPath.append(resourceBinding.getPath());

	final String localPathStr = (localPath.charAt(0) == '/') ? localPath.substring(1) : localPath.toString();
	final InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPathStr);
	final URL resourceUrl = JavaSystemHelper.getResource(getClassLoader(), localPathStr);
	final ResourceHandler resourceHandler;

	if (in != null) {
	   resourceHandler = createResourceHandler(resourceBinding.toString(), in);
	   if (resourceUrl != null) {
		String resourceFileName = null;
		if (resourceUrl.getProtocol().equals("file")) {
		   resourceFileName = resourceUrl.getFile();
		} else if (resourceUrl.getProtocol().equals("jar")) {
		   try {
			resourceFileName = resourceUrl.getFile();
			JarURLConnection jarUrl = (JarURLConnection) resourceUrl.openConnection();
			resourceFileName = jarUrl.getJarFile().getName();
		   } catch (IOException ioe) {
			throw new ResourceException(ioe, resourceBinding.toString());
		   }
		}

		if (resourceFileName != null && resourceHandler instanceof ResourceHandlerBean) {
		   File resourceFile = new File(resourceFileName);
		   ((ResourceHandlerBean) resourceHandler).setLastModified(resourceFile.lastModified());
		   ((ResourceHandlerBean) resourceHandler).setLength(resourceFile.length());
		}

	   }
	   return resourceHandler;
	} else {
	   throw new ResourceNotFoundException(resourceBinding.toString());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws ResourceNotFoundException, ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, java.io.InputStream)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {
	throw new ResourceException("store.readonly.illegal", context.getName());
   }

}
