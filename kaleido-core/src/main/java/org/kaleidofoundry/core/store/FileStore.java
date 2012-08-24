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

import static org.kaleidofoundry.core.store.FileStoreConstants.FileStorePluginName;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Stateless;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * It provides a file store interface to handle file content like :
 * <code>properties, xml datas, binary content (pdf report, email...) </code> <br/>
 * which need to be persist on a file storage. The storage type will be determining by the scheme of the URI
 * <p>
 * The {@link URI} (Uniform Resource Identifier) allow to bind different kind of resource :<br/>
 * You can use classic {@link URL} like :
 * <ul>
 * <li><code>http://</code></li>
 * <li><code>https://</code></li>
 * <li><code>ftp://</code></li>
 * <li><code>file:/</code></li>
 * <li><code>classpath:/</code></li>
 * <li><code>webapp:/</code></li>
 * <li><code>jpa:/</code></li>
 * <li><code>memory:/</code></li>
 * <li><code>sftp://</code> - not yet implemented</li>
 * </ul>
 * <p>
 * <b>More you can extends it</b>, using other kind of "custom user" protocol <code>(jdbc://, ...)</code>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Stateless
@Declare(FileStorePluginName)
@Provider(value = FileStoreProvider.class, singletons = true)
public interface FileStore extends Store<String, ResourceHandler> {

   /**
    * @return root (base) path URI
    */
   @NotNull
   String getBaseUri();

   /**
    * Get a given resource <br/>
    * 
    * @param resourceRelativePath relative resource path (relative from the file store root uri)
    * @return resource input stream to get its content
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws ResourceException other kind of error
    * @throws IllegalArgumentException if resourceRelativePath parameter is invalid - see ({@link URI#create(String)})
    */
   @Override
   @NotNull
   ResourceHandler get(@NotNull String resourceRelativePath) throws ResourceNotFoundException, ResourceException;

   /**
    * Stores the given resource and its contents
    * 
    * @param resourceRelativePath relative resource path (relative from the file store root uri)
    * @param resourceContent
    * @return current instance of the store
    * @throws ResourceException
    */
   @NotNull
   FileStore store(@NotNull String resourceRelativePath, @NotNull InputStream resourceContent) throws ResourceException;

   /**
    * Stores the given resource and its contents
    * 
    * @param resourceRelativePath relative resource path (relative from the file store root uri)
    * @param resourceContent
    * @return current instance of the store
    * @throws ResourceException
    */
   @NotNull
   FileStore store(@NotNull String resourceRelativePath, @NotNull byte[] resourceContent) throws ResourceException;

   /**
    * @param resourceRelativePath relative resource path (relative from the file store root uri)
    * @param resourceContent
    * @return current instance of the store
    * @throws ResourceException
    * @throws UnsupportedEncodingException
    */
   @NotNull
   FileStore store(@NotNull String resourceRelativePath, @NotNull String resourceContent) throws ResourceException, UnsupportedEncodingException;

   /**
    * Store updates on current R instance<br/>
    * 
    * @param resource resource input stream to store
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found, instead of returning null
    * @throws ResourceException other kind of error
    * @throws IllegalArgumentException if resourceRelativePath parameter is invalid - see ({@link URI#create(String)})
    */
   @Override
   @NotNull
   FileStore store(@NotNull ResourceHandler resource) throws ResourceException;

   /**
    * Remove resource identify by its resource binding
    * 
    * @param resourceRelativePath relative resource path (relative from the store root uri)
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found for the uri
    * @throws ResourceException other kind of error
    * @throws IllegalArgumentException if resourceRelativePath parameter is invalid - see ({@link URI#create(String)})
    */
   @Override
   @NotNull
   FileStore remove(@NotNull String resourceRelativePath) throws ResourceNotFoundException, ResourceException;

   /**
    * Does a given resource exists ?
    * 
    * @param resourceRelativePath relative resource path (relative from the store root uri)
    * @return does the resource exists <code>true / false</code>
    * @throws ResourceException other kind of error
    * @throws IllegalArgumentException if resourceRelativePath parameter is invalid - see ({@link URI#create(String)})
    */
   @Override
   boolean exists(@NotNull String resourceRelativePath) throws ResourceException;

   /**
    * Move a resource from a destination to another
    * 
    * @param origin relative original resource path (relative from the store root uri)
    * @param destination relative destination resource path (relative from the store root uri)
    * @return current instance of the store
    * @throws ResourceNotFoundException if resource can't be found for the uri
    * @throws ResourceException
    * @throws IllegalArgumentException if resourceRelativePath parameter is invalid - see ({@link URI#create(String)})
    */
   @NotNull
   FileStore move(@NotNull String origin, @NotNull String destination) throws ResourceNotFoundException, ResourceException;

   /**
    * Check the uri validity for the current store
    * 
    * @param resourceUri
    * @return true if uri can be handle by current store, otherwise throws an IllegalArgumentException
    * @throws IllegalArgumentException is resourceRelativePath is not handle by the store
    */
   boolean isUriManageable(@NotNull final String resourceUri);

   /**
    * @return does this store is for read-only use
    */
   boolean isReadOnly();

   /**
    * @param resourceUri
    * @param input
    * @return new resource handler
    */
   ResourceHandler createResourceHandler(final String resourceUri, final InputStream input);

   /**
    * @param resourceUri
    * @param content
    * @return new resource handler
    * @throws UnsupportedEncodingException
    */
   ResourceHandler createResourceHandler(final String resourceUri, final String content) throws UnsupportedEncodingException;

   /**
    * close all opened resources, that have not been closed using {@link ResourceHandler#close()} <br/>
    * <b>the store is still functional after calling this method</b>
    * 
    * @return current instance of the store
    */
   FileStore closeAll();

}
