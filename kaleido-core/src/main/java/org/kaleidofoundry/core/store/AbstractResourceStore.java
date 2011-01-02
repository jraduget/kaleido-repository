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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ResourceStoreMessageBundle;

import java.net.URI;
import java.net.URLConnection;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * ResourceStore abstract class
 * 
 * @author Jerome RADUGET
 */
@Immutable
public abstract class AbstractResourceStore implements ResourceStore {

   protected final RuntimeContext<ResourceStore> context;

   /**
    * runtime context injection by constructor
    * 
    * @param context
    */
   public AbstractResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	this.context = context;
   }

   /**
    * @return runtime context of the instance
    */
   @NotNull
   protected RuntimeContext<ResourceStore> getContext() {
	return context;
   }

   /**
    * @return types of the resource store (classpath:/, file:/, http://, https://, ftp://, sftp:/...)
    */
   @NotNull
   public abstract ResourceStoreType[] getStoreType();

   /**
    * resource connection processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @return resource handler
    * @throws ResourceException
    * @throws ResourceNotFoundException
    */
   protected abstract ResourceHandler doGet(@NotNull URI resourceUri) throws ResourceException;

   /**
    * remove processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @throws ResourceException
    * @throws ResourceNotFoundException
    */
   protected abstract void doRemove(@NotNull URI resourceUri) throws ResourceException;

   /**
    * store processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @param resource
    * @throws ResourceException
    * @throws ResourceNotFoundException
    */
   protected abstract void doStore(@NotNull URI resourceUri, @NotNull ResourceHandler resource) throws ResourceException;

   /**
    * build a full resource uri, given a relative path
    * 
    * @param resourceRelativePath
    * @return full resource uri, given the relative path parameter
    */
   protected String buildResourceURi(final String resourceRelativePath) {

	final String resourceRootUri = context.getProperty(ResourceContextBuilder.UriRootPath);
	final StringBuilder resourceUri = new StringBuilder(resourceRootUri != null ? resourceRootUri : "");

	// remove '/' is UriRootPath ends with '/' and resourceRelativePath starts with a '/'
	if (resourceRootUri != null && resourceRootUri.endsWith("/") && resourceRelativePath != null && resourceRelativePath.startsWith("/")) {
	   resourceUri.deleteCharAt(resourceUri.length() - 1);
	} else {

	   // add '/' if needed
	   if (resourceRootUri != null && !resourceRootUri.endsWith("/") && resourceRelativePath != null && !resourceRelativePath.startsWith("/")) {
		resourceUri.append("/");
	   }
	}

	resourceUri.append(resourceRelativePath);

	return resourceUri.toString();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceStore#isUriManageable(java.net.String)
    */
   @Override
   public boolean isUriManageable(@NotNull final String resourceUri) {

	final ResourceStoreType rst = ResourceStoreTypeEnum.match(resourceUri);

	if (rst != null) {
	   for (final ResourceStoreType t : getStoreType()) {
		if (t.equals(rst)) { return true; }
	   }
	}

	throw new IllegalArgumentException(ResourceStoreMessageBundle.getMessage("store.resource.uri.illegal", resourceUri.toString(), getClass().getName()));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.ConfigurationManager#isReadOnly()
    */
   public final boolean isReadOnly() {
	if (StringHelper.isEmpty(context.getProperty(ResourceContextBuilder.Readonly))) {
	   return false;
	} else {
	   return Boolean.valueOf(context.getProperty(ResourceContextBuilder.Readonly));
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#load(java.lang.Object)
    */
   @Override
   public final ResourceHandler get(@NotNull final String resourceRelativePath) throws ResourceException {
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);
	final ResourceHandler in = doGet(URI.create(resourceUri));
	if (in == null || in.getInputStream() == null) { throw new ResourceNotFoundException(resourceRelativePath); }
	return in;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#remove(java.lang.Object)
    */
   @Override
   public final ResourceStore remove(@NotNull final String resourceRelativePath) throws ResourceException {
	if (isReadOnly()) { throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.resource.readonly.illegal", context.getName())); }
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);
	doRemove(URI.create(resourceUri));
	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#store(java.lang.Object, java.lang.Object)
    */
   @Override
   public final ResourceStore store(@NotNull final String resourceRelativePath, @NotNull final ResourceHandler resource) throws ResourceException {
	if (isReadOnly()) { throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.resource.readonly.illegal", context.getName())); }
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);
	doStore(URI.create(resourceUri), resource);
	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceStore#move(java.lang.String, java.lang.String)
    */
   @Override
   public final ResourceStore move(@NotNull final String origin, @NotNull final String destination) throws ResourceException {
	if (isReadOnly()) { throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.resource.readonly.illegal", context.getName())); }

	isUriManageable(buildResourceURi(origin));
	isUriManageable(buildResourceURi(destination));

	if (exists(destination)) {
	   remove(destination);
	}

	ResourceHandler resource = null;

	try {
	   resource = get(origin);
	   if (resource != null) {
		store(destination, resource);
	   }

	   remove(origin);

	   return this;
	} finally {
	   if (resource != null) {
		resource.release();
	   }
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#exists(java.lang.Object)
    */
   @Override
   public final boolean exists(@NotNull final String resourceRelativePath) throws ResourceException {
	ResourceHandler resource = null;
	try {
	   resource = get(resourceRelativePath);
	   return true;
	} catch (final ResourceNotFoundException rnfe) {
	   return false;
	} finally {
	   if (resource != null) {
		resource.release();
	   }
	}
   }

   /**
    * @param urlConnection
    */
   protected void setUrlConnectionSettings(final URLConnection urlConnection) {

	if (!StringHelper.isEmpty(context.getProperty(ResourceContextBuilder.ConnectTimeout))) {
	   urlConnection.setConnectTimeout(Integer.parseInt(context.getProperty(ResourceContextBuilder.ConnectTimeout)));
	}
	if (!StringHelper.isEmpty(context.getProperty(ResourceContextBuilder.ReadTimeout))) {
	   urlConnection.setReadTimeout(Integer.parseInt(context.getProperty(ResourceContextBuilder.ReadTimeout)));
	}
	if (!StringHelper.isEmpty(context.getProperty(ResourceContextBuilder.UseCaches))) {
	   urlConnection.setUseCaches(Boolean.parseBoolean(context.getProperty(ResourceContextBuilder.UseCaches)));
	}

   }
}