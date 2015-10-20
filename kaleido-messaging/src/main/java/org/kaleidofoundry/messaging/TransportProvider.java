/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.messaging;

import static org.kaleidofoundry.messaging.TransportContextBuilder.TRANSPORT_PROVIDER;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.ObjectHelper;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.messaging.jms.JmsTransport;

/**
 * @author jraduget
 */
public class TransportProvider extends AbstractProviderService<Transport> {

   static final Registry<String, Transport> REGISTRY = new Registry<String, Transport>();

   public TransportProvider(Class<Transport> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, Transport> getRegistry() {
	return REGISTRY;
   }

   public Transport provides(String name) {
	return _provides(null, name, null);
   }

   public Transport provides(String providerCode, String name) {
	return _provides(providerCode, name, null);
   }

   public Transport provides(String name, RuntimeContext<?> context) {
	Transport transport = getRegistry().get(name);
	if (transport != null) {
	   return transport;
	} else {
	   return provides(new RuntimeContext<Transport>(name, Transport.class, context));
	}
   }

   @Override
   protected Transport _provides(RuntimeContext<Transport> context) throws ProviderException {
	String providerCode = context.getString(TRANSPORT_PROVIDER);
	return _provides(providerCode, context.getName(), context);
   }

   protected Transport _provides(String providerCode, String name, RuntimeContext<Transport> context) throws ProviderException {

	String type = ObjectHelper.firstNonNull(providerCode, context.getString(TRANSPORT_PROVIDER, TransportProviderEnum.jms.getCode())).toLowerCase();

	// only for optimization reasons
	if (type.equalsIgnoreCase(TransportProviderEnum.jms.name())) {
	   try {
		return new JmsTransport(context);
	   } catch (TransportException te) {
		throw new ProviderException(te);
	   }
	}

	// dynamic lookup into register plugin
	final Set<Plugin<Transport>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Transport.class);

	// scan each @Declare file store implementation, to get one which handle the given implementation code
	for (final Plugin<Transport> pi : pluginImpls) {
	   final Class<? extends Transport> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (declarePlugin.value().endsWith(type)) {
		   final Constructor<? extends Transport> constructor = impl.getConstructor(RuntimeContext.class);
		   return constructor.newInstance(context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<Transport> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<Transport> context");
	   } catch (final InvocationTargetException e) {
		throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(), "RuntimeContext<Transport> context",
			e.getCause().getClass().getName(), e.getMessage());
	   }
	}
	throw new ProviderException(new TransportException("messaging.transport.provider.illegal", providerCode));
   }

   /**
    * Close all transport with the associated consumers / producers
    * 
    * @throws TransportException
    */
   public void closeAll() throws TransportException {
	for (Transport transport : REGISTRY.values()) {
	   transport.close();
	}
   }

}
