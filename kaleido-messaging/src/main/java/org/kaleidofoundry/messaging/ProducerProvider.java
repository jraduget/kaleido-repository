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
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.util.Registry;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.jms.JmsProducer;

/**
 * @author Jerome RADUGET
 */
public class ProducerProvider extends AbstractProviderService<Producer> {

   static final Registry<String, Producer> REGISTRY = new Registry<String, Producer>();

   public ProducerProvider(Class<Producer> genericClassInterface) {
	super(genericClassInterface);
   }

   @Override
   protected Registry<String, Producer> getRegistry() {
	return REGISTRY;
   }

   public Producer provides(String name) {
	return _provides(name, null);
   }

   public Producer provides(String name, RuntimeContext<?> context) {
	Producer producer = getRegistry().get(name);
	if (producer != null) {
	   return producer;
	} else {
	   return provides(new RuntimeContext<Producer>(name, Producer.class, context));
	}
   }

   @Override
   protected Producer _provides(RuntimeContext<Producer> context) throws ProviderException {
	return _provides(context.getName(), context);
   }

   protected Producer _provides(String name, RuntimeContext<Producer> context) throws ProviderException {

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
	   return new JmsProducer(context);
	}

	// dynamic lookup into register plugin
	final Set<Plugin<Producer>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(Producer.class);

	// scan each @Declare file store implementation, to get one which handle the given implementation code
	for (final Plugin<Producer> pi : pluginImpls) {
	   final Class<? extends Producer> impl = pi.getAnnotatedClass();
	   try {

		final Declare declarePlugin = impl.getAnnotation(Declare.class);

		if (declarePlugin.value().endsWith(type)) {
		   final Constructor<? extends Producer> constructor = impl.getConstructor(RuntimeContext.class);
		   return constructor.newInstance(context);
		}

	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "RuntimeContext<Producer> context");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "RuntimeContext<Producer> context");
	   } catch (final InvocationTargetException e) {
		throw new ProviderException("context.provider.error.InvocationTargetException", e.getCause(), impl.getName(), "RuntimeContext<Producer> context",
			e.getCause().getClass().getName(), e.getMessage());
	   }
	}
	throw new ProviderException(new TransportException("messaging.producer.provider.illegal", transport.getProviderCode()));
   }

   /**
    * Stop / destroy all producers
    *  
    * @throws TransportException
    */
   public void stopAll() throws TransportException {	
	for (Producer producer : REGISTRY.values()) {
	   producer.stop();
	}
   }
}
