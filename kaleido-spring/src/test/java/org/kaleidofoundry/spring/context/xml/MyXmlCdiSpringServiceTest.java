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
package org.kaleidofoundry.spring.context.xml;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.context.AbstractMyServiceTest;
import org.kaleidofoundry.core.context.MyService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test @{@link Inject} kaleido modules injection (declared in a xml spring bean file)
 * 
 * @author jraduget
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/xml/springCdiContext.xml" })
public class MyXmlCdiSpringServiceTest extends AbstractMyServiceTest {

   @Inject
   private MyXmlCdiSpringService myService;

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
