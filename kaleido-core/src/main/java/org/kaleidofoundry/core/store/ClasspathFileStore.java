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

import static org.kaleidofoundry.core.store.FileStoreConstants.ClasspathStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.system.JavaSystemHelper;

/**
 * Classpath file store implementation<br/>
 * <br/>
 * <b>This implementation is only for read only use</b> - the methods store, remove, move will throws {@link StoreException}<br/>
 * 
 * @author Jerome RADUGET
 * @see FileStoreContextBuilder enum of context configuration properties available
 */
@Immutable
@Declare(ClasspathStorePluginName)
public class ClasspathFileStore extends AbstractFileStore implements FileStore {

   private final ClassLoader classLoader;

   /**
    * @param context
    */
   public ClasspathFileStore(@NotNull final RuntimeContext<FileStore> context) {
	super(context);
	final String strClass = context.getProperty(FileStoreContextBuilder.Classloader);

	if (strClass == null) {
	   classLoader = Thread.currentThread().getContextClassLoader();
	} else {
	   try {
		classLoader = Class.forName(strClass).getClassLoader();
	   } catch (final ClassNotFoundException cnfe) {
		throw new IllegalStateException("Illegal context property 'classloader=" + strClass + "'", cnfe);
	   }
	}
   }

   /**
    * @return class classLoader to use
    */
   @NotNull
   protected ClassLoader getClassLoader() {
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
   protected FileHandler doGet(final URI resourceBinding) throws ResourceNotFoundException, StoreException {
	final StringBuilder localPath = new StringBuilder();

	if (resourceBinding.getHost() != null) {
	   localPath.append(resourceBinding.getHost()).append("/");
	}
	localPath.append(resourceBinding.getPath());

	if (localPath.charAt(0) == '/') {
	   final InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.substring(1));
	   if (in != null) {
		return new FileHandlerBean(resourceBinding.toString(), in);
	   } else {
		throw new ResourceNotFoundException(resourceBinding.toString());
	   }
	} else {
	   final InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.toString());
	   if (in != null) {
		return new FileHandlerBean(resourceBinding.toString(), in);
	   } else {
		throw new ResourceNotFoundException(resourceBinding.toString());
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceBinding) throws ResourceNotFoundException, StoreException {
	throw new StoreException("store.readonly.illegal", context.getName());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, java.io.InputStream)
    */
   @Override
   protected void doStore(final URI resourceBinding, final FileHandler resource) throws StoreException {
	throw new StoreException("store.readonly.illegal", context.getName());
   }

}