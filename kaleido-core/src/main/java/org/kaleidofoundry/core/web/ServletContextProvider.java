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
package org.kaleidofoundry.core.web;

import javax.servlet.ServletContext;

import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;

/**
 * Provides to user the current {@link ServletContext} class<br/>
 * <br>
 * To use it use and declare {@link StartupListener} in your web.xml like this :
 * 
 * <pre>
 * 	<listener>
 * 		<listener-class>org.kaleidofoundry.core.web.StartupListener</listener-class>
 * 	</listener>
 * </pre>
 */
public abstract class ServletContextProvider {

   private static ServletContext ServletContext;

   static synchronized void init(final ServletContext context) {
	ServletContext = context;
   }

   /**
    * @return the current servletContext
    */
   @Review(comment = "I18n messages startup & error", category = ReviewCategoryEnum.Improvement)
   public static ServletContext getServletContext() {

	if (ServletContext == null) { throw new IllegalStateException(
		"webapp servlet context have not be initialized. please check that you have declared kaleido startup filter"); }
	return ServletContext;
   }

}
