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
package org.kaleidofoundry.core.context;

import java.lang.annotation.Annotation;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheProvidersEnum;

/**
 * @author Jerome RADUGET
 */
public class MyServiceJavaEE6Test extends AbstractMyServiceTest {

   private static Weld weldSE;
   private static WeldContainer weldContainer;
   private static boolean weldSEInitialize = false;

   @BeforeClass
   public static void setupStatic() {
	weldSE = new Weld();
	weldContainer = weldSE.initialize();
	weldSEInitialize = true;

	CacheManagerFactory.provides(CacheProvidersEnum.infinispan4x.name(), new RuntimeContext<CacheManager>("myCustomCacheManager"));
   }

   @AfterClass
   public static void shutdownWeld() {
	if (weldSE != null && weldSEInitialize) {
	   weldSE.shutdown();
	}
   }

   public static <T> T getBean(final Class<T> type, final Annotation... annotations) {
	return weldContainer.instance().select(type, annotations).get();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.AbstractMyServiceTest#getMyService()
    */
   @Override
   public MyService getMyService() {
	return getBean(MyService.class);
   }

}
