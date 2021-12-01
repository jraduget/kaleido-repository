package org.kaleidofoundry.mail.dispatcher;

import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.mail.session.MailSessionProviderEnum;
import org.kaleidofoundry.mail.session.MailSessionService;
import org.kaleidofoundry.messaging.Producer;

/**
 * @author jraduget
 */
public class MailDispatcherContextBuilder extends AbstractRuntimeContextBuilder<MailDispatcher> {

   /** Transport type : see {@link MailSessionProviderEnum} */
   public static final String PROVIDER = "provider";

   /** {@link MailSessionService} name */
   public static final String MAILSESSION_SERVICE_REF = "session.service-ref";

   /** {@link AsynchronousMailMessagingDispatcher} {@link Producer} */
   public static final String PRODUCER_SERVICE_NAME = "producer.service-ref";

   /** {@link AsynchronousMailDispatcher} {@link Producer} thread count */
   public static final String THREAD_COUNT = "threadCount";

   /** {@link AsynchronousMailDispatcher} send timeout in millisecond */
   public static final String TIMEOUT = "timeout";

   /** EJB service name */
   public static final String EJB_SERVICE_NAME = "namingService.name";

   /** EJB naming service reference */
   public static final String EJB_NAMING_SERVICE_REF = "namingService.service-ref";

   /**
    * @param provider
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withProvider(final String provider) {
	getContextParameters().put(PROVIDER, provider);
	return this;
   }

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

   /**
    * @param threadCount
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withThreadCount(final Integer threadCount) {
	getContextParameters().put(THREAD_COUNT, threadCount);
	return this;
   }

   /**
    * @param serviceName
    * @return current builder instance
    */
   public MailDispatcherContextBuilder withProducerServiceName(final String serviceName) {
	getContextParameters().put(PRODUCER_SERVICE_NAME, serviceName);
	return this;
   }
}
