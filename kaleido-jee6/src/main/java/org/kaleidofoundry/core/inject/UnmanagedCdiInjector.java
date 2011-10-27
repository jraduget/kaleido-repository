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
package org.kaleidofoundry.core.inject;

import java.lang.annotation.Annotation;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use it when you want to use CDI in a unmanaged env. (java main program, ...)
 * 
 * @author Jerome RADUGET
 */
public class UnmanagedCdiInjector {

   private static final Logger LOGGER = LoggerFactory.getLogger(UnmanagedCdiInjector.class);

   private static boolean weldSEInitialize = false;
   private static WeldContainer weldContainer;
   private static Weld weldSE;

   static {
	init();
   }

   /**
    * initializer, call at startup (main, junit static setup, ...)
    */
   public static synchronized void init() {
	if (!weldSEInitialize) {
	   LOGGER.info("Initializing CDI using weldSE container");
	   weldSE = new Weld();
	   weldContainer = weldSE.initialize();
	   weldSEInitialize = true;
	}
   }

   /**
    * call it when application stop (main, junit static cleanup, ...)
    */
   public static synchronized void shutdown() {
	if (weldSE != null && weldSEInitialize) {
	   LOGGER.info("Shutdown weldSE container");
	   weldSE.shutdown();
	   weldSEInitialize = false;
	}
   }

   /**
    * @param <T>
    * @param type
    * @param annotations
    * @return bean
    */
   public static <T> T getBean(final Class<T> type, final Annotation... annotations) {
	return weldContainer.instance().select(type, annotations).get();
   }

   /**
    * @return the current weld container
    */
   public static WeldContainer getContainer() {
	return weldContainer;
   }

}
