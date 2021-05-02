/*
 *  Copyright 2008-2021 the original author or authors.
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kaleidofoundry.core.cache.CacheManager;
import org.kaleidofoundry.core.cache.CacheManagerFactory;
import org.kaleidofoundry.core.cache.CacheProvidersEnum;
import org.kaleidofoundry.launcher.UnmanagedCdiInjector;

/**
 * @author jraduget
 */
public class MyServiceJavaEETest extends AbstractMyServiceTest {

   @BeforeClass
   public static void setupStatic() {
	UnmanagedCdiInjector.init();
	CacheManagerFactory.provides(CacheProvidersEnum.infinispan.name(), new RuntimeContext<CacheManager>("myCustomCacheManager"));
   }

   @AfterClass
   public static void shutdownWeld() {
	UnmanagedCdiInjector.shutdown();
   }

   public static <T> T getBean(final Class<T> type, final Annotation... annotations) {
	return UnmanagedCdiInjector.getContainer().instance().select(type, annotations).get();
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
