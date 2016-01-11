/*  
 * Copyright 2008-2016 the original author or authors 
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

import static org.kaleidofoundry.core.lang.NotNullException.ERROR_NotNullArgument;
import static org.kaleidofoundry.core.lang.NotNullException.ERROR_NotNullReturn;
import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotNullException;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jraduget
 */
public class NotNullAspectTest  {

   static Logger LOGGER = LoggerFactory.getLogger(NotNullAspectTest.class);

   @Test
   public void constructorArguments() {

	// have to failed @NotNull is presents
	try {
	   new ServiceTest((String) null);
	   fail("NotNullException expected on first argument");
	} catch (NotNullException nne) {
	   LOGGER.info(nne.getMessage());
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg1"));
	}

	// have to succeed, no @NotNull annotation on argument
	new ServiceTest((Integer) null);

	// have to succeed
	new ServiceTest(1, "foo2", "foo3", "foo4");
	new ServiceTest(null, "foo2", null, "foo4");

	// have to failed @NotNull is presents
	try {
	   new ServiceTest(1, null, "foo3", null);
	   fail("NotNullException expected on second argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg2"));
	}
	try {
	   new ServiceTest(1, null, "foo3", "foo4");
	   fail("NotNullException expected on second argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg2"));
	}
	try {
	   new ServiceTest(1, "foo2", "foo3", null);
	   fail("NotNullException expected on fourth argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg4"));
	}

   }

   @Test
   public void methodArguments() {

	ServiceTest serviceTest = new ServiceTest((Integer) null);

	// have to succeed, no @NotNull annotation on argument
	serviceTest.call("foo");
	// have to failed @NotNull is presents
	try {
	   serviceTest.call(null);
	   fail("NotNullException expected on first argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg1"));
	}
	// have to succeed
	serviceTest.call(1, "foo2", "foo3", "foo4");
	serviceTest.call(null, "foo2", null, "foo4");

	// have to failed @NotNull is presents
	try {
	   serviceTest.call(1, null, "foo3", null);
	   fail("NotNullException expected on second argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg2"));
	}
	try {
	   serviceTest.call(1, null, "foo3", "foo4");
	   fail("NotNullException expected on second argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg2"));
	}
	try {
	   serviceTest.call(1, "foo2", "foo3", null);
	   fail("NotNullException expected on fourth argument");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullArgument, nne.getCode());
	   assertTrue(nne.getMessage().contains("arg4"));
	}
   }

   @Test
   public void methodResults() {

	// have to succeed
	new ServiceTest(1).identity(1);

	// have to failed
	try {
	   new ServiceTest(1).identity(null);
	   fail("NotNullException expected on result");
	} catch (NotNullException nne) {
	   assertEquals(ERROR_NotNullReturn, nne.getCode());
	   assertTrue(nne.getMessage().contains("i"));
	}
   }
}

class ServiceTest {

   public ServiceTest(@NotNull final String arg1) {
	NotNullAspectTest.LOGGER.debug("constructor processing");
	NotNullAspectTest.LOGGER.debug("*********************************************************************************************");
   }

   public ServiceTest(final Integer arg1) {
	NotNullAspectTest.LOGGER.debug("constructor processing");
	NotNullAspectTest.LOGGER.debug("*********************************************************************************************");
   }

   public ServiceTest(final Integer arg1, @NotNull final String arg2, final String arg3, @NotNull final String arg4) {
	NotNullAspectTest.LOGGER.debug("constructor processing");
	NotNullAspectTest.LOGGER.debug("*********************************************************************************************");
   }

   public void call(@NotNull final Object arg1) {
	NotNullAspectTest.LOGGER.debug("method processing");
	NotNullAspectTest.LOGGER.debug("*********************************************************************************************");
   }

   public void call(final Integer arg1, @NotNull final String arg2, final String arg3, @NotNull final String arg4) {
	NotNullAspectTest.LOGGER.debug("method processing");
	NotNullAspectTest.LOGGER.debug("*********************************************************************************************");
   }

   @NotNull
   public Integer identity(final Integer i) {
	return i;
   }

}