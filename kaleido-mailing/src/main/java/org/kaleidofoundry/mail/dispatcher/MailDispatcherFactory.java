package org.kaleidofoundry.mail.dispatcher;

import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.util.Registry;

public class MailDispatcherFactory {

   static final Registry<String, MailDispatcher> REGISTRY = new Registry<String, MailDispatcher>();

   static final MailDispatcherProvider PROVIDER = new MailDispatcherProvider(MailDispatcher.class);

   public static MailDispatcher provides(String name) throws ProviderException {
	return PROVIDER.provides(name, new RuntimeContext<MailDispatcher>(name, MailDispatcher.class));
   }

   public static MailDispatcher provides(String name, RuntimeContext<MailDispatcher> context) throws ProviderException {
	return PROVIDER.provides(name, context);
   }

   public static final MailDispatcher provides(RuntimeContext<MailDispatcher> context) throws ProviderException {
	return PROVIDER.provides(context);
   }

}
