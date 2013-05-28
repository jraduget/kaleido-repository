package org.kaleidofoundry.spring.web;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.core.env.EnvironmentInitializer;
import org.kaleidofoundry.core.env.model.EnvironmentConstants;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.core.web.StartupListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

/**
 * A default web application environment initializer, used to load environment settings during the application startup.
 * This class implements {@link ServletContextAware}, by this way spring will automatically inject the {@link ServletContext} informations. <br/>
 * You can customize the configurations to load :
 * <ul>
 * <li>by setting setting {@link #setClassName(String)} in your spring application.xml the class annotations to scan (
 * {@link NamedConfigurations} or {@link NamedConfiguration})</li>
 * <li>by extending this class, and adding annotation {@link NamedConfigurations} or {@link NamedConfiguration}</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 * @see EnvironmentConstants
 * @see EnvironmentInitializer
 */
@Configuration
public class WebEnvironmentInitializer implements ServletContextAware {

   private ServletContext servletContext;

   protected EnvironmentInitializer initializer;

   private String className;

   @Override
   public void setServletContext(ServletContext servletContext) {
	this.servletContext = servletContext;
   }

   /**
    * set the class where to scan {@link NamedConfigurations} or {@link NamedConfiguration} configurations to load
    * @param className
    */
   public void setClassName(String className) {
	this.className = className;
   }

   @PostConstruct
   public void initialize() {
	if (servletContext != null) {
	   Class<?> classToIntrospect = this.getClass();
	   if (!StringHelper.isEmpty(className)) {
		try {
		   classToIntrospect = Class.forName(className);
		} catch (ClassNotFoundException e) {
		   throw new IllegalArgumentException(e.getMessage(), e);
		}
	   }
	   initializer = StartupListener.createEnvironmentInitializerFrom(servletContext, classToIntrospect);
	   initializer.start();
	}
   }

   @PreDestroy
   public void destroy() {
	if (servletContext != null) {
	   initializer.stop();
	}
   }
}
