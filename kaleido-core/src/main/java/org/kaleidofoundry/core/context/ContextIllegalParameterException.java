/*
 *  Copyright 2008-2010 the original author or authors.
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

import java.util.Locale;

/**
 * @author Jerome RADUGET
 */
public class ContextIllegalParameterException extends ContextException {

   private static final long serialVersionUID = -7106275164578192921L;

   /**
    * following context parameter have causes error
    * @param parameter
    * @param parameterValue
    * @param context
    * @param error
    */
   public ContextIllegalParameterException(String parameter, String parameterValue, RuntimeContext<?> context, Throwable error ) {
	super("context.parameter.causes.error", parameter, parameterValue, context.getName(), error.getMessage());
   }
   
   /**
    * @param code
    * @param locale
    * @param args
    */
   ContextIllegalParameterException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
    ContextIllegalParameterException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
    ContextIllegalParameterException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
    ContextIllegalParameterException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
    ContextIllegalParameterException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
    ContextIllegalParameterException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
    ContextIllegalParameterException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
    ContextIllegalParameterException(final String code) {
	super(code);
   }

    
}
