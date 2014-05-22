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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.MailDispatcherPluginName;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

   Logger LOGGER = LoggerFactory.getLogger(MailDispatcher.class);

   /**
    * @return create a new mail message
    */
   MailMessage createMessage();

   /**
    * send the message
    * 
    * @param message
    * @throws MailException problem around the mail message (invalid address...)
    * @throws MailDispatcherException problem around the dispatching of the mail message
    */
   void send(MailMessage message) throws MailException, MailDispatcherException;

}
