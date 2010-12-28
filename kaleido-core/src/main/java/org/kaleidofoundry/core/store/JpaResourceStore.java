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
import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;
import static org.kaleidofoundry.core.store.ResourceStoreConstants.ClobJpaStorePluginName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.store.entity.ResourceStoreEntity;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JPA resource store. Resource will be stored in clob or / blob database<br/>
 * 
 * @author Jerome RADUGET
 * @see ResourceContextBuilder enum of context configuration properties available
 */
@Declare(ClobJpaStorePluginName)
@Review(comment = "Annotate it as @Stateless ejb to enable ejb exposition + injection - import right ejb 3.x library - problem : coupling it to ejb3")
public class JpaResourceStore extends AbstractResourceStore implements ResourceStore {

   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   private EntityManager em;

   /**
    * @return current entity manager (handle managed one or not)
    */
   @NotNull
   protected final EntityManager getEntityManager() {
	/*
	 * done via aop : PersistenceContextAspect
	 * // unmanaged environment
	 * if (em == null) {
	 * return UnmanagedEntityManagerFactory.currentEntityManager(KaleidoPersistentContextUnitName);
	 * }
	 * // managed environment
	 * else {
	 * return em;
	 * }
	 */
	return em;
   }

   /**
    * @param context
    */
   public JpaResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#getStoreType()
    */
   @Override
   public ResourceStoreType[] getStoreType() {
	return new ResourceStoreType[] { ResourceStoreTypeEnum.jpa };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doLoad(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws ResourceException {
	final ResourceStoreEntity entity = getEntityManager().find(ResourceStoreEntity.class, resourceUri.getPath());
	if (entity == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   final ResourceHandler resourceHandler = new ResourceHandlerBean(resourceUri.toString(), new ByteArrayInputStream(entity.getContent()));
	   return resourceHandler;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws ResourceException {
	final ResourceStoreEntity entity = getEntityManager().find(ResourceStoreEntity.class, resourceUri.getPath());
	if (entity == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   getEntityManager().remove(entity);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI, org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {

	final ResourceStoreEntity storeEntity = newInstance();
	final String filename = resourceUri.getPath().substring(1);

	storeEntity.setUri(resourceUri.toString());
	storeEntity.setName(FileHelper.getFileName(filename));
	storeEntity.setPath(filename);
	storeEntity.setCreationDate(Calendar.getInstance().getTime());
	storeEntity.setUpdatedDate(Calendar.getInstance().getTime());

	if (resource.getInputStream() != null) {

	   final int buffSize = !StringHelper.isEmpty(context.getProperty(ResourceContextBuilder.BufferSize)) ? Integer.valueOf(context
		   .getProperty(ResourceContextBuilder.BufferSize)) : 128;
	   final byte[] buffer = new byte[buffSize];
	   final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	   try {
		while (resource.getInputStream().read(buffer) >= 0) {
		   outputStream.write(buffer);
		}
		storeEntity.setContent(outputStream.toByteArray());

		getEntityManager().merge(storeEntity);

	   } catch (final IOException ioe) {
		throw new ResourceException(ioe, resourceUri.toString());
	   }
	}
   }

   /**
    * @return new ResourceStoreEnity instance (default or custom) depending from the current context
    */
   private ResourceStoreEntity newInstance() {

	final String customResourceStoreEntityClass = context.getProperty(ResourceContextBuilder.CustomResourceStoreEntity);

	if (StringHelper.isEmpty(customResourceStoreEntityClass)) {
	   return new ResourceStoreEntity();
	} else {
	   try {
		final Class<?> customClass = Class.forName(customResourceStoreEntityClass);
		final Object instance = customClass.newInstance();

		if (instance instanceof ResourceStoreEntity) {
		   return (ResourceStoreEntity) instance;
		} else {
		   throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.illegaltype", context.getName(),
			   context.getProperty(ResourceContextBuilder.CustomResourceStoreEntity), ResourceContextBuilder.CustomResourceStoreEntity));
		}

	   } catch (final ClassNotFoundException cnfe) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.notfound", context.getName(),
			context.getProperty(ResourceContextBuilder.CustomResourceStoreEntity), ResourceContextBuilder.CustomResourceStoreEntity));
	   } catch (final IllegalAccessException iae) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.illegalconstructor",
			customResourceStoreEntityClass, iae.getMessage()));

	   } catch (final InstantiationException ie) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.cantcreate", customResourceStoreEntityClass,
			ie.getMessage()));
	   }
	}
   }
}
