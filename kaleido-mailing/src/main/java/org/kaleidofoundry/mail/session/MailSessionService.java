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
package org.kaleidofoundry.mail.session;

import static org.kaleidofoundry.mail.MailConstants.MailSessionPluginName;

import javax.mail.Session;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Mail session service, used to send mail using smtp protocol
 * 
 * @author jraduget
 */
@Declare(value = MailSessionPluginName)
@Provider(value = MailSessionProvider.class)
public interface MailSessionService {

   /**
    * @return {@link Session} creation
    * @throws MailSessionException
    */
   Session createSession() throws MailSessionException;

}
