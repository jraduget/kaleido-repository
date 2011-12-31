/*
 *  Copyright 2008-2011 the original author or authors.
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
package org.kaleidofoundry.core.context.spring;

import java.lang.reflect.Field;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * BeanPostProcessor used to inject {@link RuntimeContext} information to a spring bean <br/>
 * <br/>
 * This bean, could be injected :
 * <ul>
 * <li>using annotations {@link Autowired}, Inject,</li>
 * <li>using spring application context, with a bean declared in its xml descriptor</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public class BeanContextPostProcessor implements MergedBeanDefinitionPostProcessor {

   @Override
   public Object postProcessAfterInitialization(final Object beanInstance, final String beanName) throws BeansException {
	return beanInstance;
   }

   @Override
   public Object postProcessBeforeInitialization(final Object beanInstance, final String beanName) throws BeansException {

	Field[] fields = beanInstance.getClass().getDeclaredFields();

	for (Field field : fields) {
	   final Context context = field.getAnnotation(Context.class);
	   if (context != null) {
		if (beanInstance instanceof RuntimeContext) {
		   return RuntimeContext.createFrom(context, field.getName(), field.getDeclaringClass());
		} else {
		   // inject context into the current @Context field...
		   // same way as AnnotationContextInjectorAspect
		   // using
		   // ReflectionUtils.makeAccessible(field);
		   // ReflectionUtils.setField(field, beanInstance, ...);
		   // beanInstance = ...

		}

	   }
	}

	return beanInstance;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public void postProcessMergedBeanDefinition(final RootBeanDefinition beanDefinition, final Class beanClass, final String beanName) {
   }

}
