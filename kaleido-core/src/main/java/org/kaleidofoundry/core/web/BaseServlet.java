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
package org.kaleidofoundry.core.web;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.WebMessageBundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.context.ProviderService;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.util.ReflectionHelper;

/**
 * Handle context and module injection, by inherited this servlet class
 * 
 * @author Jerome RADUGET
 */
public class BaseServlet extends HttpServlet {

   private static final long serialVersionUID = 9150681853001874710L;

   /*
    * (non-Javadoc)
    * @see javax.servlet.GenericServlet#init()
    */
   @Task
   @Override
   public void init() throws ServletException {
	super.init();
	initContextInjection(this);
   }

   /**
    * @param servletInstance
    * @throws ServletException
    */
   public static <CL> void initContextInjection(final CL servletInstance) throws ServletException {
	Set<Field> fields = ReflectionHelper.getAllDeclaredFields(servletInstance.getClass());

	for (Field field : fields) {
	   final Context context = field.getAnnotation(Context.class);
	   if (field != null && context != null) {

		try {
		   // runtime context injection
		   if (field.getDeclaringClass() == RuntimeContext.class) {
			Object fieldValue = RuntimeContext.createFrom(context, field.getName(), field.getDeclaringClass());
			field.setAccessible(true);
			field.set(servletInstance, fieldValue);
		   }
		   // plugin injection
		   else {
			if (field.getType().isAnnotationPresent(Provider.class)) {

			   // create provider using annotation meta-information
			   final Provider provideInfo = field.getType().getAnnotation(Provider.class);
			   final Constructor<? extends ProviderService<?>> providerConstructor = provideInfo.value().getConstructor(Class.class);
			   final ProviderService<?> fieldProviderInstance = providerConstructor.newInstance(field.getType());

			   // invoke provides method with Context annotation meta-informations
			   final Method providesMethod = provideInfo.value().getMethod(ProviderService.PROVIDES_METHOD, Context.class, String.class, Class.class);

			   Object fieldValue = providesMethod.invoke(fieldProviderInstance, context, field.getName(), field.getType());
			   // set the field that was not yet injected
			   field.setAccessible(true);
			   field.set(servletInstance, fieldValue);
			}
		   }
		} catch (InvocationTargetException ite) {
		   throw new ServletException(WebMessageBundle.getMessage("web.baseservlet.init.error"), ite.getCause() != null ? ite.getCause()
			   : (ite.getTargetException() != null ? ite.getTargetException() : ite));
		} catch (Throwable th) {
		   throw new ServletException(WebMessageBundle.getMessage("web.baseservlet.init.error"), th);
		}
	   }
	}
   }
}
