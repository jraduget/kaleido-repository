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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.StoreMessageBundle;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.BaseUri;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ConnectTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.MaxRetryOnFailure;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ReadTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Readonly;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.SleepTimeBeforeRetryOnFailure;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.UseCaches;
import static org.kaleidofoundry.core.store.FileStoreProvider.FILE_STORE_REGISTRY;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FileStore} abstract class (common to all store class implementation)<br/>
 * <br/>
 * You can create your own store, by extending this class and implement :
 * <ul>
 * <li>{@link #getStoreType()}</li>
 * <li>{@link #doGet(URI)}</li>
 * <li>{@link #doRemove(URI)}</li>
 * <li>{@link #doStore(URI, ResourceHandler)}</li>
 * </ul>
 * Then, annotate {@link Declare} your new class to register your implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
public abstract class AbstractFileStore implements FileStore {

   protected final RuntimeContext<FileStore> context;

   protected final Logger logger;

   protected final String baseUri;

   protected final ConcurrentHashMap<String, ResourceHandler> openResources;

   /**
    * runtime context injection by constructor<br/>
    * the file store will be registered in {@link FileStoreFactory#getRegistry()}
    * 
    * @param context
    * @see FileStoreRegistry
    */
   public AbstractFileStore(@NotNull final RuntimeContext<FileStore> context) {
	this(null, context);
   }

   /**
    * runtime context injection by constructor<br/>
    * the file store will be registered in {@link FileStoreFactory#getRegistry()}
    * 
    * @param baseUri
    * @param context
    * @see FileStoreRegistry
    */
   public AbstractFileStore(String baseUri, @NotNull final RuntimeContext<FileStore> context) {

	// base uri parameter
	baseUri = !StringHelper.isEmpty(baseUri) ? baseUri : context.getString(BaseUri);

	// context check
	if (StringHelper.isEmpty(baseUri)) { throw new EmptyContextParameterException(BaseUri, context); }

	this.context = context;
	this.logger = LoggerFactory.getLogger(this.getClass());
	this.baseUri = baseUri;
	this.openResources = new ConcurrentHashMap<String, ResourceHandler>();

	// register the file store instance
	FILE_STORE_REGISTRY.put(getBaseUri(), this);
   }

   /*
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   AbstractFileStore() {
	this.context = null;
	this.baseUri = null;
	this.logger = LoggerFactory.getLogger(this.getClass());
	this.openResources = new ConcurrentHashMap<String, ResourceHandler>();
   }

   /**
    * @return runtime context of the instance
    */
   @NotNull
   protected RuntimeContext<FileStore> getContext() {
	return context;
   }

   /**
    * @return types of the store (classpath:/, file:/, http://, https://, ftp://, sftp:/...)
    */
   @NotNull
   public abstract FileStoreType[] getStoreType();

   /**
    * resource connection processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @return resource handler
    * @throws ResourceException
    * @throws ResourceNotFoundException
    */
   protected abstract ResourceHandler doGet(@NotNull URI resourceUri) throws ResourceNotFoundException, ResourceException;

   /**
    * remove processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @throws ResourceException
    * @throws ResourceNotFoundException
    */
   protected abstract void doRemove(@NotNull URI resourceUri) throws ResourceNotFoundException, ResourceException;

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

	boolean appendBaseUri = false;
	final String baseUri = getBaseUri();
	final String relativePath = resourceRelativePath;
	final StringBuilder resourceUri = new StringBuilder();

	if (relativePath != null && !relativePath.startsWith(baseUri)) {
	   appendBaseUri = true;
	   resourceUri.append(baseUri);
	} else {
	   resourceUri.append(relativePath);
	}

	// remove '/' is baseUri ends with '/' and relativePath starts with a '/'
	if (appendBaseUri && baseUri != null && baseUri.endsWith("/") && relativePath != null && relativePath.startsWith("/")) {
	   resourceUri.deleteCharAt(resourceUri.length() - 1);
	} else {
	   // add '/' if needed
	   if (appendBaseUri && baseUri != null && !baseUri.endsWith("/") && relativePath != null && !relativePath.startsWith("/")) {
		resourceUri.append("/");
	   }
	}

	if (appendBaseUri) {
	   resourceUri.append(relativePath);
	}

	// normalize uri by using '/' as path separator, and by replacing spaces by %20
	return StringHelper.replaceAll(FileHelper.buildCustomPath(resourceUri.toString(), FileHelper.UNIX_SEPARATOR, false), " ", "%20");
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final InputStream input) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, input);
	openResources.put(resourceUri, resource);
	return resource;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final String content) throws UnsupportedEncodingException {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, content);
	openResources.put(resourceUri, resource);
	return resource;
   }

   @Override
   public FileStore closeAll() {
	for (ResourceHandler resourceHandler : openResources.values()) {
	   resourceHandler.close();
	}
	return this;
   }

   /**
    * opened resource cleanup
    * 
    * @param resource
    * @see ResourceHandler#close()
    */
   void unregisterResource(final ResourceHandler resource) {
	unregisterResource(resource.getResourceUri());
   }

   /**
    * opened resource cleanup
    * 
    * @param resourceUri
    * @see ResourceHandler#close()
    */
   void unregisterResource(final String resourceUri) {
	openResources.remove(resourceUri);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#getBaseUri()
    */
   @Override
   @NotNull
   public String getBaseUri() {
	return baseUri;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#isUriManageable(java.net.String)
    */
   @Override
   public boolean isUriManageable(@NotNull final String resourceUri) {

	final FileStoreType rst = FileStoreTypeEnum.match(resourceUri);

	if (rst != null) {
	   for (final FileStoreType t : getStoreType()) {
		if (t.equals(rst)) { return true; }
	   }
	}

	throw new IllegalArgumentException(StoreMessageBundle.getMessage("store.uri.illegal", resourceUri.toString(), getClass().getName()));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#isReadOnly()
    */
   @Override
   public boolean isReadOnly() {
	if (StringHelper.isEmpty(context.getString(Readonly))) {
	   return false;
	} else {
	   return context.getBoolean(Readonly);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#get(java.lang.String)
    */
   @Override
   public final ResourceHandler get(@NotNull final String resourceRelativePath) throws ResourceException {
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	ResourceException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to get the resource
		final ResourceHandler in = doGet(URI.create(resourceUri));
		if (in == null || in.getInputStream() == null) { throw new ResourceNotFoundException(resourceRelativePath); }
		return in;
	   } catch (final ResourceException rse) {
		lastError = rse;
		maxRetryCount = getMaxRetryOnFailure();
		// no fail-over, we throw the exception
		if (maxRetryCount <= 0) {
		   throw rse;
		}
		// wait for the configuring delay (in milliseconds)
		else {
		   retryCount++;
		   final int sleepTime = getSleepTimeBeforeRetryOnFailure();
		   if (retryCount < maxRetryCount) {
			logger.warn(StoreMessageBundle.getMessage("store.failover.retry.get.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   logger.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			logger.error(StoreMessageBundle.getMessage("store.failover.retry.get.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount), rse);
		   }
		}
	   }
	}

	if (lastError != null) {
	   throw lastError;
	} else {
	   throw new IllegalStateException();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#remove(java.lang.String)
    */
   @Override
   public final FileStore remove(@NotNull final String resourceRelativePath) throws ResourceException {
	if (isReadOnly()) { throw new ResourceException("store.readonly.illegal", context.getName() != null ? context.getName() : ""); }

	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	ResourceException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to remove the resource
		doRemove(URI.create(resourceUri));
		return this;
	   } catch (final ResourceException rse) {
		lastError = rse;
		maxRetryCount = getMaxRetryOnFailure();
		// no fail-over, we throw the exception
		if (maxRetryCount <= 0) {
		   throw rse;
		}
		// wait for the configuring delay (in milliseconds)
		else {
		   retryCount++;
		   final int sleepTime = getSleepTimeBeforeRetryOnFailure();
		   if (retryCount < maxRetryCount) {
			logger.warn(StoreMessageBundle.getMessage("store.failover.retry.remove.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   logger.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			logger.error(StoreMessageBundle.getMessage("store.failover.retry.remove.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount),
				rse);
		   }
		}
	   }
	}

	if (lastError != null) {
	   throw lastError;
	} else {
	   throw new IllegalStateException();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#store(org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   public final FileStore store(@NotNull final ResourceHandler resource) throws ResourceException {
	if (isReadOnly()) { throw new ResourceException("store.readonly.illegal", context.getName() != null ? context.getName() : ""); }
	final String resourceUri = buildResourceURi(resource.getResourceUri());
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	ResourceException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to store the resource
		doStore(URI.create(resourceUri), resource);
		return this;
	   } catch (final ResourceException rse) {
		lastError = rse;
		maxRetryCount = getMaxRetryOnFailure();
		// no fail-over, we throw the exception
		if (maxRetryCount <= 0) {
		   throw rse;
		}
		// wait for the configuring delay (in milliseconds)
		else {
		   retryCount++;
		   final int sleepTime = getSleepTimeBeforeRetryOnFailure();
		   if (retryCount < maxRetryCount) {
			logger.warn(StoreMessageBundle.getMessage("store.failover.retry.store.info", resource.getResourceUri(), sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   logger.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			logger.error(
				StoreMessageBundle.getMessage("store.failover.retry.store.info", resource.getResourceUri(), sleepTime, retryCount, maxRetryCount), rse);
		   }
		}
	   }
	}

	if (lastError != null) {
	   throw lastError;
	} else {
	   throw new IllegalStateException();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#store(java.lang.String, java.io.InputStream)
    */
   @Override
   public FileStore store(final String resourceRelativePath, final InputStream resourceContent) throws ResourceException {
	return store(createResourceHandler(resourceRelativePath, resourceContent));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#store(java.lang.String, byte[])
    */
   @Override
   public FileStore store(final String resourceRelativePath, final byte[] resourceContent) throws ResourceException {
	return store(resourceRelativePath, new ByteArrayInputStream(resourceContent));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#store(java.lang.String, java.lang.String)
    */
   @Override
   public FileStore store(final String resourceRelativePath, final String resourceContent) throws ResourceException, UnsupportedEncodingException {
	return store(createResourceHandler(resourceRelativePath, resourceContent));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#move(java.lang.String, java.lang.String)
    */
   @Override
   public final FileStore move(@NotNull final String origin, @NotNull final String destination) throws ResourceNotFoundException, ResourceException {
	if (isReadOnly()) { throw new ResourceException("store.readonly.illegal", context.getName() != null ? context.getName() : ""); }

	isUriManageable(buildResourceURi(origin));
	isUriManageable(buildResourceURi(destination));

	if (exists(destination)) {
	   remove(destination);
	}

	final ResourceHandler resource = get(origin);

	try {
	   if (resource != null) {
		store(createResourceHandler(destination, resource.getInputStream()));
	   }
	} finally {
	   resource.close();
	}
	remove(origin);

	return this;

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
		resource.close();
	   }
	}
   }

   /**
    * @param urlConnection
    */
   protected void setUrlConnectionSettings(final URLConnection urlConnection) {

	if (!StringHelper.isEmpty(context.getString(ConnectTimeout))) {
	   urlConnection.setConnectTimeout(context.getInteger(ConnectTimeout));
	}
	if (!StringHelper.isEmpty(context.getString(ReadTimeout))) {
	   urlConnection.setReadTimeout(context.getInteger(ReadTimeout));
	}
	if (!StringHelper.isEmpty(context.getString(UseCaches))) {
	   urlConnection.setUseCaches(context.getBoolean(UseCaches));
	}

   }

   /**
    * @return max attempt after failure
    * @see FileStoreContextBuilder#MaxRetryOnFailure
    */
   protected int getMaxRetryOnFailure() {
	final Integer maxRetry = context.getInteger(MaxRetryOnFailure);
	if (maxRetry != null) {
	   return maxRetry.intValue();
	} else {
	   return -1;
	}
   }

   /**
    * @return time to sleep after failure
    * @see FileStoreContextBuilder#SleepTimeBeforeRetryOnFailure
    */
   protected int getSleepTimeBeforeRetryOnFailure() {
	final Integer sleepOnFailure = context.getInteger(SleepTimeBeforeRetryOnFailure);
	if (sleepOnFailure != null) {
	   return sleepOnFailure.intValue();
	} else {
	   return 0;
	}
   }
}