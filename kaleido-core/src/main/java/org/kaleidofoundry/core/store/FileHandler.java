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
import java.io.InputStreamReader;
import java.net.URI;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * A file resource binding<br/>
 * Once {@link FileStore} client have get its resource ({@link FileHandler}), he have to free it, by calling {@link #release()}<br/>
 * You'd better use {@link FileHandler} locally or with {@link ThreadLocal}, because instance will not be thread
 * safe (it handles an {@link InputStream})<br/>
 * 
 * @author Jerome RADUGET
 */
@NotThreadSafe
public interface FileHandler {

   /**
    * Get the identifier of the file content resource {@link URI}
    * 
    * @return uri of the identifier of the file content resource
    */
   String getResourceUri();

   /**
    * Get the input stream to get the content of the resource<br/>
    * You can use {@link BufferedInputStream} to handle it.<br/>
    * Once done, free resource with {@link #release()}
    * 
    * @return input stream of the resource
    */
   @NotNull
   InputStream getInputStream();

   /**
    * Get the input stream reader to get the content of the resource<br/>
    * You can use {@link BufferedReader} to handle it.<br/>
    * Once done, free resource with {@link #release()}
    * 
    * @return input stream of the resource
    */
   @NotNull
   InputStreamReader getInputStreamReader() throws StoreException;

   /**
    * Get the input stream reader to get the content of the resource<br/>
    * You can use {@link BufferedReader} to handle it.<br/>
    * Once done, free resource with {@link #release()}
    * 
    * @param charset The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
    * @return input stream of the resource
    */
   @NotNull
   InputStreamReader getInputStreamReader(String charset) throws StoreException;

   /**
    * Get the full text representation of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #release()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getInputStreamReader()} with {@link BufferedReader} method.
    * 
    * @return text of the resource
    * @throws StoreException
    */
   @NotNull
   String getText() throws StoreException;

   /**
    * Get the full text representation of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #release()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getInputStreamReader()} with {@link BufferedReader} method.
    * 
    * @param charset The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
    * @return text of the resource
    * @throws StoreException
    */
   @NotNull
   String getText(String charset) throws StoreException;

   /**
    * Get the full binary data of the resource<br/>
    * <br/>
    * When you have used this method, the {@link #getInputStream()} result will no more be available for a next call.<br/>
    * For this reason, {@link #release()} method is automatically called at the end of this method. <br/>
    * <br/>
    * <b>Be careful, for huge resource, you should use :</b> {@link #getInputStream()} with {@link BufferedInputStream} method.
    * 
    * @return text of the resource
    * @throws StoreException
    */
   @NotNull
   byte[] getBytes() throws StoreException;

   /**
    * Release the resource and the eventual connections
    */
   void release();

}
