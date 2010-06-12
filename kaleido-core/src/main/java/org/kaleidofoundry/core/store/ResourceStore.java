package org.kaleidofoundry.core.store;

import static org.kaleidofoundry.core.store.ResourceStoreConstants.ResourceStorePluginName;

import java.net.URI;
import java.net.URL;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Stateless;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * Store for resource like file properties, xml datas, binary content (pdf report, email...) that need to be persit on a
 * file storage. The storage will be determining by the uri schem
 * <p>
 * The {@link URI} (Uniform Resource Identifier) allow to bind different kind of resource :<br/>
 * You can use classic {@link URL} like :
 * <ul>
 * <li>http://
 * <li>ftp://
 * <li>file://
 * <li>webapp:/
 * <li>classpath:/
 * </ul>
 * <p>
 * More you can extends it, using other kind of "custom user" protocol (classpath://, jpa://, jdbc://, ...)
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Stateless
@DeclarePlugin(ResourceStorePluginName)
public interface ResourceStore extends Store<URI, ResourceHandler> {

   /**
    * load a given resource <br/>
    * 
    * @param resourceUri resource binding informations to access the resource<br/>
    * @return resource input stream
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws StoreException other kind of error
    */
   @Override
   @NotNull
   ResourceHandler load(@NotNull URI resourceUri) throws StoreException;

   /**
    * store updates on current R instance<br/>
    * 
    * @param resourceUri resource uri which have to be store
    * @param resource resource input stream to store
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws StoreException other kind of error
    */
   @Override
   @NotNull
   ResourceStore store(@NotNull URI resourceUri, @NotNull ResourceHandler resource) throws StoreException;

   /**
    * remove resource identify by its resource binding
    * 
    * @param resourceUri resource binding which have to be removed
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found for the uri
    * @throws StoreException other kind of error
    */
   @Override
   @NotNull
   ResourceStore remove(@NotNull URI resourceUri) throws StoreException;

   /**
    * @param resourceUri
    * @return does the resource exists <code>true / false</code>
    * @throws StoreException other kind of error
    */
   @Override
   boolean exists(@NotNull URI resourceUri) throws StoreException;

   /**
    * @param origin uri of the original resource
    * @param destination uri of the destination resource
    * @return current instance of the store
    * @throws ResourceNotFoundException
    * @throws StoreException
    */
   @NotNull
   ResourceStore move(@NotNull URI origin, @NotNull URI destination) throws StoreException;

   /**
    * check uri validity for the current store
    * 
    * @param resourceUri
    * @return true if uri can be handle by current store, otherwise throws an IllegalArgumentException
    * @throws IllegalArgumentException
    */
   boolean isUriManageable(@NotNull final URI resourceUri);
}
