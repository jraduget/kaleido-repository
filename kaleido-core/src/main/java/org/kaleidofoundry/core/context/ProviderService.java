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
package org.kaleidofoundry.core.context;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Provider service interface that will create instance T, given a {@link RuntimeContext} or {@link Context}
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
@ThreadSafe
public interface ProviderService<T> {

   /**
    * @param context runtime context
    * @return the new instance that will be created using {@link RuntimeContext} information
    * @throws ProviderException
    */
   T provides(RuntimeContext<T> context) throws ProviderException;

   /**
    * @param context inject context annotation
    * @param defaultName the context default name if context annotation does not provide one
    * @param genericClassInterface class of the interface for which this provider will create instance
    * @return the new instance
    * @throws ProviderException
    */
   T provides(Context context, String defaultName, Class<T> genericClassInterface) throws ProviderException;
}
