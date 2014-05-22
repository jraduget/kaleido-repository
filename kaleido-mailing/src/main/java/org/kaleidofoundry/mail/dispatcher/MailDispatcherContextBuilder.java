package org.kaleidofoundry.mail.dispatcher;

import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.mail.session.MailSessionService;

/**
 * @author jraduget
 */
public class MailDispatcherContextBuilder extends AbstractRuntimeContextBuilder<MailDispatcher> {

   /** {@link MailSessionService} name */
   public static final String MAILSESSION_SERVICE_REF = "mailSession.service-ref";

   /** EJB service name */
   public static final String EJB_SERVICE_NAME = "namingService.name";
   /** EJB naming service reference */
   public static final String EJB_NAMING_SERVICE_REF = "namingService.service-ref";

   /** Producer service name */
   public static final String PRODUCER_SERVICE_NAME = "producer.service-ref";

   /**
    * @param mailSessionServiceRef
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withMailSessionServiceRef(final String mailSessionServiceRef) {
	getContextParameters().put(MAILSESSION_SERVICE_REF, mailSessionServiceRef);
	return this;
   }

   /**
    * @param ejbServiceName
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withEjbServiceName(final String ejbServiceName) {
	getContextParameters().put(EJB_SERVICE_NAME, ejbServiceName);
	return this;
   }

   /**
    * @param namingServiceRef
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withNamingServiceRef(final String namingServiceRef) {
	getContextParameters().put(EJB_NAMING_SERVICE_REF, namingServiceRef);
	return this;
   }
}
