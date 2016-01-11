/*  
 * Copyright 2008-2016 the original author or authors 
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
package org.kaleidofoundry.mail.dispatcher;

import java.io.IOException;

import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailingDispatcherReport;

/**
 * Mail dispatcher exception
 * 
 * @author jraduget
 */
public class MailDispatcherException extends MailException {

   private static final long serialVersionUID = -4072499048280078160L;

   private final MailingDispatcherReport report;

   public MailDispatcherException(MailingDispatcherReport report) {
	super("mail.service.send.error");
	this.report = report;
   }

   public MailDispatcherException(final String code, final String... args) {
	super(code, args);
	this.report = null;
   }

   public MailDispatcherException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
	this.report = null;
   }

   public MailDispatcherException(final String code, final Throwable cause) {
	super(code, cause);
	this.report = null;
   }

   public MailDispatcherException(final String code) {
	super(code);
	this.report = null;
   }

   public MailingDispatcherReport getReport() {
	return report;
   }

   public static MailDispatcherException ioReadMailAttachmentException(String attachmentName, IOException ioe) {
	return new MailDispatcherException("mail.service.message.attachment.ioread.error", ioe, attachmentName);
   }
}
