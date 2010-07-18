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
package org.kaleidofoundry.core.context.aop;

import static org.kaleidofoundry.core.util.AspectjHelper.debugJoinPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.context.InjectContext;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.context.RuntimeContextException;
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
 * <li>This aspect have to init this field, using {@link InjectContext} annnotation meta data,</li>
 * <li>When to init the field ? see {@link InjectContext#when()}</li>
 * </ol>
 * This class is used in another class field, annotated @{@link InjectContext} *
 * 
 * @author Jerome RADUGET
 */
@Aspect
public class InjectAgregatedContextAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(InjectAgregatedContextAspect.class);

   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@org.kaleidofoundry.core.context.InjectContext * *) && if()")
   public static boolean trackAgregatedRuntimeContextField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut InjectAgregatedContextAspect - trackAgregatedRuntimeContextField match");
	return true;
   }

   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @Around("trackAgregatedRuntimeContextField(jp, esjp) && @annotation(annotation)")
   public Object trackAgregatedRuntimeContextFieldToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp,
	   final ProceedingJoinPoint thisJoinPoint, final InjectContext annotation) throws Throwable {

	debugJoinPoint(LOGGER, jp);

	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   final FieldSignature annotatedFieldSignature = (FieldSignature) thisJoinPoint.getSignature();
	   final Field annotatedField = annotatedFieldSignature.getField();
	   // we need to access to its fields, in order to lookup for a RuntimeContext field
	   annotatedField.setAccessible(true);
	   final Object targetInstance = thisJoinPoint.getTarget();
	   final Object fieldToInjectInstance = targetInstance != null ? annotatedField.get(targetInstance) : null;

	   // annotated field have not yet been initialize, skip injection
	   if (targetInstance != null && fieldToInjectInstance != null) {

		// extract from field annotation, the configuration ids to use
		final String[] configurationsIdsToUse = annotation.configurations();
		Configuration[] configs = null;

		// does a RuntimeContext field have been found
		boolean targetRuntimeContextFieldFound = false;

		final Set<Field> contextFields = new LinkedHashSet<Field>();

		// add direct private field
		contextFields.addAll(ReflectionHelper.getAllDeclaredFields(fieldToInjectInstance.getClass(), Modifier.PRIVATE));

		// add other accessible field (protected, public...)
		contextFields.addAll(ReflectionHelper.getAllDeclaredFields(fieldToInjectInstance.getClass(), Modifier.PROTECTED, Modifier.PUBLIC));

		// for each RuntimeContext field, that are not annotated by @Context
		for (final Field cfield : contextFields) {

		   if (cfield.getType().isAssignableFrom(RuntimeContext.class) && !cfield.isAnnotationPresent(InjectContext.class)) {

			RuntimeContext<?> currentRuntimeContext = null;
			targetRuntimeContextFieldFound = true;
			cfield.setAccessible(true);

			currentRuntimeContext = (RuntimeContext<?>) cfield.get(fieldToInjectInstance);

			// if runtime context field have not yet been set
			if (currentRuntimeContext == null || !currentRuntimeContext.isInjected()) {

			   // create configurations to use, if it have not been done yet
			   if (configs == null) {
				configs = new Configuration[configurationsIdsToUse != null ? configurationsIdsToUse.length : 0];
				for (int i = 0; i < configs.length; i++) {
				   final Configuration config = ConfigurationFactory.getRegistry().get(configurationsIdsToUse[i]);
				   if (config != null) {
					configs[i] = config;
				   } else {
					throw new RuntimeContextException("context.annotation.illegalconfig", fieldToInjectInstance.getClass().getName(), cfield
						.getName(), configurationsIdsToUse[i]);
				   }
				}
			   }

			   // does the implementation is a declare plugin
			   final Plugin<?> interfacePlugin = PluginHelper.getInterfacePlugin(fieldToInjectInstance);

			   // create a new instance of RuntimeContext<?>, using annotation information
			   final Constructor<?> runtimeContextConstructor = cfield.getType().getDeclaredConstructor(String.class, String.class, boolean.class,
				   Configuration[].class);
			   runtimeContextConstructor.setAccessible(true);
			   final RuntimeContext<?> runtimeContext = (RuntimeContext<?>) runtimeContextConstructor.newInstance(annotation.value(),
				   interfacePlugin != null ? interfacePlugin.getName() : null, true, configs);

			   // inject new RuntimeContext<?> field instance to the target instance
			   if (!Modifier.isFinal(cfield.getModifiers())) {
				cfield.set(fieldToInjectInstance, runtimeContext);
			   }
			   // re-copy runtimeContext information to the existing one
			   else {
				if (currentRuntimeContext != null) {
				   RuntimeContext.copyFrom(runtimeContext, currentRuntimeContext);
				} else {
				   // RuntimeContext field is final && null
				   throw new RuntimeContextException("context.annotation.illegalfield", fieldToInjectInstance.getClass().getName(), cfield.getName());
				}
			   }
			}
		   }
		}

		// coherence checks
		if (!targetRuntimeContextFieldFound) { throw new RuntimeContextException("context.annotation.illegaluse.noRuntimeContextField",
			annotatedFieldSignature.getFieldType().getName() + "#" + annotatedField.getName(), InjectContext.class.getName()); }

	   }

	   return thisJoinPoint.proceed();

	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}
   }
}
