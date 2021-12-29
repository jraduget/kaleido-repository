/*  
 * Copyright 2008-2021 the original author or authors 
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

import java.io.IOException;
import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.lang.annotation.Immutable;

/**
 * Ancestor exception for store module
 * 
 * @author jraduget
 */
@Immutable
public class ResourceException extends IOException implements I18nException {

   private static final long serialVersionUID = 980939390919586472L;

   private final String code;
   private final Locale locale; // user locale if specified
   private final String parameters[]; // parameters to pass to the message : "user {0} is disconnect"

   /**
    * @param code i18n code of the exception
    */
   public ResourceException(final String code) {
	this(code, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param parameters
    */
   public ResourceException(final String code, final String... parameters) {
	this(code, (Locale) null, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    */
   public ResourceException(final String code, final Throwable cause) {
	this(code, cause, null, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause
    * @param parameters message tokens arguments
    */
   public ResourceException(final String code, final Throwable cause, final String... parameters) {
	this(code, cause, null, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    */
   public ResourceException(final String code, final Locale locale) {
	this(code, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param locale user locale
    * @param parameters token value to replace
    */
   public ResourceException(final String code, final Locale locale, final String... parameters) {
	this(code, null, locale, parameters);
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    */
   public ResourceException(final String code, final Throwable cause, final Locale locale) {
	this(code, cause, locale, (String[]) null);
   }

   /**
    * @param code i18n code of the exception
    * @param cause exception cause
    * @param locale user locale
    * @param parameters message tokens arguments
    */
   public ResourceException(final String code, final Throwable cause, final Locale locale, final String... parameters) {
	super(code, cause);
	this.code = code;
	this.parameters = parameters;
	this.locale = locale;
   }

   /**
    * @param ioe
    * @param resourceUri
    */
   public ResourceException(final IOException ioe, final String resourceUri) {
	this("store.unexpeceted.ioe", ioe, resourceUri);
   }

   /**
    * @return the code
    */
   public String getCode() {
	return code;
   }

   /**
    * @return the locale
    */
   public Locale getLocale() {
	return locale;
   }

   /**
    * @return the parameters
    */
   public String[] getParameters() {
	return parameters;
   }

   public String getI18nBundleName() {
	return InternalBundleEnum.STORE.getResourceName();
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
