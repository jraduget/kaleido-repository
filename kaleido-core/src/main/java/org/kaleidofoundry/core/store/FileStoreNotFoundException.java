/*  
 * Copyright 2008-2014 the original author or authors 
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

import org.kaleidofoundry.core.i18n.AbstractI18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * @author jraduget
 */
public class FileStoreNotFoundException extends AbstractI18nRuntimeException {

   /**
    * 
    */
   private static final long serialVersionUID = 7034217214321691961L;

   /**
    * @param code
    */
   public FileStoreNotFoundException(String storeName) {
	super("store.notfound", storeName);
   }

   /**
    * @param resourceId
    * @param locale
    */
   public FileStoreNotFoundException(final String storeName, final Locale locale) {
	super("store.resource.notfound", locale, storeName);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.AbstractI18nRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.STORE.getResourceName();
   }

}
