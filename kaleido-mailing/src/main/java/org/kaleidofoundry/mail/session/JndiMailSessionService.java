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
package org.kaleidofoundry.mail.session;

import static org.kaleidofoundry.mail.MailConstants.JndiMailSessionPluginName;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.JNDI_NAMING_SERVICE_NAME;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.JNDI_NAMING_SERVICE_REF;

import javax.mail.Session;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.naming.NamingService;
import org.kaleidofoundry.core.naming.NamingServiceFactory;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * JNDI mail session provider
 * 
 * @author jraduget
 */
@Declare(value = JndiMailSessionPluginName)
public class JndiMailSessionService implements MailSessionService {

   protected final RuntimeContext<MailSessionService> context;

   protected final NamingService namingService;

   public JndiMailSessionService(RuntimeContext<MailSessionService> context) {

	this.context = context;

	if (StringHelper.isEmpty(context.getString(JNDI_NAMING_SERVICE_NAME))) { throw new EmptyContextParameterException(JNDI_NAMING_SERVICE_NAME, context); }

	final RuntimeContext<NamingService> namingServiceContext = new RuntimeContext<NamingService>(context.getString(JNDI_NAMING_SERVICE_REF),
		NamingService.class, context);
	this.namingService = NamingServiceFactory.provides(namingServiceContext);

   }

   @Override
   public Session createSession() {
	return namingService.locate(context.getString(JNDI_NAMING_SERVICE_NAME), Session.class);
   }

}
