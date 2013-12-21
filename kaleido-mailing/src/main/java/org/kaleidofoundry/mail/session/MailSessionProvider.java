/*  
 * Copyright 2008-2012 the original author or authors 
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

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.Registry;

/**
 * 
 * @author Jerome RADUGET
 *
 */
public class MailSessionProvider extends AbstractProviderService<MailSessionService> {

   public MailSessionProvider(Class<MailSessionService> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, MailSessionService> getRegistry() {
	// TODO Auto-generated method stub
	return null;
   }

   @Override
   protected MailSessionService _provides(RuntimeContext<MailSessionService> context) throws ProviderException {
	// TODO Auto-generated method stub
	return null;
   }

   
}
