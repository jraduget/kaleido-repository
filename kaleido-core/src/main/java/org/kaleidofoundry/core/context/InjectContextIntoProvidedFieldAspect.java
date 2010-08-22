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
package org.kaleidofoundry.core.context;

import static org.kaleidofoundry.core.util.AspectjHelper.debugJoinPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;
import org.kaleidofoundry.core.lang.annotation.Reviews;
import org.kaleidofoundry.core.plugin.Plugin;
import org.kaleidofoundry.core.plugin.PluginHelper;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context : <br/>
 * <ol>
 * <li>A class instance field is annotated {@link InjectContext},</li>
 * <li>The class of the field have a {@link RuntimeContext} field to be created (it is not annotated {@link InjectContext}),</li>
 * <li>This aspect have to init this field, using {@link InjectContext} annotation meta data,</li>
 * <li>When to init the field ? see {@link InjectContext#when()}</li>
 * </ol>
 * This class is used in another class field, annotated @{@link InjectContext} *
 * 
 * @author Jerome RADUGET
 */
@Aspect
public class InjectContextIntoProvidedFieldAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(InjectContextIntoProvidedFieldAspect.class);

   // map of @InjectContext field injected state
   private final ConcurrentMap<Field, Boolean> _$injectedFieldMap = new ConcurrentHashMap<Field, Boolean>();

   public InjectContextIntoProvidedFieldAspect() {
	LOGGER.debug("@Aspect(InjectContextAgregatedAspect) new instance");
   }

   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@org.kaleidofoundry.core.context.InjectContext !org.kaleidofoundry.core.context.RuntimeContext *) && if()")
   public static boolean trackAgregatedRuntimeContextField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut(InjectAgregatedContextAspect) trackAgregatedRuntimeContextField match");
	return true;
   }

   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @Around("trackAgregatedRuntimeContextField(jp, esjp) && @annotation(annotation)")
   @Reviews(reviews = {
	   @Review(comment = "check and handle reflection exception ", category = ReviewCategoryEnum.Improvement),
	   @Review(comment = "for provider reflection part, add static method to AbstractProvider with following code ", category = ReviewCategoryEnum.Improvement) })
   public Object trackAgregatedRuntimeContextFieldToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp,
	   final ProceedingJoinPoint thisJoinPoint, final InjectContext annotation) throws Throwable {

	debugJoinPoint(LOGGER, jp);

	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   final FieldSignature annotatedFieldSignature = (FieldSignature) thisJoinPoint.getSignature();
	   final Field annotatedField = annotatedFieldSignature.getField();
	   final Boolean annotatedFieldInjected = _$injectedFieldMap.get(annotatedField);

	   LOGGER.debug("\t<field \"{}\" injected>\t{}", annotatedField.getName(), annotatedFieldInjected == null ? Boolean.FALSE : annotatedFieldInjected
		   .booleanValue());

	   // does the field to inject have already been injected
	   if (annotatedFieldInjected == null || !annotatedFieldInjected.booleanValue()) {
		// we need to access to its fields, in order to lookup for a RuntimeContext field
		annotatedField.setAccessible(true);
		final Object targetInstance = thisJoinPoint.getTarget();
		Object fieldToInjectInstance = targetInstance != null ? annotatedField.get(targetInstance) : null;

		// process field initialize if null
		if (targetInstance != null && fieldToInjectInstance == null) {

		   // does the plugin interface have a provider specify
		   if (annotatedField.getType().isAnnotationPresent(Provider.class)) {

			// create provider using annotation meta-information
			Provider provideInfo = annotatedField.getType().getAnnotation(Provider.class);
			Constructor<? extends ProviderService<?>> providerConstructor = provideInfo.value().getConstructor(Class.class);
			ProviderService<?> fieldProviderInstance = providerConstructor.newInstance(annotatedField.getType());

			// invoke provides method with InjectContext annotation meta-informations
			Method providesMethod = provideInfo.value().getMethod("provides", InjectContext.class, Class.class);

			try {
			   fieldToInjectInstance = providesMethod.invoke(fieldProviderInstance, annotation, annotatedField.getType());
			} catch (InvocationTargetException ite) {
			   // direct runtime exception like RuntimeContextException...
			   throw ite.getCause();
			}
			// set the field that was not yet injected
			annotatedField.set(targetInstance, fieldToInjectInstance);
		   }

		}

		// if annotated field have not yet been initialize, skip injection
		if (targetInstance != null && fieldToInjectInstance != null) {

		   // does a RuntimeContext field have been found
		   boolean targetRuntimeContextFieldFound = false;

		   final Set<Field> contextFields = new LinkedHashSet<Field>();

		   // add direct private field
		   contextFields.addAll(ReflectionHelper.getAllDeclaredFields(fieldToInjectInstance.getClass(), Modifier.PRIVATE));

		   // add other accessible field (protected, public...)
		   contextFields.addAll(ReflectionHelper.getAllDeclaredFields(fieldToInjectInstance.getClass(), Modifier.PROTECTED, Modifier.PUBLIC));

		   // for each RuntimeContext field, that are not annotated by @InjectContext (to skip direct injection aspect)
		   for (final Field cfield : contextFields) {

			if (cfield.getType().isAssignableFrom(RuntimeContext.class) && !cfield.isAnnotationPresent(InjectContext.class)) {

			   RuntimeContext<?> currentRuntimeContext = null;
			   targetRuntimeContextFieldFound = true;
			   cfield.setAccessible(true);

			   currentRuntimeContext = (RuntimeContext<?>) cfield.get(fieldToInjectInstance);

			   // if runtime context field have not yet been set
			   if (currentRuntimeContext == null || !currentRuntimeContext.hasBeenInjectedByAnnotationProcessing()) {

				// does the implementation is a declare plugin
				final Plugin<?> interfacePlugin = PluginHelper.getInterfacePlugin(fieldToInjectInstance);

				// create a new instance of RuntimeContext<?>, using annotation information
				final RuntimeContext<?> newRuntimeContext = RuntimeContext.createFrom(annotation, interfacePlugin != null ? interfacePlugin
					.getAnnotatedClass() : null);

				// inject new RuntimeContext<?> field instance to the target instance
				if (!Modifier.isFinal(cfield.getModifiers())) {
				   cfield.set(fieldToInjectInstance, newRuntimeContext);
				}
				// re-copy runtimeContext information to the existing one
				else {
				   if (currentRuntimeContext != null) {
					RuntimeContext.copyFrom(newRuntimeContext, currentRuntimeContext);
				   } else {
					// RuntimeContext field is final && null
					throw new RuntimeContextException("context.annotation.illegalfield", fieldToInjectInstance.getClass().getName(), cfield.getName());
				   }
				}

				// mark instance has injected
				_$injectedFieldMap.put(annotatedField, Boolean.TRUE);
			   }
			}
		   }

		   // coherence checks
		   if (!targetRuntimeContextFieldFound) { throw new RuntimeContextException("context.annotation.illegaluse.noRuntimeContextField",
			   annotatedFieldSignature.getFieldType().getName() + "#" + annotatedField.getName(), InjectContext.class.getName()); }

		}
	   }

	   return thisJoinPoint.proceed();

	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}

   }

}
