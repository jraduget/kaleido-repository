package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.ResourceStoreMessageBundle;
import static org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory.KaleidoPersistentContextUnitName;
import static org.kaleidofoundry.core.store.ResourceStoreConstants.ClobJpaStorePluginName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.store.entity.ResourceStoreEntity;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JPA resource store. Resource will be stored in clob or / blob database<br/>
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(ClobJpaStorePluginName)
// TODO - Annotate it as @Stateless ejb to enable ejb exposition + injection - import right ejb 3.x library - problem : coupling it to ejb3
public class JpaResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {

	/** class name of a custom resource store entity */
	customResourceStoreEntity,

	/** buffer size for reading input stream data */
	bufferSize;
   }

   @PersistenceContext(unitName = KaleidoPersistentContextUnitName)
   private EntityManager em;

   /**
    * @return current entity manager (handle managed one or not)
    */
   protected final EntityManager getEntityManager() {
	// unmanaged environment
	if (em == null) {
	   return UnmanagedEntityManagerFactory.currentEntityManager(KaleidoPersistentContextUnitName);
	}
	// managed environment
	else {
	   return em;
	}
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
   protected ResourceHandler doLoad(final URI resourceUri) throws StoreException {
	ResourceStoreEntity entity = getEntityManager().find(ResourceStoreEntity.class, resourceUri.toString());
	if (entity == null) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} else {
	   ResourceHandler resourceHandler = new ResourceHandlerBean(new ByteArrayInputStream(entity.getContent()));
	   return resourceHandler;
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceUri) throws StoreException {
	ResourceStoreEntity entity = getEntityManager().find(ResourceStoreEntity.class, resourceUri.toString());
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
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws StoreException {

	ResourceStoreEntity storeEntity = newInstance();

	storeEntity.setUri(resourceUri.toString());
	storeEntity.setName(resourceUri.getPath());
	storeEntity.setPath(resourceUri.getPath());

	if (resource.getInputStream() != null) {

	   int buffSize = StringHelper.isEmpty(context.getProperty(ContextProperty.bufferSize.name())) ? Integer.valueOf(context
		   .getProperty(ContextProperty.bufferSize.name())) : 128;
	   byte[] buffer = new byte[buffSize];
	   ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	   try {
		while (resource.getInputStream().read(buffer) >= 0) {
		   outputStream.write(buffer);
		}
		storeEntity.setContent(outputStream.toByteArray());
	   } catch (IOException ioe) {
		throw new StoreException(ioe);
	   }
	}
   }

   /**
    * @return new ResourceStoreEnity instance (default or custom) depending from the current context
    */
   private ResourceStoreEntity newInstance() {

	String customResourceStoreEntityClass = context.getProperty(ContextProperty.customResourceStoreEntity.name());

	if (StringHelper.isEmpty(customResourceStoreEntityClass)) {
	   return new ResourceStoreEntity();
	} else {
	   try {
		Class<?> customClass = Class.forName(customResourceStoreEntityClass);
		Object instance = customClass.newInstance();

		if (instance instanceof ResourceStoreEntity) {
		   return (ResourceStoreEntity) instance;
		} else {
		   throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.illegaltype", context.getName(), context
			   .getProperty(ContextProperty.customResourceStoreEntity.name()), ContextProperty.customResourceStoreEntity.name()));
		}

	   } catch (ClassNotFoundException cnfe) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.notfound", context.getName(), context
			.getProperty(ContextProperty.customResourceStoreEntity.name()), ContextProperty.customResourceStoreEntity.name()));
	   } catch (IllegalAccessException iae) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.illegalconstructor",
			customResourceStoreEntityClass, iae.getMessage()));

	   } catch (InstantiationException ie) {
		throw new IllegalStateException(ResourceStoreMessageBundle.getMessage("store.context.customentity.cantcreate", customResourceStoreEntityClass, ie
			.getMessage()));
	   }
	}
   }
}
