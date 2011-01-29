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
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.MaxRetryOnFailure;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.SleepTimeBeforeRetryOnFailure;

import java.net.URI;
import java.net.URLConnection;

import org.kaleidofoundry.core.context.RuntimeContext;
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
 * <li>{@link #doStore(URI, FileHandler)}</li>
 * </ul>
 * Then, annotate {@link Declare} your new class to register your implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
public abstract class AbstractFileStore implements FileStore {

   protected final RuntimeContext<FileStore> context;

   protected final Logger logger;

   /**
    * runtime context injection by constructor
    * 
    * @param context
    */
   public AbstractFileStore(@NotNull final RuntimeContext<FileStore> context) {
	this.context = context;
	this.logger = LoggerFactory.getLogger(this.getClass());
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
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract FileHandler doGet(@NotNull URI resourceUri) throws ResourceNotFoundException, StoreException;

   /**
    * remove processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract void doRemove(@NotNull URI resourceUri) throws ResourceNotFoundException, StoreException;

   /**
    * store processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @param resource
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract void doStore(@NotNull URI resourceUri, @NotNull FileHandler resource) throws StoreException;

   /**
    * build a full resource uri, given a relative path
    * 
    * @param resourceRelativePath
    * @return full resource uri, given the relative path parameter
    */
   protected String buildResourceURi(final String resourceRelativePath) {

	final String resourceRootUri = context.getString(FileStoreContextBuilder.UriRootPath);
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
	if (StringHelper.isEmpty(context.getString(FileStoreContextBuilder.Readonly))) {
	   return false;
	} else {
	   return context.getBoolean(FileStoreContextBuilder.Readonly);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.FileStore#get(java.lang.String)
    */
   @Override
   public final FileHandler get(@NotNull final String resourceRelativePath) throws StoreException {
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	StoreException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to get the resource
		final FileHandler in = doGet(URI.create(resourceUri));
		if (in == null || in.getInputStream() == null) { throw new ResourceNotFoundException(resourceRelativePath); }
		return in;
	   } catch (final StoreException rse) {
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
   public final FileStore remove(@NotNull final String resourceRelativePath) throws StoreException {
	if (isReadOnly()) { throw new StoreException("store.readonly.illegal", context.getName()); }

	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	StoreException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to remove the resource
		doRemove(URI.create(resourceUri));
		return this;
	   } catch (final StoreException rse) {
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
    * @see org.kaleidofoundry.core.store.FileStore#store(java.lang.String, org.kaleidofoundry.core.store.FileHandler)
    */
   @Override
   public final FileStore store(@NotNull final String resourceRelativePath, @NotNull final FileHandler resource) throws StoreException {
	if (isReadOnly()) { throw new StoreException("store.readonly.illegal", context.getName()); }
	final String resourceUri = buildResourceURi(resourceRelativePath);
	isUriManageable(resourceUri);

	int retryCount = 0;
	int maxRetryCount = 1;
	StoreException lastError = null;

	while (retryCount < maxRetryCount) {
	   try {
		// try to store the resource
		doStore(URI.create(resourceUri), resource);
		return this;
	   } catch (final StoreException rse) {
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
			logger.warn(StoreMessageBundle.getMessage("store.failover.retry.store.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount));
			try {
			   Thread.sleep((sleepTime));
			} catch (final InterruptedException e) {
			   logger.error(StoreMessageBundle.getMessage("store.failover.retry.error", sleepTime), rse);
			   throw rse;
			}
		   } else {
			logger.error(StoreMessageBundle.getMessage("store.failover.retry.store.info", resourceRelativePath, sleepTime, retryCount, maxRetryCount),
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
    * @see org.kaleidofoundry.core.store.FileStore#move(java.lang.String, java.lang.String)
    */
   @Override
   public final FileStore move(@NotNull final String origin, @NotNull final String destination) throws ResourceNotFoundException, StoreException {
	if (isReadOnly()) { throw new StoreException("store.readonly.illegal", context.getName()); }

	isUriManageable(buildResourceURi(origin));
	isUriManageable(buildResourceURi(destination));

	if (exists(destination)) {
	   remove(destination);
	}

	final FileHandler resource = get(origin);

	try {
	   if (resource != null) {
		store(destination, resource);
	   }
	} finally {
	   resource.release();
	}
	remove(origin);

	return this;

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#exists(java.lang.Object)
    */
   @Override
   public final boolean exists(@NotNull final String resourceRelativePath) throws StoreException {
	FileHandler resource = null;
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

	if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.ConnectTimeout))) {
	   urlConnection.setConnectTimeout(context.getInteger(FileStoreContextBuilder.ConnectTimeout));
	}
	if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.ReadTimeout))) {
	   urlConnection.setReadTimeout(context.getInteger(FileStoreContextBuilder.ReadTimeout));
	}
	if (!StringHelper.isEmpty(context.getString(FileStoreContextBuilder.UseCaches))) {
	   urlConnection.setUseCaches(context.getBoolean(FileStoreContextBuilder.UseCaches));
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