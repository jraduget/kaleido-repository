/*
 *  Copyright 2008-2014 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.kaleidofoundry.core.i18n.AbstractI18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.lang.annotation.Immutable;

/**
 * Base instance provider exception. <br/>
 * It encapsulate introspection error (like {@link NoSuchMethodException}, {@link InstantiationException}, {@link IllegalAccessException},
 * {@link InvocationTargetException})
 * 
 * @author jraduget
 */
@Immutable
public class ProviderException extends AbstractI18nRuntimeException {

   private static final long serialVersionUID = 4182831574911814722L;

   /**
    * @param code
    */
   public ProviderException(final String code) {
	super(code);
   }

   /**
    * @param code
    * @param args
    */
   public ProviderException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    */
   public ProviderException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   public ProviderException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param locale
    */
   public ProviderException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param locale
    * @param args
    */
   public ProviderException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   public ProviderException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   public ProviderException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param cause
    */
   public ProviderException(final Throwable cause) {
	super("context.provider.error.default", cause);
   }

   /**
    * @param cause
    * @param args
    */
   public ProviderException(final Throwable cause, final String... args) {
	super("context.provider.error.default", cause, args);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.i18n.AbstractI18nRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.CONTEXT.getResourceName();
   }

}
