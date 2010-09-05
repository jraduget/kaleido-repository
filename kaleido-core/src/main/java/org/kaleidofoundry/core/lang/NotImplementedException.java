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
package org.kaleidofoundry.core.lang;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.lang.annotation.NotImplemented;

/**
 * Throws this exception when portion of code will never be implemented<br/>
 * You can use annotation {@link NotImplemented} on constructor / method / class , it will automated this behaviors without any code
 * 
 * @see NotImplemented
 * @author Jerome RADUGET
 */
public class NotImplementedException extends I18nRuntimeException {

   private static final long serialVersionUID = 732204899702062693L;

   public final static String ERROR_NotImplemented = "exception.notimplemented";
   public final static String ERROR_NotImplementedCustom = "exception.notimplemented.custom";

   /**
   *
   */
   public NotImplementedException() {
	super(ERROR_NotImplemented);
   }

   /**
    * @param locale
    */
   public NotImplementedException(final Locale locale) {
	super(ERROR_NotImplemented, locale);
   }

   /**
    * @param customMessage
    */
   public NotImplementedException(final String customMessage) {
	super(ERROR_NotImplementedCustom, customMessage);
   }

   /**
    * @param locale
    * @param customMessage
    */
   public NotImplementedException(final Locale locale, final String customMessage) {
	super(ERROR_NotImplementedCustom, locale, customMessage);
   }

   // following is needed by child class, keep package qualifier

   NotImplementedException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   NotImplementedException(final String code, final Locale locale) {
	super(code, locale);
   }

   NotImplementedException(final String code, final String... args) {
	super(code, args);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.I18nRuntimeException#getI18nBundleName()
    */
   @Override
   public final String getI18nBundleName() {
	return InternalBundleEnum.Core.getResourceName();
   }
}
