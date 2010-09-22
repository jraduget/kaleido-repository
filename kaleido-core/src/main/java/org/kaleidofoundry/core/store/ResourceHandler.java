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

import java.io.InputStream;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Represents a client resource binding<br/>
 * Once {@link ResourceStore} client have get its resource, he have to free it, by calling {@link #release()}<br/>
 * You'd better use {@link ResourceHandler} locally or with {@link ThreadLocal}, because instance will not be thread
 * safe (it handles an {@link InputStream})<br/>
 * 
 * @author Jerome RADUGET
 */
@NotThreadSafe
public interface ResourceHandler {

   /**
    * @return input stream of the resource
    */
   @NotNull
   InputStream getInputStream();

   /**
    * release resource and the eventual connections
    */
   void release();
}
