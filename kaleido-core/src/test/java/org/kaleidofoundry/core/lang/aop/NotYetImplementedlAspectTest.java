/*  
 * Copyright 2008-2021 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.lang.aop;

import static org.kaleidofoundry.core.lang.NotYetImplementedException.ERROR_NotYetImplemented;
import static org.kaleidofoundry.core.lang.NotYetImplementedException.ERROR_NotYetImplementedCustom;
import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
public class NotYetImplementedlAspectTest  {

   static Logger LOGGER = LoggerFactory.getLogger(NotImplementedlAspectTest.class);

   @Test
   public void constructors() {

	// have to failed
	try {
	   new PartialNotYetImplementedTestService();
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   LOGGER.info(nie.getMessage());
	   assertEquals(ERROR_NotYetImplemented, nie.getCode());
	}

	// have to failed
	try {
	   new PartialNotYetImplementedTestService(0);
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   assertEquals(ERROR_NotYetImplemented, nie.getCode());
	}

	// must succed
	new PartialNotYetImplementedTestService(0, "foo");

   }

   @Test
   public void methods() {

	PartialNotYetImplementedTestService service = new PartialNotYetImplementedTestService(1, "foo");

	// must succed
	service.callImplemented("foo");

	// have to failed
	try {
	   service.callNotImplemented();
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   assertEquals(ERROR_NotYetImplemented, nie.getCode());
	}

	// have to failed
	try {
	   service.callNotImplemented(1, "foo2");
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   assertEquals(ERROR_NotYetImplementedCustom, nie.getCode());
	   assertTrue(nie.getMessage().contains("blablabla"));
	}
   }

   @Test
   public void classes() {
	// have to failed
	try {
	   new NotYetImplementedTestService();
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   assertEquals(ERROR_NotYetImplemented, nie.getCode());
	}

	// have to failed
	try {
	   new NotYetImplementedTestService2();
	   fail("NotYetImplementedException expected");
	} catch (NotYetImplementedException nie) {
	   assertEquals(ERROR_NotYetImplementedCustom, nie.getCode());
	   assertTrue(nie.getMessage().contains("blabla"));
	}
   }
}

class PartialNotYetImplementedTestService {

   private static Logger LOGGER = LoggerFactory.getLogger(NotYetImplementedlAspectTest.class);

   @NotYetImplemented
   public PartialNotYetImplementedTestService() {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotYetImplemented
   public PartialNotYetImplementedTestService(final Integer arg1) {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   public PartialNotYetImplementedTestService(final Integer arg1, final String arg2) {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotYetImplemented
   public void callNotImplemented() {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

   public void callImplemented(final Object arg1) {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotYetImplemented("blablabla")
   public void callNotImplemented(final Integer arg1, final String arg2) {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

}

@NotYetImplemented
class NotYetImplementedTestService {
}

@NotYetImplemented("blabla")
class NotYetImplementedTestService2 {
}