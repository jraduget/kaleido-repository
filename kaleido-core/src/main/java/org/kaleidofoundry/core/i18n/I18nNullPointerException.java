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
package org.kaleidofoundry.core.i18n;

import java.util.Locale;

import org.kaleidofoundry.core.lang.CodedNullPointerException;

/**
 * I18n NullPointer Exception<br/>
 * I18nNullPointerException is normally not handle, and propagate through the layers.<br/>
 * 
 * @author Jerome RADUGET
 */
public abstract class I18nNullPointerException extends CodedNullPointerException {

   private static final long serialVersionUID = 6623119392420767038L;

   private final Locale locale; // user locale if specified
   private final String args[]; // arguments to pass to the message : "user {0} is disconnect"

   /**
    * @param code domain code of the exception
    */
   public I18nNullPointerException(final String code) {
	this(code, null, (String[]) null);
   }

   /**
    * @param code
    * @param args
    */
   public I18nNullPointerException(final String code, final String... args) {
	this(code, null, args);
   }

   /**
    * @param code domain code of the exception
    * @param locale user locale
    */
   public I18nNullPointerException(final String code, final Locale locale) {
	this(code, locale, (String[]) null);
   }

   /**
    * @param code code of the exception
    * @param locale user locale
    * @param args token value to replace
    */
   public I18nNullPointerException(final String code, final Locale locale, final String... args) {
	super(code, null);
	this.args = args;
	this.locale = locale;
   }

   /**
    * @return Token arguments passed for the template message
    */
   public String[] getArgs() {
	return args;
   }

   /**
    * @return exception specified locale (for i18n message)
    */
   public Locale getLocale() {
	return locale;
   }

   /**
    * @return Message resource bundle
    */
   protected I18nMessages getMessages() {
	// locale is specified
	if (getLocale() != null) {
	   return I18nMessagesFactory.provides(getI18nBundleName(), getLocale());
	}
	// no locale : default user / server locale compute by I18nMessagesFactory
	else {
	   return I18nMessagesFactory.provides(getI18nBundleName());
	}
   }

   /**
    * @return I18 resourceBundle name (i18n/exceptions , i18n/messages, messages, ...)
    */
   public abstract String getI18nBundleName();

   /**
    * @return override default message value
    */
   @Override
   public String getMessage() {
	final I18nMessages msgB = getMessages();
	if (msgB != null) {
	   if (getArgs() != null) {
		return msgB.getMessage(getCode(), (Object[]) getArgs());
	   } else {
		return msgB.getMessage(getCode());
	   }
	} else {
	   return getCode();
	}
   }
}
