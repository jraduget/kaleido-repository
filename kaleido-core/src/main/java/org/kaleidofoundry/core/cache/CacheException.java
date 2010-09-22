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
package org.kaleidofoundry.core.cache;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * Root Cache Exception for cache handling
 * 
 * @author Jerome RADUGET
 */
public class CacheException extends I18nRuntimeException {

   private static final long serialVersionUID = -2959865376439393715L;

   public CacheException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public CacheException(final String code, final Locale locale) {
	super(code, locale);
   }

   public CacheException(final String code, final String... args) {
	super(code, args);
   }

   public CacheException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public CacheException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public CacheException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public CacheException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public CacheException(final String code) {
	super(code);
   }

   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.Cache.getResourceName();
   }

}
