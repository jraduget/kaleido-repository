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

import org.kaleidofoundry.core.lang.CodedException;

/**
 * <b>I18n Coded exception</b><br/>
 * <p>
 * An i18n coded exception is used to identified precisely an exception by a code and a i18n message.<br/>
 * The exception code is bind to an i18n message using {@link I18nMessagesFactory}, and user specific {@link Locale}<br/>
 * <br/>
 * This exception can be extended and used in your service layer error, web service layer error (fault code...)
 * </p>
 * 
 * @author Jerome RADUGET
 * @see I18nMessagesFactory
 */
public abstract class I18nException extends CodedException {

   private static final long serialVersionUID = 6623119392420767038L;

   private final Locale locale; // user locale if specified
   private final String args[]; // arguments to pass to the message : "user {0} is disconnect"

   /**
    * @param code i18n code of the exception
    */
   public I18nException(final String code) {
	this(code, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param args
    */
   public I18nException(final String code, final String... args) {
	this(code, (Locale) null, args);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    */
   public I18nException(final String code, final Throwable cause) {
	this(code, cause, null, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    * @param args message tokens arguments
    */
   public I18nException(final String code, final Throwable cause, final String... args) {
	this(code, cause, null, args);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    */
   public I18nException(final String code, final Locale locale) {
	this(code, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    * @param args token value to replace
    */
   public I18nException(final String code, final Locale locale, final String... args) {
	super(code, null);
	this.args = args;
	this.locale = locale;
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    */
   public I18nException(final String code, final Throwable cause, final Locale locale) {
	this(code, cause, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    * @param args message tokens arguments
    */
   public I18nException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, null, cause);
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
    * @return specified locale to use to get i18n exception message
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