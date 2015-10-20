/*  
 * Copyright 2008-2014 the original author or authors 
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

import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;

/**
 * Throws this exception when portion of code is not yet implemented<br/>
 * You can use annotation {@link NotImplemented} on constructor / method / class , it will automated this behaviors without any code
 * 
 * @author jraduget
 * @see NotYetImplemented
 */
public class NotYetImplementedException extends NotImplementedException {

   private static final long serialVersionUID = 5485178075125195470L;

   public final static String ERROR_NotYetImplemented = "exception.notyetimplemented";
   public final static String ERROR_NotYetImplementedCustom = "exception.notyetimplemented.custom";

   /**
    *
    */
   public NotYetImplementedException() {
	super(ERROR_NotYetImplemented, (Locale) null);
   }

   /**
    * @param locale
    */
   public NotYetImplementedException(final Locale locale) {
	super(ERROR_NotYetImplemented, locale);
   }

   /**
    * @param customMessage
    */
   public NotYetImplementedException(final String customMessage) {
	super(ERROR_NotYetImplementedCustom, customMessage);
   }

   /**
    * @param locale
    * @param customMessage
    */
   public NotYetImplementedException(final Locale locale, final String customMessage) {
	super(ERROR_NotYetImplementedCustom, locale, customMessage);
   }

}
