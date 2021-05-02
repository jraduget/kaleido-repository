/*
 *  Copyright 2008-2021 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.context.service_with_provider;

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

/**
 * provider by plugin code
 * 
 * @author jraduget
 */
public class MyServiceProvider extends AbstractProviderService<MyServiceInterface> {

   /**
    * @param genericClass
    */
   public MyServiceProvider(final Class<MyServiceInterface> genericClass) {
	super(genericClass);
   }

   @Override
   protected Registry<String, MyServiceInterface> getRegistry() {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.Provider#_provides(org.kaleidofoundry.core.context.RuntimeContext)
    */
   @Override
   public MyServiceInterface _provides(final RuntimeContext<MyServiceInterface> context) throws ProviderException {

	final Set<Plugin<MyServiceInterface>> pluginImpls = PluginFactory.getImplementationRegistry().findByInterface(MyServiceInterface.class);
	final String contextPluginCode = context.getString("pluginCode");

	if (StringHelper.isEmpty(contextPluginCode)) { throw new EmptyContextParameterException("pluginCode", context); }

	// scan each @Declare MyServiceInterface implementation, to get one which match the context plugin code
	for (final Plugin<MyServiceInterface> pi : pluginImpls) {
	   final Class<? extends MyServiceInterface> impl = pi.getAnnotatedClass();
	   try {
		final Declare declarePlugin = impl.getAnnotation(Declare.class);
		if (declarePlugin.value().equalsIgnoreCase(contextPluginCode)) {
		   final Constructor<? extends MyServiceInterface> constructor = impl.getConstructor();
		   return constructor.newInstance();
		}
	   } catch (final NoSuchMethodException e) {
		throw new ProviderException("context.provider.error.NoSuchConstructorException", impl.getName(), "");
	   } catch (final InstantiationException e) {
		throw new ProviderException("context.provider.error.InstantiationException", impl.getName(), e.getMessage());
	   } catch (final IllegalAccessException e) {
		throw new ProviderException("context.provider.error.IllegalAccessException", impl.getName(), "");
	   } catch (final InvocationTargetException e) {
		throw new ProviderException("context.provider.error.InvocationTargetException", impl.getName(), "", e.getCause().getClass().getName(),
			e.getMessage());
	   }
	}

	throw new IllegalStateException("can't found a declare plugin with plugin code : " + contextPluginCode);
   }

}
