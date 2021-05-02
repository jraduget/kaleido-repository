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
package org.kaleidofoundry.spring.context.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.context.AbstractMyServiceTest;
import org.kaleidofoundry.core.context.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author jraduget
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/processor/springContext.xml" })
public class MySpringServiceTest extends AbstractMyServiceTest {

   @Autowired
   private MyService myService;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.AbstractMyServiceTest#getMyService()
    */
   @Override
   public MyService getMyService() {
	return myService;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.AbstractMyServiceTest#entityManagerFactoryInjectionTest()
    */
   @Override
   @Test
   public void entityManagerFactoryInjectionTest() {
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.context.AbstractMyServiceTest#entityManagerInjectionTest()
    */
   @Override
   @Test
   public void entityManagerInjectionTest() {
   }

   /**
    * Test that {@link MySpringService#getMySpringContext()} is well injected by spring, and not by spring post processor.
    * The reason is that this field is annotated by {@link Autowired}, then it is handle by spring bean ioc
    */
   @Test
   public void mySpringContext() {
	assertNotNull(myService);
	assertTrue(myService.getClass().equals(MySpringService.class));
	assertNotNull(((MySpringService) myService).getMySpringContext());
	assertEquals("classpath:/store", ((MySpringService) myService).getMySpringContext().getProperty("baseUri"));
   }
}
