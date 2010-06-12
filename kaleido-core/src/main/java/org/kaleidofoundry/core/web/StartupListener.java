package org.kaleidofoundry.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Startup listener used to intitialize some webapp resource
 * 
 * @author Jerome RADUGET
 */
public class StartupListener implements ServletContextListener {

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent sce) {
	ServletContextProvider.init(sce.getServletContext());
   }

   /*
    * (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent sce) {
   }

}
