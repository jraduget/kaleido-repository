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

/**
 * <b>I18n exception base implementation</b><br/>
 * <p>
 * An i18n exception is used to identified precisely an exception by a code and a i18n message.<br/>
 * The exception code is bind to an i18n message using {@link I18nMessagesFactory}, and user specific {@link Locale}<br/>
 * <br/>
 * This exception can be extended and used in your service layer error, web service layer error (fault code...)
 * </p>
 * 
 * @author Jerome RADUGET
 * @see I18nMessagesFactory
 */
public abstract class AbstractI18nException extends Exception implements I18nException {

   private static final long serialVersionUID = 6623119392420767038L;

   private final String code;
   private final Locale locale; // user locale if specified
   private final String parameters[]; // parameters to pass to the message : "user {0} is disconnect"

   /**
    * @param code i18n code of the exception
    */
   public AbstractI18nException(final String code) {
	this(code, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param parameters
    */
   public AbstractI18nException(final String code, final String... parameters) {
	this(code, (Locale) null, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    */
   public AbstractI18nException(final String code, final Throwable cause) {
	this(code, cause, null, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    * @param parameters message tokens arguments
    */
   public AbstractI18nException(final String code, final Throwable cause, final String... parameters) {
	this(code, cause, null, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    */
   public AbstractI18nException(final String code, final Locale locale) {
	this(code, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    * @param parameters token value to replace
    */
   public AbstractI18nException(final String code, final Locale locale, final String... parameters) {
	this(code, null, locale, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    */
   public AbstractI18nException(final String code, final Throwable cause, final Locale locale) {
	this(code, cause, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    * @param parameters message tokens arguments
    */
   public AbstractI18nException(final String code, final Throwable cause, final Locale locale, final String... parameters) {
	super(code, cause);
	this.code = code;
	this.parameters = parameters;
	this.locale = locale;
   }

   @Override
   public String getCode() {
	return code;
   }

   /**
    * @return Token parameters passed for the template message
    */
   public String[] getParameters() {
	return parameters;
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
	   if (getParameters() != null) {
		return msgB.getMessage(getCode(), (Object[]) getParameters());
	   } else {
		return msgB.getMessage(getCode());
	   }
	} else {
	   return getCode();
	}
   }
}