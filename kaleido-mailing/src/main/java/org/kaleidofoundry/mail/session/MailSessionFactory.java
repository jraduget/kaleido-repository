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
package org.kaleidofoundry.mail.session;

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.Registry;

/**
 * @author Jerome RADUGET
 */
public final class MailSessionFactory {

   static final Registry<String, MailSessionService> REGISTRY = new Registry<String, MailSessionService>();

   static final MailSessionProvider PROVIDER = new MailSessionProvider(MailSessionService.class);

   public static MailSessionService provides(String name) throws ProviderException {
	return PROVIDER.provides(name, new RuntimeContext<MailSessionService>(name, MailSessionService.class));
   }

   public static MailSessionService provides(String name, RuntimeContext<MailSessionService> context) throws ProviderException {
	return PROVIDER.provides(name, context);
   }

   public static final MailSessionService provides(RuntimeContext<MailSessionService> context) throws ProviderException {
	return PROVIDER.provides(context);
   }

}
