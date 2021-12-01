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
package org.kaleidofoundry.messaging;

import java.util.Locale;

/**
 * @author jraduget
 */
public class MessageException extends MessagingException {

   private static final long serialVersionUID = 5636264633325001707L;

   public MessageException(String code, Locale locale, String... parameters) {
	super(code, locale, parameters);
   }

   public MessageException(String code, Locale locale) {
	super(code, locale);
   }

   public MessageException(String code, String... parameters) {
	super(code, parameters);
   }

   public MessageException(String code, Throwable cause, Locale locale, String... parameters) {
	super(code, cause, locale, parameters);
   }

   public MessageException(String code, Throwable cause, Locale locale) {
	super(code, cause, locale);
   }

   public MessageException(String code, Throwable cause, String... parameters) {
	super(code, cause, parameters);
   }

   public MessageException(String code, Throwable cause) {
	super(code, cause);
   }

   public MessageException(String code) {
	super(code);
   }

}
