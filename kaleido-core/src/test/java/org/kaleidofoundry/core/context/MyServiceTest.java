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

import java.text.ParseException;

import org.junit.Test;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author jraduget
 */
public interface MyServiceTest {

   @Test
   void runtimeContextInjectionTest();

   @Test
   void storeInjectionTest() throws ResourceException;

   @Test
   void configurationInjectionTest() throws ParseException;

   @Test
   void cacheManagerInjectionTest();

   @Test
   void cacheInjectionTest();

   @Test
   void i18nMessagesInjectionTest();

   @Test
   void namingServiceInjectionTest();

   @Test
   void entityManagerFactoryInjectionTest();

   @Test
   void entityManagerInjectionTest();
}