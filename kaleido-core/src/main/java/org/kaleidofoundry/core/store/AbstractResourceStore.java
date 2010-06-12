package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ResourceStoreMessageBundle;

import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;

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
    * @return types of the resource store (classpath:/, file:/, http:/, https://, ftp:/, sftp:/...)
    */
   @NotNull
   public abstract ResourceStoreType[] getStoreType();

   /**
    * load processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @return
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract ResourceHandler doLoad(@NotNull URI resourceUri) throws StoreException;

   /**
    * remove processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract void doRemove(@NotNull URI resourceUri) throws StoreException;

   /**
    * store processing, you don't have to check argument validity
    * 
    * @param resourceUri
    * @param resource
    * @throws StoreException
    * @throws ResourceNotFoundException
    */
   protected abstract void doStore(@NotNull URI resourceUri, @NotNull ResourceHandler resource) throws StoreException;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceStore#isUriManageable(java.net.URI)
    */
   @Override
   public boolean isUriManageable(@NotNull final URI resourceUri) {

	ResourceStoreType rst = ResourceStoreTypeEnum.match(resourceUri);

	if (rst != null) {
	   for (ResourceStoreType t : getStoreType()) {
		if (t.equals(rst)) { return true; }
	   }
	}

	throw new IllegalArgumentException(ResourceStoreMessageBundle.getMessage("store.resource.uri.illegal", resourceUri.toString(), getClass().getName()));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#load(java.lang.Object)
    */
   @Override
   public final ResourceHandler load(@NotNull final URI resourceUri) throws StoreException {
	isUriManageable(resourceUri);
	ResourceHandler in = doLoad(resourceUri);
	if (in == null || in.getInputStream() == null) { throw new ResourceNotFoundException(resourceUri.toString()); }
	return in;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#remove(java.lang.Object)
    */
   @Override
   public final ResourceStore remove(@NotNull final URI resourceUri) throws StoreException {
	isUriManageable(resourceUri);
	doRemove(resourceUri);
	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#store(java.lang.Object, java.lang.Object)
    */
   @Override
   public final ResourceStore store(@NotNull final URI resourceUri, @NotNull final ResourceHandler resource) throws StoreException {
	isUriManageable(resourceUri);
	doStore(resourceUri, resource);
	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceStore#move(java.lang.String, java.lang.String)
    */
   @Override
   public ResourceStore move(@NotNull final URI origin, @NotNull final URI destination) throws StoreException {
	isUriManageable(origin);
	isUriManageable(destination);

	if (exists(destination)) {
	   remove(destination);
	}

	ResourceHandler in = load(origin);

	if (in != null) {
	   store(destination, in);
	}

	return this;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.pattern.Store#exists(java.lang.Object)
    */
   @Override
   public boolean exists(@NotNull final URI resourceUri) throws StoreException {
	ResourceHandler resource = null;
	try {
	   resource = load(resourceUri);
	   return true;
	} catch (ResourceNotFoundException rnfe) {
	   return false;
	} finally {
	   if (resource != null) {
		resource.release();
	   }
	}
   }

}