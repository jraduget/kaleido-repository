package org.kaleidofoundry.core.lang.aop;

import static org.kaleidofoundry.core.lang.NotYetImplementedException.ERROR_NotYetImplemented;
import junit.framework.Assert;

import org.junit.Test;
import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class NotYetImplementedlAspectTest extends Assert {

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
	   assertEquals(ERROR_NotYetImplemented, nie.getCode());
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

   @NotYetImplemented
   public void callNotImplemented(final Integer arg1, final String arg2) {
	LOGGER.debug("method processing");
	LOGGER.debug("*********************************************************************************************");
   }

}

@NotYetImplemented
class NotYetImplementedTestService {
}