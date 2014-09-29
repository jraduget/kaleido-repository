package org.kaleidofoundry.spring.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kaleidofoundry.core.env.EnvironmentInitializer;
import org.kaleidofoundry.core.persistence.UnmanagedEntityManagerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link SpringJUnit4ClassRunner} extension for kaleido
 * 
 * @author Jerome RADUGET
 */
public class SpringExtendedJUnit4ClassRunner extends SpringJUnit4ClassRunner {

   public SpringExtendedJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
	super(clazz);
   }

   /*
    * (non-Javadoc)
    * @see org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier)
    */
   @Override
   public void run(final RunNotifier notifier) {

	// load env variables and start kaleido components
	EnvironmentInitializer environmentInitializer = new EnvironmentInitializer(getTestClass().getJavaClass());
	environmentInitializer.init();
	environmentInitializer.start();

	// run tests
	super.run(notifier);

	// cleanup at end

	// unload other resources like configurations / caches ...
	if (environmentInitializer != null) {
	   environmentInitializer.stop();
	}

	// release entity manager factory is used
	UnmanagedEntityManagerFactory.closeAll();
   }

}
