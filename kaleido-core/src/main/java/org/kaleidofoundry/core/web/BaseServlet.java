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

import java.lang.reflect.Field;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.kaleidofoundry.core.context.Context;
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

	Set<Field> fields = ReflectionHelper.getAllDeclaredFields(getClass());
	Object fieldValue = null;

	for (Field field : fields) {
	   final Context context = field.getAnnotation(Context.class);
	   if (field != null && context != null) {

		try {
		   // runtime context injection
		   if (field.getDeclaringClass() == RuntimeContext.class) {
			fieldValue = RuntimeContext.createFrom(context, field.getName(), field.getDeclaringClass());
			field.setAccessible(true);
			field.set(this, fieldValue);
		   }
		   // plugin injection
		   else {

		   }
		} catch (IllegalArgumentException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		} catch (IllegalAccessException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		}
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
    */
   @Task
   @Override
   public void init(final ServletConfig config) throws ServletException {
	// TODO Auto-generated method stub
	super.init(config);
   }

}
