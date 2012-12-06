package org.kaleidofoundry.launcher.web;
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


import static org.kaleidofoundry.core.i18n.InternalBundleHelper.WebMessageBundle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.web.StartupListener;
import org.kaleidofoundry.launcher.UnmanagedCdiInjector;

/**
 * Startup listener which support standalone CDI (unmanaged) 
 *
 * @see StartupListener
 * @author Jerome RADUGET
 */
public class StartupListenerWithCdi extends StartupListener {

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(final ServletContextEvent sce) {
	super.contextInitialized(sce);
	// CDI weld start
	UnmanagedCdiInjector.init(getClass(), initializer);
	// init @Inject bean
	initCdiInjection();
   }

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(final ServletContextEvent sce) {
	UnmanagedCdiInjector.shutdown();
	super.contextDestroyed(sce);
   }

   /**
    * @param type
    * @param annotations
    * @return
    */
   public <T> T getBean(final Class<T> type, final Annotation... annotations) {
	return UnmanagedCdiInjector.getBean(type, annotations);
   }
   
   /**
    * CDI injection
    * 
    * @param servletInstance
    * @throws ServletException
    */
   void initCdiInjection()  {
	Set<Field> fields = ReflectionHelper.getAllDeclaredFields(this.getClass());
	for (Field field : fields) {
	   if (field != null && field.isAnnotationPresent(Inject.class) && !field.isAnnotationPresent(Context.class)) {
		Object fieldValue = getBean(field.getType());
		try {
		   // set the field that was not yet injected
		   field.setAccessible(true);
		   field.set(this, fieldValue);
		} catch (Throwable th) {		   
		   throw new IllegalStateException(WebMessageBundle.getMessage("web.context.init.error"), th);
		}
	   }
	}
   }
}