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

import static org.kaleidofoundry.core.store.ResourceStoreConstants.ResourceStorePluginName;

import java.net.URI;
import java.net.URL;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Stateless;
import org.kaleidofoundry.core.plugin.Declare;

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
@Declare(ResourceStorePluginName)
@Provider(ResourceStoreProvider.class)
public interface ResourceStore extends Store<URI, ResourceHandler> {

   /**
    * get a given resource <br/>
    * 
    * @param resourceUri resource binding informations to access the resource<br/>
    * @return resource input stream to get its content
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws ResourceException other kind of error
    */
   @Override
   @NotNull
   ResourceHandler get(@NotNull URI resourceUri) throws ResourceException;

   /**
    * store updates on current R instance<br/>
    * 
    * @param resourceUri resource uri which have to be store
    * @param resource resource input stream to store
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws ResourceException other kind of error
    */
   @Override
   @NotNull
   ResourceStore store(@NotNull URI resourceUri, @NotNull ResourceHandler resource) throws ResourceException;

   /**
    * remove resource identify by its resource binding
    * 
    * @param resourceUri resource binding which have to be removed
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found for the uri
    * @throws ResourceException other kind of error
    */
   @Override
   @NotNull
   ResourceStore remove(@NotNull URI resourceUri) throws ResourceException;

   /**
    * @param resourceUri
    * @return does the resource exists <code>true / false</code>
    * @throws ResourceException other kind of error
    */
   @Override
   boolean exists(@NotNull URI resourceUri) throws ResourceException;

   /**
    * @param origin uri of the original resource
    * @param destination uri of the destination resource
    * @return current instance of the store
    * @throws ResourceNotFoundException
    * @throws ResourceException
    */
   @NotNull
   ResourceStore move(@NotNull URI origin, @NotNull URI destination) throws ResourceException;

   /**
    * check uri validity for the current store
    * 
    * @param resourceUri
    * @return true if uri can be handle by current store, otherwise throws an IllegalArgumentException
    * @throws IllegalArgumentException
    */
   boolean isUriManageable(@NotNull final URI resourceUri);
}
