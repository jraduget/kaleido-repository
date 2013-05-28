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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * A file resource handler<br/>
 * Once {@link FileStore} client had get a ({@link ResourceHandler}), you have to free it, by calling {@link #close()} <br/>
 * Be careful, if you use {@link ResourceHandler#getInputStream()} : this class is not thread safe. Use a {@link ThreadLocal} to do this. <br/>
 * 
 * @author Jerome RADUGET
 */
@NotThreadSafe
public interface ResourceHandler extends Serializable {

   /**
    * Get the file resource {@link URI}, identifier of the resource
    * 
    * @return uri of the identifier of the file content resource
    */
   String getUri();

   /**
    * Get the local path of the file resource
    * 
    * @return
    */
   String getPath();

   /**
    * Get an input stream to read the content of the resource<br/>
    * You can use {@link BufferedInputStream} to handle it.<br/>
    * Once done, free resource with {@link #close()}<br/>
    * Be careful, if you call several time this method, the same inputStream instance will be return.
    * 
    * @return input stream of the resource
    */
   @NotNull
   InputStream getInputStream() throws ResourceException;

   /**
    * Get a reader used to read the content of the resource<br/>
    * You can use {@link BufferedReader} to handle it.<br/>
    * Once done, free resource with {@link #close()}
    * Be careful, if you call several time this method, the same inputStream instance will be return.
    * 
    * @return input stream of the resource
    */
   @NotNull
   Reader getReader() throws ResourceException;

   /**
    * Get a reader used to read the content of the resource<br/>
    * You can use {@link BufferedReader} to handle it.<br/>
    * Once done, free resource with {@link #close()}
    * Be careful, if you call several time this method, the same inputStream instance will be return.
    * 
    * @param charset The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
    * @return input stream of the resource
    */
   @NotNull
   Reader getReader(String charset) throws ResourceException;

   /**
    * Get the full text representation of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #close()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getReader()} with {@link BufferedReader} method.
    * 
    * @return text of the resource
    * @throws ResourceException
    */
   @NotNull
   String getText() throws ResourceException;

   /**
    * Get the full text representation of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #close()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getReader()} with {@link BufferedReader} method.
    * 
    * @param charset The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
    * @return text of the resource
    * @throws ResourceException
    */
   @NotNull
   String getText(String charset) throws ResourceException;

   /**
    * Get the full binary data of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #close()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getInputStream()} with {@link BufferedInputStream} method.
    * 
    * @return text of the resource
    * @throws ResourceException
    */
   @NotNull
   byte[] getBytes() throws ResourceException;

   /**
    * @return resource content length (-1, if it can't be obtained)
    */
   long getLength();

   /**
    * Release the resource and the eventual connections
    */
   void close();

   /**
    * @return Time in milliseconds, when the resource has been modified. It will be set to 0 if the information can't be provided.
    */
   long getLastModified();

   /**
    * @return the mime type of the resource. It will be set to null if the information can't be provided
    */
   String getMimeType();

   /**
    * @return the charset of the resource. By default it will be the {@link FileStore} default charset
    */
   String getCharset();

   /**
    * @return do the resource have been closed
    */
   boolean isClosed();

   /**
    * @return do this resource have data
    */
   boolean isEmpty();

}
