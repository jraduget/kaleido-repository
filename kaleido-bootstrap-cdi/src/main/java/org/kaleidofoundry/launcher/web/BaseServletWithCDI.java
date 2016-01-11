/*
 *  Copyright 2008-2016 the original author or authors.
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
package org.kaleidofoundry.launcher.web;

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.WebMessageBundle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.web.BaseServlet;
import org.kaleidofoundry.launcher.UnmanagedCdiInjector;

/**
 * Use it when you want to use @Inject CDI in a servlet unmanaged env.
 * 
 * @author jraduget
 */
public class BaseServletWithCDI extends BaseServlet {

   private static final long serialVersionUID = 4266751670162044385L;

   /*
    * (non-Javadoc)
    * @see javax.servlet.GenericServlet#init()
    */
   @Override
   public void init() throws ServletException {

	// kaleido context injection
	super.init();

	// CDI injection
	initCdiInjection(this);
   }

   /**
    * @param cl
    * @return CDI beans accessor
    */
   protected <T> T getBean(final Class<T> type, final Annotation... annotations) {
	return UnmanagedCdiInjector.getBean(type, annotations);
   }

   /**
    * CDI injection
    * 
    * @param servletInstance
    * @throws ServletException
    */
   public static <CL> void initCdiInjection(final CL servletInstance) throws ServletException {
	Set<Field> fields = ReflectionHelper.getAllDeclaredFields(servletInstance.getClass());
	for (Field field : fields) {
	   if (field != null && field.isAnnotationPresent(Inject.class) && !field.isAnnotationPresent(Context.class)) {
		Object fieldValue = UnmanagedCdiInjector.getBean(field.getType());
		try {
		   // set the field that was not yet injected
		   field.setAccessible(true);
		   field.set(servletInstance, fieldValue);
		} catch (Throwable th) {
		   throw new ServletException(WebMessageBundle.getMessage("web.baseservlet.init.error"), th);
		}
	   }
	}
   }
}
