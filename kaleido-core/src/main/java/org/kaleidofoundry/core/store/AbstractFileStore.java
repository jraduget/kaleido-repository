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
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.CacheManagerRef;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Caching;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ConnectTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.MaxRetryOnFailure;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.ReadTimeout;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Readonly;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.SleepTimeBeforeRetryOnFailure;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.UseCaches;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Task;
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
@Task(comment = "Caching resources")
public abstract class AbstractFileStore implements FileStore {

   /** default fileStore logger */
   static final Logger LOGGER = LoggerFactory.getLogger(FileStore.class);

   protected final RuntimeContext<FileStore> context;

   protected final String baseUri;

   protected final ConcurrentHashMap<String, ResourceHandler> openedResources;

   protected final Cache<String, ResourceHandler> resourcesByUri;

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
	baseUri = FileStoreProvider.buildFullResourceURi(!StringHelper.isEmpty(baseUri) ? baseUri : context.getString(BaseUri));

	// context check
	if (StringHelper.isEmpty(baseUri)) { throw new EmptyContextParameterException(BaseUri, context); }

	this.context = context;
	this.baseUri = baseUri;
	openedResources = new ConcurrentHashMap<String, ResourceHandler>();

	// internal resource cache
	if (context.getBoolean(Caching, false)) {
	   final String cacheName;
	   final CacheManager cacheManager;
	   final String cacheManagerContextRef = context.getString(CacheManagerRef);

	   if (!StringHelper.isEmpty(cacheManagerContextRef)) {
		cacheManager = CacheManagerFactory.provides(new RuntimeContext<CacheManager>(cacheManagerContextRef, CacheManager.class, context));
	   } else {
		cacheManager = CacheManagerFactory.provides();
	   }
	   cacheName = "kaleidofoundry/store/" + (!StringHelper.isEmpty(context.getName()) ? context.getName() : getBaseUri().replaceAll(":", ""));
	   resourcesByUri = cacheManager.getCache(cacheName);
	} else {
	   resourcesByUri = null;
	}

	// register the file store instance
	FileStoreFactory.getRegistry().put(getBaseUri(), this);
   }

   /*
    * don't use it,
    * this constructor is only needed and used by some IOC framework like spring.
    */
   AbstractFileStore() {
	context = null;
	baseUri = null;
	resourcesByUri = null;
	openedResources = new ConcurrentHashMap<String, ResourceHandler>();
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

	// merge variables that could be contains in the resource path
	final StringBuilder mergedResourceUri = new StringBuilder(FileStoreProvider.buildFullResourceURi(resourceUri.toString()));

	// remove '/' is baseUri ends with '/' and relativePath starts with a '/'
	if (appendBaseUri && baseUri != null && baseUri.endsWith("/") && relativePath != null && relativePath.startsWith("/")) {
	   mergedResourceUri.deleteCharAt(mergedResourceUri.length() - 1);
	} else {
	   // add '/' if needed
	   if (appendBaseUri && baseUri != null && !baseUri.endsWith("/") && relativePath != null && !relativePath.startsWith("/")) {
		mergedResourceUri.append("/");
	   }
	}

	if (appendBaseUri) {
	   mergedResourceUri.append(relativePath);
	}

	String result = mergedResourceUri.toString();
	// normalize uri by using '/' as path separator
	result = FileHelper.buildCustomPath(result, FileHelper.UNIX_SEPARATOR, false);
	// normalize uri by replacing spaces by %20
	result = StringHelper.replaceAll(result, " ", "%20");

	return result;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final InputStream input) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, input);
	openedResources.put(resourceUri, resource);
	return resource;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final String content) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, content);
	return resource;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final String content, final String charset) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, content, charset);
	return resource;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final Reader reader, final String charset) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, reader, charset);
	openedResources.put(resourceUri, resource);
	return resource;
   }

   @Override
   public ResourceHandler createResourceHandler(final String resourceUri, final byte[] content) {
	ResourceHandler resource = new ResourceHandlerBean(this, resourceUri, content);
	return resource;
   }

   /**
    * creates a new resource handler for caching purposes.<br/>
    * the input and output resource handler, will sharing the same bytes data,<br/>
    * but the new one will have a dedicated inputStream / reader to avoid thread share problem between users
    * 
    * @param resourceHandler
    * @return
    * @throws ResourceException
    */
   protected ResourceHandler createCacheableResourceHandler(final ResourceHandler resourceHandler) throws ResourceException {
	ResourceHandlerBean cacheableResource = new ResourceHandlerBean(this, resourceHandler.getResourceUri(), resourceHandler.getBytes());
	cacheableResource.setLastModified(resourceHandler.getLastModified());
	cacheableResource.setMimeType(resourceHandler.getMimeType());
	cacheableResource.setCharset(resourceHandler.getCharset());
	cacheableResource.setInputStream(new ByteArrayInputStream(resourceHandler.getBytes()));
	return cacheableResource;
   }

   @Override
   public FileStore closeAll() {
	for (ResourceHandler resourceHandler : openedResources.values()) {
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
   void unregisterOpenedResource(final ResourceHandler resource) {
	unregisterOpenedResource(resource.getResourceUri());
   }

   /**
    * opened resource cleanup
    * 
    * @param resourceUri
    * @see ResourceHandler#close()
    */
   void unregisterOpenedResource(final String resourceUri) {
	openedResources.remove(resourceUri);
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
   public boolean isUriManageable(@NotNull final String pResourceUri) {

	final String resourceUri = buildResourceURi(pResourceUri);
	final FileStoreType rst = FileStoreTypeEnum.match(resourceUri);

	if (rst != null) {
	   for (final FileStoreType t : getStoreType()) {
		if (t.equals(rst)) { return true; }
	   }
	}

	throw new IllegalArgumentException(StoreMessageBundle.getMessage("store.uri.illegal", pResourceUri, getClass().getName()));
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

	// get from cache if enabled
	if (resourcesByUri != null && resourcesByUri.containsKey(resourceUri)) {
	   // create a new resourceHandler sharing same bytes data, but with a specific user inputStream / reader
	   return createCacheableResourceHandler(resourcesByUri.get(resourceUri));
	}

	while (retryCount < maxRetryCount) {
	   try {
		// try to get the resource
		final ResourceHandler in = doGet(URI.create(resourceUri));
		if (in == null || in.isEmpty()) { throw new ResourceNotFoundException(resourceRelativePath); }
		// if caching is enabled : put to cache
		if (resourcesByUri != null) {
		   final ResourceHandler cacheableResource = createResourceHandler(resourceUri, in.getBytes());
		   if (cacheableResource instanceof ResourceHandlerBean) {
			((ResourceHandlerBean) cacheableResource).setLastModified(in.getLastModified());
			((ResourceHandlerBean) cacheableResource).setMimeType(in.getMimeType());
			((ResourceHandlerBean) cacheableResource).setCharset(in.getCharset());
		   }
		   resourcesByUri.put(resourceUri, cacheableResource);
		   return cacheableResource;
		}
		// no cache, direct resource access
		else {
		   return in;
		}
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
			LOGGER.warn(StoreMessageBundle.getMessage("store.failover.retry.get.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   LOGGER.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			LOGGER.error(StoreMessageBundle.getMessage("store.failover.retry.get.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount), rse);
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

	// invalidate cache entry
	if (resourcesByUri != null && resourcesByUri.containsKey(resourceUri)) {
	   resourcesByUri.remove(resourceUri);
	}

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
			LOGGER.warn(StoreMessageBundle.getMessage("store.failover.retry.remove.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   LOGGER.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			LOGGER.error(StoreMessageBundle.getMessage("store.failover.retry.remove.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount),
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

	// invalidate cache entry
	if (resourcesByUri != null && resourcesByUri.containsKey(resourceUri)) {
	   resourcesByUri.remove(resourceUri);
	}

	while (retryCount < maxRetryCount) {
	   try {
		// Set some meta datas
		if (resource instanceof ResourceHandlerBean) {
		   ((ResourceHandlerBean) resource).setLastModified(System.currentTimeMillis());
		   ((ResourceHandlerBean) resource).setMimeType(MimeTypeResolverFactory.getService().getMimeType(FileHelper.getFileNameExtension(resourceUri)));
		}
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
			LOGGER.warn(StoreMessageBundle.getMessage("store.failover.retry.store.info", resource.getResourceUri(), sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   LOGGER.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			LOGGER.error(
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
	return store(createResourceHandler(resourceRelativePath, resourceContent));
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