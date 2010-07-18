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
package org.kaleidofoundry.mail.sender;

import java.util.Locale;

import org.kaleidofoundry.mail.MailException;

/**
 * MailSender Exception
 * 
 * @author Jerome RADUGET
 */
public class MailSenderException extends MailException {

   private static final long serialVersionUID = -4072499048280078160L;

   public MailSenderException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public MailSenderException(final String code, final Locale locale) {
	super(code, locale);
   }

   public MailSenderException(final String code, final String... args) {
	super(code, args);
   }

   public MailSenderException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public MailSenderException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public MailSenderException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public MailSenderException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public MailSenderException(final String code) {
	super(code);
   }

}
