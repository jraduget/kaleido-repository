package org.kaleidofoundry.core.lang.aop;

import static org.kaleidofoundry.core.lang.NotImplementedException.ERROR_NotImplemented;
import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class NotImplementedlAspectTest extends Assert {

   static Logger LOGGER = LoggerFactory.getLogger(NotImplementedlAspectTest.class);

   @Test
   public void constructors() {

	// have to failed
	try {
	   new PartialImplementedTestService();
	   fail("NotImplementedException expected");
	} catch (NotImplementedException nie) {
	   LOGGER.info(nie.getMessage());
	   assertEquals(ERROR_NotImplemented, nie.getCode());
	}

	// have to failed
	try {
	   new PartialImplementedTestService(0);
	   fail("NotImplementedException expected");
	} catch (NotImplementedException nie) {
	   assertEquals(ERROR_NotImplemented, nie.getCode());
	}

	// must succed
	new PartialImplementedTestService(0, "foo");

   }

   @Test
   public void methods() {

	PartialImplementedTestService service = new PartialImplementedTestService(1, "foo");

	// must succed
	service.callImplemented("foo");

	// have to failed
	try {
	   service.callNotImplemented();
	   fail("NotImplementedException expected");
	} catch (NotImplementedException nie) {
	   assertEquals(ERROR_NotImplemented, nie.getCode());
	}

	// have to failed
	try {
	   service.callNotImplemented(1, "foo2");
	   fail("NotImplementedException expected");
	} catch (NotImplementedException nie) {
	   assertEquals(ERROR_NotImplemented, nie.getCode());
	}
   }

   @Test
   public void classes() {
	// have to failed
	try {
	   new NotImplementedTestService();
	   fail("NotImplementedException expected");
	} catch (NotImplementedException nie) {
	   assertEquals(ERROR_NotImplemented, nie.getCode());
	}
   }
}

class PartialImplementedTestService {

   private static Logger LOGGER = LoggerFactory.getLogger(NotImplementedlAspectTest.class);

   @NotImplemented
   public PartialImplementedTestService() {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotImplemented
   public PartialImplementedTestService(final Integer arg1) {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   public PartialImplementedTestService(final Integer arg1, final String arg2) {
	LOGGER.debug("constructor processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotImplemented
   public void callNotImplemented() {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

   public void callImplemented(final Object arg1) {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

   @NotImplemented
   public void callNotImplemented(final Integer arg1, final String arg2) {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

}

@NotImplemented
class NotImplementedTestService {
}