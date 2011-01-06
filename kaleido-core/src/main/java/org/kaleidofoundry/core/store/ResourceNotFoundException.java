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

import java.util.Locale;

/**
 * @author Jerome RADUGET
 */
public class ResourceNotFoundException extends StoreException {

   private static final long serialVersionUID = -3825066742298741103L;

   /**
    * @param resourceId
    * @param locale
    */
   public ResourceNotFoundException(final String resourceId, final Locale locale) {
	super("store.resource.notfound", locale, resourceId);
   }

   /**
    * @param resourceId
    */
   public ResourceNotFoundException(final String resourceId) {
	super("store.resource.notfound", resourceId);
   }

   /**
    * @param resourceId
    * @param cause
    * @param locale
    */
   public ResourceNotFoundException(final String resourceId, final Throwable cause, final Locale locale) {
	super("store.resource.notfound", cause, locale, resourceId);
   }

   /**
    * @param resourceId
    * @param cause
    */
   public ResourceNotFoundException(final String resourceId, final Throwable cause) {
	super("store.resource.notfound", cause, resourceId);
   }

}
