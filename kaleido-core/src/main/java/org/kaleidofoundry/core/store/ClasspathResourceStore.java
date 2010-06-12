/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.ResourceStoreConstants.ClasspathStorePluginName;

import java.io.InputStream;
import java.net.URI;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.system.JavaSystemHelper;

/**
 * classpath resource store implementation<br/>
 * readonly use: the store, remove, move methods will throw {@link IllegalStateException}
 * 
 * @author Jerome RADUGET
 */
@Immutable
@DeclarePlugin(ClasspathStorePluginName)
public class ClasspathResourceStore extends AbstractResourceStore implements ResourceStore {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** property name for setting the class name, to get the class loader to use */
	classloader;
   }

   // PRIVATE VARIABLES INSTANCES *************************************************************************************

   private final ClassLoader classLoader;

   /**
    * @param context
    */
   public ClasspathResourceStore(@NotNull final RuntimeContext<ResourceStore> context) {
	super(context);
	String strClass = context.getProperty(ContextProperty.classloader.name());

	if (strClass == null) {
	   classLoader = Thread.currentThread().getContextClassLoader();
	} else {
	   try {
		classLoader = Class.forName(strClass).getClassLoader();
	   } catch (ClassNotFoundException cnfe) {
		throw new IllegalStateException("illegal context property 'classloader=" + strClass + "'", cnfe);
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
   protected ResourceHandler doLoad(final URI resourceBinding) throws StoreException {
	StringBuilder localPath = new StringBuilder();

	if (resourceBinding.getHost() != null) {
	   localPath.append(resourceBinding.getHost()).append("/");
	}
	localPath.append(resourceBinding.getPath());

	if (localPath.charAt(0) == '/') {
	   InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.substring(1));
	   if (in != null) {
		return new ResourceHandlerBean(in);
	   } else {
		throw new ResourceNotFoundException(localPath.substring(1));
	   }
	} else {
	   InputStream in = JavaSystemHelper.getResourceAsStream(getClassLoader(), localPath.toString());
	   if (in != null) {
		return new ResourceHandlerBean(in);
	   } else {
		throw new ResourceNotFoundException(localPath.toString());
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doRemove(java.net.URI)
    */
   @Override
   protected void doRemove(final URI resourceBinding) throws StoreException {
	throw new IllegalStateException("Can't remove a resource from classpath. ClasspathResourceStore is for a readonly use");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractResourceStore#doStore(java.net.URI, java.io.InputStream)
    */
   @Override
   protected void doStore(final URI resourceBinding, final ResourceHandler resource) throws StoreException {
	throw new IllegalStateException("Can't store a resource in classpath. ClasspathResourceStore is for a readonly use");
   }

}
