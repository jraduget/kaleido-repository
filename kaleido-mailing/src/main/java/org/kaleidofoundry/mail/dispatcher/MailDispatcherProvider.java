/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.mail.dispatcher;

import static org.kaleidofoundry.mail.MailConstants.MailDispatcherPluginName;
import static org.kaleidofoundry.mail.dispatcher.MailDispatcherContextBuilder.*;

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
import org.kaleidofoundry.mail.session.MailSessionException;

/**
 * @author Jerome RADUGET
 */
public class MailDispatcherProvider extends AbstractProviderService<MailDispatcher> {

   public MailDispatcherProvider(Class<MailDispatcher> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, MailDispatcher> getRegistry() {
	return MailDispatcherFactory.REGISTRY;
   }

   @Override
   protected MailDispatcher _provides(RuntimeContext<MailDispatcher> context) throws ProviderException {
	return provides(context.getName(), context);
   }

   public MailDispatcher provides(String name) {
	return provides(name, new RuntimeContext<MailDispatcher>(name, MailDispatcher.class));
   }

   public MailDispatcher provides(@NotNull final String name, RuntimeContext<MailDispatcher> context) throws ProviderException {

	MailDispatcher mailDispatcher = getRegistry().get(name);

	if (mailDispatcher == null) {
	   return create(name, context);
	} else {
	   return mailDispatcher;
	}

   }

   private MailDispatcher create(@NotNull final String name, @NotNull final RuntimeContext<MailDispatcher> context) throws ProviderException {

	final String provider = context.getString(PROVIDER, MailDispatcherEnum.sync.name());

	// optimization purposes
	if (MailDispatcherEnum.sync.name().equals(provider)) { return new SynchronousMailDispatcher(context); }
	if (MailDispatcherEnum.async.name().equals(provider)) { return new AsynchronousMailDispatcher(context); }

	// plugin extension mechanism
	final Set<Plugin<MailDispatcher>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(MailDispatcher.class);

	// scan each @Declare store implementation, to get one which handle the uri scheme
	for (final Plugin<MailDispatcher> pi : pluginImpls) {
	   final Class<? extends MailDispatcher> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);
		final String pluginName = declarePlugin.value().replace(MailDispatcherPluginName, "").toLowerCase();

		if (pluginName.endsWith(provider)) {
		   final Constructor<? extends MailDispatcher> constructor = impl.getConstructor(RuntimeContext.class);
		   return constructor.newInstance(context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<MailDispatcher> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<MailDispatcher> context");
	   } catch (final InvocationTargetException e) {
		if (e.getCause() instanceof ResourceException) {
		   throw new ProviderException(e.getCause());
		} else {
		   throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(),
			   "RuntimeContext<MailDispatcher> context");
		}
	   }
	}

	throw new ProviderException(new MailSessionException("mail.session.provider.illegal", provider));
   }

}
