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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jerome RADUGET
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/springContext.xml" })
public class MyServiceSpringTest extends AbstractMyServiceTest {

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

}
