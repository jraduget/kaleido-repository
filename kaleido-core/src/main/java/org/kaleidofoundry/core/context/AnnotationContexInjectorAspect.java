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
 * This aspect is used to inject {@link RuntimeContext} on a field annotated by {@link Context}
 * <p>
 * <b>Usage 1 :</b><br/>
 * <ol>
 * <li>A class field {@link RuntimeContext} is annotated {@link Context},</li>
 * <li>This aspect have to init this field, using {@link Context} annotation meta data,</li>
 * <li>See method poincut {@link #trackRuntimeContextField(JoinPoint, org.aspectj.lang.JoinPoint.EnclosingStaticPart)}</li>
 * <li>Example :
 * 
 * <pre>
 * public class MyClass {
 * 
 *    &#064;Context(name = &quot;jndi.context&quot;)
 *    private RuntimeContext&lt;?&gt; context; // no need to instantiate it
 * 
 *    public MyClass() {           
 *          	...
 *          	// you can use context field in your constructor
 *          	System.out.println(context.getString(&quot;...&quot;)); 
 *          	...          	
 *          }
 * }
 * </pre>
 * 
 * </li>
 * </ol>
 * </p>
 * <p>
 * <b>Usage 2 :</b><br/>
 * <ol>
 * <li>A class field is annotated {@link Context} (and this field is not a {@link RuntimeContext} instance),</li>
 * <li>The field class have a {@link RuntimeContext} field (which is not annotated {@link Context}),</li>
 * <li>This aspect have to init this field, using {@link Context} annotation meta data,</li>
 * <li>Example :
 * 
 * <pre>
 * public class MyControler {
 * 	
 * 	&#064;Context(name = &quot;myService.context&quot;)
 * 	private MyService myService; // no need to instantiate it
 * 
 * 	public void processing(...) {
 * 		// use myService with the given context
 * 		myService.echo();
 * 		...
 * 	}
 * }
 * 
 * public class MyService {
 * 
 *    private RuntimeContext&lt;MySingleService&gt; context;
 * 
 *    public MySingleService() {
 * 		...
 * 		// you can use context field in your constructor
 * 	System.out.println(context.getString(&quot;...&quot;)); 
 *          ...     
 * 	}
 * 
 * 	public void echo() {
 * 		return contex.toString();
 * 	}
 * 
 * }
 * 
 * 
 * </pre>
 * 
 * </li>
 * </ol>
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Aspect
public class AnnotationContexInjectorAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationContexInjectorAspect.class);

   // map of @Context field injected state, used by complex RuntimeContext agregation
   private final ConcurrentMap<Field, Boolean> _$injectedFieldMap = new ConcurrentHashMap<Field, Boolean>();

   /**
    * 
    */
   public AnnotationContexInjectorAspect() {
	LOGGER.debug("@Aspect({}) new instance", getClass().getName());
   }

   // **************************************************************************************************************************************
   // Usage 1 : simple RuntimeContext injection
   // **************************************************************************************************************************************

   /**
    * @param jp
    * @param esjp
    * @return <code>true / false</code> to enable poincut
    */
   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@org.kaleidofoundry.core.context.Context org.kaleidofoundry.core.context.RuntimeContext *) && if()")
   public static boolean trackRuntimeContextField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut({}) trackRuntimeContextField match", AnnotationContexInjectorAspect.class.getName());
	return true;
   }

   /**
    * @param jp
    * @param esjp
    * @param thisJoinPoint
    * @param annotation
    * @return <code>true / false</code> to enable poincut
    * @throws Throwable
    */
   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @SuppressWarnings("unchecked")
   @Around("trackRuntimeContextField(jp, esjp) && @annotation(annotation)")
   public Object trackRuntimeContextFieldToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp, final ProceedingJoinPoint thisJoinPoint,
	   final Context annotation) throws Throwable {
	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   final FieldSignature fs = (FieldSignature) thisJoinPoint.getSignature();
	   final Object target = thisJoinPoint.getTarget();
	   final Field field = fs.getField();
	   field.setAccessible(true);
	   final Object currentValue = field.get(target);
	   if (currentValue == null) {
		final RuntimeContext<?> runtimeContext = RuntimeContext.createFrom(annotation, fs.getFieldType());
		field.set(target, runtimeContext);
		return runtimeContext;
	   } else {
		return thisJoinPoint.proceed();
	   }
	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}
   }

   // **************************************************************************************************************************************
   // Usage 2 : complex RuntimeContext injection
   // **************************************************************************************************************************************

   /**
    * @param jp
    * @param esjp
    * @return <code>true / false</code> to enable poincut
    */
   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@org.kaleidofoundry.core.context.Context !org.kaleidofoundry.core.context.RuntimeContext *) && if()")
   public static boolean trackAgregatedRuntimeContextField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut({}) trackAgregatedRuntimeContextField match", AnnotationContexInjectorAspect.class.getName());
	return true;
   }

   /**
    * @param jp
    * @param esjp
    * @param thisJoinPoint
    * @param annotation
    * @return <code>true / false</code> if join point have been processed
    * @throws Throwable
    */
   // track field with ProceedingJoinPoint and annotation information with @annotation(annotation)
   @Around("trackAgregatedRuntimeContextField(jp, esjp) && @annotation(annotation)")
   @Reviews(reviews = {
	   @Review(comment = "check and handle reflection exception ", category = ReviewCategoryEnum.Improvement),
	   @Review(comment = "for provider reflection part, add static method to AbstractProvider with following code ", category = ReviewCategoryEnum.Improvement) })
   public Object trackAgregatedRuntimeContextFieldToInject(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp,
	   final ProceedingJoinPoint thisJoinPoint, final Context annotation) throws Throwable {

	debugJoinPoint(LOGGER, jp);

	if (thisJoinPoint.getSignature() instanceof FieldSignature) {
	   final FieldSignature annotatedFieldSignature = (FieldSignature) thisJoinPoint.getSignature();
	   final Field annotatedField = annotatedFieldSignature.getField();
	   final Boolean annotatedFieldInjected = _$injectedFieldMap.get(annotatedField);

	   LOGGER.debug("\t<joinpoint.field.{}.injected>\t{}", annotatedField.getName(),
		   annotatedFieldInjected == null ? Boolean.FALSE : annotatedFieldInjected.booleanValue());

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
			final Provider provideInfo = annotatedField.getType().getAnnotation(Provider.class);
			final Constructor<? extends ProviderService<?>> providerConstructor = provideInfo.value().getConstructor(Class.class);
			final ProviderService<?> fieldProviderInstance = providerConstructor.newInstance(annotatedField.getType());

			// invoke provides method with Context annotation meta-informations
			final Method providesMethod = provideInfo.value().getMethod("provides", Context.class, Class.class);

			try {
			   fieldToInjectInstance = providesMethod.invoke(fieldProviderInstance, annotation, annotatedField.getType());
			} catch (final InvocationTargetException ite) {
			   // direct runtime exception like ContextException...
			   throw ite.getCause() != null ? ite.getCause() : (ite.getTargetException() != null ? ite.getTargetException() : ite);
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

		   // for each RuntimeContext field, that are not annotated by @Context (to skip direct injection aspect)
		   for (final Field cfield : contextFields) {

			if (cfield.getType().isAssignableFrom(RuntimeContext.class) && !cfield.isAnnotationPresent(Context.class)) {

			   RuntimeContext<?> currentRuntimeContext = null;
			   targetRuntimeContextFieldFound = true;
			   cfield.setAccessible(true);

			   currentRuntimeContext = (RuntimeContext<?>) cfield.get(fieldToInjectInstance);

			   // if runtime context field have not yet been set
			   if (currentRuntimeContext == null || !currentRuntimeContext.hasBeenInjectedByAnnotationProcessing()) {

				// does the implementation is a declare plugin
				final Plugin<?> interfacePlugin = PluginHelper.getInterfacePlugin(fieldToInjectInstance);

				// create a new instance of RuntimeContext<?>, using annotation information
				final RuntimeContext<?> newRuntimeContext = RuntimeContext.createFrom(annotation,
					interfacePlugin != null ? interfacePlugin.getAnnotatedClass() : null);

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
					throw new ContextException("context.annotation.illegalfield", fieldToInjectInstance.getClass().getName(), cfield.getName());
				   }
				}

				// mark instance has injected
				_$injectedFieldMap.put(annotatedField, Boolean.TRUE);
			   }
			}
		   }

		   // coherence checks
		   if (!targetRuntimeContextFieldFound) { throw new ContextException("context.annotation.illegaluse.noRuntimeContextField", annotatedFieldSignature
			   .getFieldType().getName() + "#" + annotatedField.getName(), Context.class.getName()); }

		}
	   }

	   return thisJoinPoint.proceed();

	} else {
	   throw new IllegalStateException("aspect advise handle only field, please check your pointcut");
	}

   }

}
