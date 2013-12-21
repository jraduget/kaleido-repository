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

import java.util.Locale;

/**
 * @author jraduget
 */
public class IllegalContextParameterException extends ContextException {

   private static final long serialVersionUID = -7106275164578192921L;

   /**
    * context parameter are invalid
    * 
    * @param parameter
    * @param parameterValue
    * @param context
    * @param reason
    */
   public IllegalContextParameterException(final String parameter, final String parameterValue, final RuntimeContext<?> context, final String reason) {
	super("context.parameter.illegal.use", parameter, parameterValue, context.getName(), reason);
   }

   /**
    * context parameter have causes some error
    * 
    * @param parameter
    * @param parameterValue
    * @param context
    * @param error
    */
   public IllegalContextParameterException(final String parameter, final String parameterValue, final RuntimeContext<?> context, final Throwable error ) {
	super("context.parameter.causes.error", parameter, parameterValue, context.getName(), error.getMessage());
   }

   /**
    * @param code
    * @param locale
    * @param args
    */
   IllegalContextParameterException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
   IllegalContextParameterException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
   IllegalContextParameterException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   IllegalContextParameterException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   IllegalContextParameterException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   IllegalContextParameterException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
   IllegalContextParameterException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
   IllegalContextParameterException(final String code) {
	super(code);
   }


}
