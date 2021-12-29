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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.MailDispatcherPluginName;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.session.MailSessionException;

/**
 * <p>
 * Mail sender service
 * </p>
 * 
 * @author jraduget
 */
@Declare(MailDispatcherPluginName)
@Provider(value = MailDispatcherProvider.class)
public interface MailDispatcher {

   /**
    * Send the given messages in a same mail session.
    * 
    * @param messages
    * @throws MailSessionException problem around the creation of the mail session
    * @throws MailDispatcherException problem with some mail message (illegal addresses, ...)
    */
   void send(@NotNull MailMessage... messages) throws MailSessionException, MailDispatcherException;

   /**
    * Send the given messages in a same mail session.<br/>
    * Errors while sending a mail are processed by the message error handler, in argument of the method
    * 
    * @param handler error handler if an error occurred sending an email
    * @param messages
    */
   void send(@NotNull MailMessageErrorHandler handler, @NotNull MailMessage... messages);

   /**
    * Close properly all the resources used by the mail dispatcher, waiting the processing of the mails being sent.
    * Then, the call of this method will give back the hand to the execution of the current thread. The call of this method is blocking.
    * 
    * @throws Exception
    */
   void close() throws Exception;
}
