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
