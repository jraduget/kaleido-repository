package org.kaleidofoundry.core.web;

import javax.servlet.ServletContext;

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

   static synchronized void init(ServletContext context) {
	ServletContext = context;
   }

   /**
    * @return the current servletContext
    */
   public static ServletContext getServletContext() {

	if (ServletContext == null) {
	   // TODO I18n messages startup & error
	   throw new IllegalStateException(
		   "webapp servlet context have not be initialized. please check that you have declared kaleido startup filter");
	}
	return ServletContext;
   }

}
