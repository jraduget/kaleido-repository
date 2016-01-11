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
package org.kaleidofoundry.core.context;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;

/**
 * Base class used for building {@link RuntimeContext} instance<br/>
 * You can extends it, adding your own methods like: <br/>
 * 
 * <pre>
 * class YourContextBuilder extends AbstractRuntimeContextBuilder<YourType> {
 * 	... inherit constructor part from parent
 * 
 * 	// your own method
 * 	public YourContextBuilder withParameter(String parameter) {
 * 		// handle parameter in your current context
 * 		return this;
 * 	}
 * }
 * 
 * new YourContextBuilder()
 * .withParameter(String parameterValue)
 * .withOtherParam(String otherParam)
 * .asReadOnly()
 * .build();
 * </pre>
 * 
 * @author jraduget
 * @param <T>
 */
public abstract class AbstractRuntimeContextBuilder<T> {

   /**
    * the context you wish to interact with
    */
   protected final RuntimeContext<T> context;

   public AbstractRuntimeContextBuilder() {
	this.context = new RuntimeContext<T>();
   }

   public AbstractRuntimeContextBuilder(final String name) {
	this.context = new RuntimeContext<T>(name);
   }

   public AbstractRuntimeContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	this.context = new RuntimeContext<T>(staticParameters, configurations);
   }

   public AbstractRuntimeContextBuilder(final String name, final String prefix) {
	this.context = new RuntimeContext<T>(name, prefix);
   }

   public AbstractRuntimeContextBuilder(final String name, final Configuration... configurations) {
	this.context = new RuntimeContext<T>(name, configurations);
   }

   public AbstractRuntimeContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	this.context = new RuntimeContext<T>(name, prefixProperty, configurations);
   }

   public AbstractRuntimeContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	this.context = new RuntimeContext<T>(name, prefixProperty, staticParameters, configurations);
   }

   public AbstractRuntimeContextBuilder(final Class<T> pluginInterface) {
	this.context = new RuntimeContext<T>(pluginInterface);
   }

   public AbstractRuntimeContextBuilder(final Class<T> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	this.context = new RuntimeContext<T>(pluginInterface, staticParameters);
   }

   public AbstractRuntimeContextBuilder(final Class<T> pluginInterface, final Configuration... configurations) {
	this.context = new RuntimeContext<T>(pluginInterface, configurations);
   }

   public AbstractRuntimeContextBuilder(final String name, final Class<T> pluginInterface, final Configuration... configurations) {
	this.context = new RuntimeContext<T>(name, pluginInterface, configurations);
   }

   public AbstractRuntimeContextBuilder(final String name, final Class<T> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	this.context = new RuntimeContext<T>(name, pluginInterface, configurations);
   }

   /**
    * @return static parameters set by developer, which will overrides configuration items
    * @see RuntimeContext#getParameters() protected delegate
    */
   protected ConcurrentMap<String, Serializable> getContextParameters() {
	return context.getParameters();
   }

   /**
    * @return finalize the build processing, returning the build instance (that could not be modified after return)
    */
   public final RuntimeContext<T> build() {
	// mark context as injected (no more updates will be possible)
	context.hasBeenBuildByContextBuilder = true;
	context.hasBeenInjectedByAnnotationProcessing = false;
	return context;
   }
}
