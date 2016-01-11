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

import static org.kaleidofoundry.mail.MailConstants.MailSessionPluginName;
import static org.kaleidofoundry.mail.session.MailSessionContextBuilder.PROVIDER;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.util.Registry;

/**
 * @author Jerome RADUGET
 */
public class MailSessionProvider extends AbstractProviderService<MailSessionService> {

   public MailSessionProvider(Class<MailSessionService> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, MailSessionService> getRegistry() {
	return MailSessionFactory.REGISTRY;
   }

   @Override
   protected MailSessionService _provides(RuntimeContext<MailSessionService> context) throws ProviderException {
	return provides(context.getName(), context);
   }

   public MailSessionService provides(String name) {
	return provides(name, new RuntimeContext<MailSessionService>(name, MailSessionService.class));
   }

   public MailSessionService provides(@NotNull final String name, RuntimeContext<MailSessionService> context) throws ProviderException {

	MailSessionService mailSessionService = getRegistry().get(name);

	if (mailSessionService == null) {
	   return create(name, context);
	} else {
	   return mailSessionService;
	}

   }

   private MailSessionService create(@NotNull final String name, @NotNull final RuntimeContext<MailSessionService> context) throws ProviderException {

	final String provider = context.getString(PROVIDER, MailSessionProviderEnum.local.name());

	// optimization purposes
	if (MailSessionProviderEnum.local.name().equals(provider)) { return new LocalMailSessionService(context); }
	if (MailSessionProviderEnum.jndi.name().equals(provider)) { return new JndiMailSessionService(context); }

	// plugin extension mechanism
	final Set<Plugin<MailSessionService>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(MailSessionService.class);

	// scan each @Declare store implementation, to get one which handle the uri scheme
	for (final Plugin<MailSessionService> pi : pluginImpls) {
	   final Class<? extends MailSessionService> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		final String pluginName = declarePlugin.value().replace(MailSessionPluginName, "").toLowerCase();

		if (pluginName.endsWith(provider)) {
		   final Constructor<? extends MailSessionService> constructor = impl.getConstructor(RuntimeContext.class);
		   return constructor.newInstance(context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<MailSessionService> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<MailSessionService> context");
	   } catch (final InvocationTargetException e) {
		if (e.getCause() instanceof ResourceException) {
		   throw new ProviderException(e.getCause());
		} else {
		   throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(),
			   "RuntimeContext<MailSessionService> context");
		}
	   }
	}

	throw new ProviderException(new MailSessionException("mail.session.provider.illegal", provider));
   }

}
