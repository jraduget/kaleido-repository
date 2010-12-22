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

import static org.kaleidofoundry.core.store.ResourceStoreConstants.ClasspathStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.system.JavaSystemHelper;

/**
 * Classpath file resource store implementation<br/>
 * <br/>
 * <b>this store is for readonly use</b>: the methods store, remove, move will throws {@link IllegalStateException}
 * 
 * @author Jerome RADUGET
 * @see ResourceContextBuilder enum of context configuration properties available
 */
@Immutable
@Declare(ClasspathStorePluginName)
public class ClasspathResourceStore extends AbstractResourceStore implements ResourceStore {

   private final ClassLoader classLoader;

   /**
    * @param context
    */
   public ClasspathResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
	final String strClass = context.getProperty(ResourceContextBuilder.Classloader);

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
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.classpath };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceBinding) throws ResourceException {
	final StringBuilder localPath = new StringBuilder();

	if (resourceBinding.getHost() != null) {
	   localPath.append(resourceBinding.getHost()).append("/");
	}
	localPath.append(resourceBinding.getPath());

	if (localPath.charAt(0) == '/') {
	   final InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.substring(1));
	   if (in != null) {
		return new ResourceHandlerBean(resourceBinding.toString(), in);
	   } else {
		throw new ResourceNotFoundException(resourceBinding.toString());
	   }
	} else {
	   final InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.toString());
	   if (in != null) {
		return new ResourceHandlerBean(resourceBinding.toString(), in);
	   } else {
		throw new ResourceNotFoundException(resourceBinding.toString());
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceBinding) throws ResourceException {
	throw new IllegalStateException("Can't remove a resource from classpath. ClasspathResourceStore is for a readonly use");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI, java.io.InputStream)
    */
   @Override
   protected void doStore(final URI resourceBinding, final ResourceHandler resource) throws ResourceException {
	throw new IllegalStateException("Can't store a resource in classpath. ClasspathResourceStore is for a readonly use");
   }

}
