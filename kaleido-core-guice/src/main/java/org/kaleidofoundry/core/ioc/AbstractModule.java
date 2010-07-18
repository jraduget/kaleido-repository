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
package org.kaleidofoundry.core.ioc;

import static org.kaleidofoundry.core.plugin.PluginHelper.getPluginName;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginFactory;
import org.kaleidofoundry.core.plugin.PluginImplementationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * This module is used to bind an interface (generic T type) to its multiple implementation, using guice mecanism<br>
 * This class extends guice module, using kaleido plugin mecanism<br/>
 * <p>
 * The default and unnamed T implementation is computed using {@link #getUnnamedImplementation()} <br/>
 * The guice {@link Named} binding use internal plugin registry to bind plugin name to implementation <br/>
 * <br/>
 * You can custom binding, overriding {@link #configure()} method. By this way you can add binding for your custom guice binding annotation.
 * For more informations : http://code.google.com/p/google-guice/wiki/BindingAnnotations
 * </p>
 * 
 * @author Jerome RADUGET
 * @param <T>
 */
// TODO providers for RuntimeContext
public abstract class AbstractModule<T> extends com.google.inject.AbstractModule {

   static final Logger LOGGER = LoggerFactory.getLogger(AbstractModule.class);

   // class of generic type T
   private final Class<T> annotatedInterface;

   /**
    * 
    */
   @SuppressWarnings("unchecked")
   protected AbstractModule() {
	annotatedInterface = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
   }

   /**
    * @return default unnamed implementation to use if name is not qualified with {@link Named}
    */
   public abstract Class<? extends T> getUnnamedImplementation();

   /*
    * (non-Javadoc)
    * @see com.google.inject.AbstractModule#configure()
    */
   @Override
   protected void configure() {

	// ** bindings ***************************************************************************************************

	// 1. bind default and unnamed ResourceStore implementation
	bind(annotatedInterface).to(getUnnamedImplementation()).in(scope(getUnnamedImplementation()));

	// 2. bind standard kaleido implementation first
	Set<Class<? extends T>> standardImpls = implementations(true);

	// bind with Names.named("...")
	for (Class<? extends T> standard : standardImpls) {
	   bind(annotatedInterface).annotatedWith(Names.named(getPluginName(standard))).to(standard).in(scope(standard));
	}

	// 3. bind custom non kaleido implementation after
	Set<Class<? extends T>> customImpls = implementations(false);

	for (Class<? extends T> custom : customImpls) {
	   bind(annotatedInterface).annotatedWith(Names.named(getPluginName(custom))).to(custom).in(scope(custom));
	}

	// ** interceptor ************************************************************************************************

	// interceptor for constructors ****
	// bindInterceptor(ConstructorInterceptorà doesn't exists (use ProvisionInterception in guice 2.1)
	// ContextInjectionConstructorInterceptor constructorContextInterceptor = new ContextInjectionConstructorInterceptor();
	// requestInjection(constructorContextInterceptor);
	// bindInterceptor(Matchers.any(), Matchers.annotatedWith(Context.class), constructorContextInterceptor);

	// interceptor for fields ****
	// -> doesn't exists we use TypeListener (ContextTypeListener) instead

	// interceptor for methods ****
	MethodInterceptor methodContextInterceptor = new ContextInjectionMethodInterceptor();
	requestInjection(methodContextInterceptor);
	bindInterceptor(Matchers.any(), Matchers.annotatedWith(Inject.class), methodContextInterceptor);

	// ** listeners **************************************************************************************************

	// listeners only for fields....
	bindListener(Matchers.any(), new ContextTypeListener());

   }

   /**
    * @param standard does we search kaleido standard implementation using <code>true</code> or not
    * @return collection of standards implementations
    */

   public Set<Class<? extends T>> implementations(final boolean standard) {

	PluginImplementationRegistry pluginImplRegistry = PluginFactory.getImplementationRegistry();
	Set<Plugin<T>> storePluginImpls = pluginImplRegistry.findByInterface(annotatedInterface);
	Set<Class<? extends T>> result = new LinkedHashSet<Class<? extends T>>();

	for (Plugin<T> pi : storePluginImpls) {
	   if (pi.isStandard() == standard) {
		result.add(pi.getAnnotatedClass());
	   }
	}
	return result;
   }

   /**
    * @param c
    * @return if class argument annotated with {@link Declare}, return the guice {@link Scope} that maps
    *         {@link Declare#singleton()} <br/>
    *         otherwise return {@link Scopes#NO_SCOPE}
    */
   public Scope scope(final Class<? extends T> c) {

	Declare declarePlugin = c.getAnnotation(Declare.class);

	if (declarePlugin != null) {
	   return declarePlugin.singleton() ? Scopes.SINGLETON : Scopes.NO_SCOPE;
	} else {
	   return Scopes.NO_SCOPE;
	}
   }

}
