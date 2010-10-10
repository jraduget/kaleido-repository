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

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect used to inject RuntimeContext<?> field that is annotated {@link Context}(...) <br/>
 * 
 * <pre>
 * class YourClass {
 * 
 *    &#064;Context(name = &quot;jndi.context&quot;)
 *    private RuntimeContext&lt;?&gt; runtimeContext;
 * 
 *    public YourClass() {           
 *          	...
 *          	System.out.println(runtimeContext.getString(&quot;...&quot;)); 
 *          	...          	
 *          }
 * }
 * </pre>
 * 
 * @author Jerome RADUGET
 */
@Aspect
public class RuntimeContextFieldInjectorAspect {

   private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeContextFieldInjectorAspect.class);

   public RuntimeContextFieldInjectorAspect() {
	LOGGER.debug("@Aspect(RuntimeContextFieldInjectorAspect) new instance");
   }

   // no need to filter on field modifier here, otherwise you can use private || !public at first get argument
   @Pointcut("get(@org.kaleidofoundry.core.context.Context org.kaleidofoundry.core.context.RuntimeContext *) && if()")
   public static boolean trackRuntimeContextField(final JoinPoint jp, final JoinPoint.EnclosingStaticPart esjp) {
	LOGGER.debug("@Pointcut(RuntimeContextFieldInjectorAspect) trackRuntimeContextField match");
	return true;
   }

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

}
