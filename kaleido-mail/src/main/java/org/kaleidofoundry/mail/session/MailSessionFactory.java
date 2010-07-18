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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * MailSessionFactory - Fournisseur de Session
 * 
 * @author Jerome RADUGET
 */
public abstract class MailSessionFactory {

   /**
    * @param context 
    * @return Session pour pouvoir créer un Message
    * @throws MailSessionException
    */
   public static MailSessionService createMailSessionService(final MailSessionContext context) throws MailSessionException {
	MailSessionService mailService = null;

	for (final MailSessionEnum mailSessionEnum : MailSessionEnum.getEnums()) {

	   final MailSessionEnum impl = mailSessionEnum;

	   if (impl.getKey().equals(context.getType())) {

		final Class<? extends MailSessionService> mailSessionClass = impl.getImplementationClass();

		if (mailSessionClass != null) {

		   try {
			final Constructor<? extends MailSessionService> cons = mailSessionClass.getConstructor(new Class[] { MailSessionContext.class });
			mailService = cons.newInstance(new Object[] { context });

		   } catch (final NoSuchMethodException nme) {
			// getConstructor
			throw new MailSessionException("mail.session.factory.constructor", nme, mailSessionClass.getName());
		   } catch (final IllegalArgumentException iae) {
			// newInstance
			throw new MailSessionException("mail.session.factory.constructor", iae, mailSessionClass.getName());
		   } catch (final IllegalAccessException iae) {
			// newInstance
			throw new MailSessionException("mail.session.factory.constructor", iae, mailSessionClass.getName());
		   } catch (final InvocationTargetException ite) {
			// newInstance
			if (ite.getTargetException() instanceof MailSessionException)
			   throw (MailSessionException) ite.getTargetException();
			else
			   throw new MailSessionException("mail.session.factory.constructor.init", ite.getTargetException(), mailSessionClass.getName());
		   } catch (final InstantiationException ie) {
			// newInstance
			throw new MailSessionException("mail.session.factory.constructor.init", ie, mailSessionClass.getName());
		   } catch (final Throwable th) {
			// ?
			throw new MailSessionException("mail.session.factory.constructor.init", th, mailSessionClass.getName());
		   }

		   break;
		}
	   }
	}

	if (mailService == null) throw new MailSessionException("mail.session.context.illegal");

	return mailService;
   }

}
