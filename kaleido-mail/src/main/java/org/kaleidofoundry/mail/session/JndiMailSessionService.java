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
package org.kaleidofoundry.mail.session;

import javax.mail.Session;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Implémentation accès Jndi pour accéder aux service Mail et obtenir une session
 * 
 * @author Jerome RADUGET
 */
public class JndiMailSessionService implements MailSessionService {

   private MailSessionContext context;
   private JndiResourceLocator<Session> jndiLocator;

   /**
    * @param context
    * @throws MailSessionException Si problème Jndi à l'initialistion de l'IntialContext
    */
   public JndiMailSessionService(final MailSessionContext context) throws MailSessionException {
	try {
	   // Nom du contexte jndi à utiliser dans le MailSessionContext
	   final String jndiContextName = getContext().getProperty(MailSessionConstants.JndiContextNameProperty);

	   final JndiContext jndiContext = new JndiContext(jndiContextName, context);

	   // Mémorisation du context
	   this.context = context;
	   // Instanciation du jndi service locator
	   jndiLocator = new JndiResourceLocator<Session>(jndiContext);

	   // Check que le nom de la session est bien spécifée
	   final String jndiSessionName = getContext().getProperty(MailSessionConstants.JndiSessionNameProperty);

	   if (StringHelper.isEmpty(jndiSessionName)) { throw new MailSessionException("mail.session.context.property",
		   MailSessionConstants.JndiSessionNameProperty); }

	} catch (final JndiResourceException jndieInit) {
	   throw new MailSessionException("mail.session.initialcontext", jndieInit);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailSessionService#getSession()
    */
   public Session createSession() throws MailSessionException {
	if (getContext() == null) { throw new MailSessionException("mail.session.context.null"); }

	// Nom Jndi de la ressource Session
	final String jndiSessionName = getContext().getProperty(MailSessionConstants.JndiSessionNameProperty);

	// Lookup de la session
	try {
	   return jndiLocator.lookup(jndiSessionName, Session.class);
	} catch (final JndiResourceException jndielookup) {
	   throw new MailSessionException("mail.session.initialcontext.lookup", jndielookup);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailSessionService#getContext()
    */
   public MailSessionContext getContext() {
	return context;
   }

   protected void setContext(final MailSessionContext context) {
	this.context = context;
   }

   protected JndiResourceLocator<Session> getJndiServiceLocator() {
	return jndiLocator;
   }
}
