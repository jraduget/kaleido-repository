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
package org.kaleidofoundry.mail;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;

/**
 * MailSessionException
 * 
 * @author Jerome RADUGET
 */
public class MailException extends I18nException {

   private static final long serialVersionUID = 8604765471487464677L;

   public MailException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public MailException(final String code, final Locale locale) {
	super(code, locale);
   }

   public MailException(final String code, final String... args) {
	super(code, args);
   }

   public MailException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public MailException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public MailException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public MailException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public MailException(final String code) {
	super(code);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.exception.I18nCodedException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return MailConstants.I18nRessource;
   }

}
