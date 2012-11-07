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
package org.kaleidofoundry.messaging;

import static org.kaleidofoundry.messaging.ClientContextBuilder.TRANSPORT_REF;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.kaleidofoundry.core.context.AbstractProviderService;
import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.ProviderException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.jms.JmsConsumer;

/**
 * @author Jerome RADUGET
 */
public class ConsumerProvider extends AbstractProviderService<Consumer> {

   static final Registry<String, Consumer> REGISTRY = new Registry<String, Consumer>();

   public ConsumerProvider(Class<Consumer> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, Consumer> getRegistry() {
	return REGISTRY;
   }

   public Consumer provides(String name) {
	return _provides(name, null);
   }

   public Consumer provides(String name, RuntimeContext<?> context) {
	Consumer consumer = getRegistry().get(name);
	if (consumer != null) {
	   return consumer;
	} else {
	   return provides(new RuntimeContext<Consumer>(name, Consumer.class, context));
	}
   }

   @Override
   protected Consumer _provides(RuntimeContext<Consumer> context) throws ProviderException {
	return _provides(context.getName(), context);
   }

   protected Consumer _provides(String name, RuntimeContext<Consumer> context) throws ProviderException {

	// consumer transport
	String transportRef = context.getString(TRANSPORT_REF);
	Transport transport;
	
	if (!StringHelper.isEmpty(transportRef)) {
	   transport = TransportFactory.provides(transportRef, context);
	} else {
	   throw new EmptyContextParameterException(TRANSPORT_REF, context);
	}
	
	String type = transport.getProviderCode();

	// only for optimization reasons
	if (type.equalsIgnoreCase(TransportProviderEnum.jms.name())) {
	   return new JmsConsumer(context);
	}

	// dynamic lookup into register plugin
	final Set<Plugin<Consumer>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Consumer.class);

	// scan each @Declare file store implementation, to get one which handle the given implementation code
	for (final Plugin<Consumer> pi : pluginImpls) {
	   final Class<? extends Consumer> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (declarePlugin.value().endsWith(type)) {
		   final Constructor<? extends Consumer> constructor = impl.getConstructor(RuntimeContext.class);
		   return constructor.newInstance(context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<Consumer> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<Consumer> context");
	   } catch (final InvocationTargetException e) {
		throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(), "RuntimeContext<Consumer> context",
			e.getCause().getClass().getName(), e.getMessage());
	   }
	}
	throw new ProviderException(new TransportException("messaging.consumer.provider.illegal", transport.getProviderCode()));
   }

   /**
    * Stop / destroy all consumers
    *  
    * @throws TransportException
    */
   public void stopAll() throws TransportException {	
	for (Consumer consumer : REGISTRY.values()) {
	   consumer.stop();
	}
   }
}
