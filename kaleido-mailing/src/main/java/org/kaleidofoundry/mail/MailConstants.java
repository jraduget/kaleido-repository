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
package org.kaleidofoundry.mail;

import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;

/**
 * @author jraduget
 */
public interface MailConstants {

   /** Mail message bundle */
   I18nMessages MailMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.MAIL.getResourceName(), InternalBundleHelper.CoreMessageBundle);

   String MailSessionPluginName = "mailing.sessions";

   String LocalMailSessionPluginName = "mailing.sessions.local";

   String JndiMailSessionPluginName = "mailing.sessions.jndi";

   String MailDispatcherPluginName = "mailing.dispatchers";

   String MessagingMailDispatcherPluginName = "mailing.dispatchers.messaging";

   String AsynchronousMailDispatcherPluginName = "mailing.dispatchers.async";

   String SynchronousMailDispatcherPluginName = "mailing.dispatchers.sync";

}
