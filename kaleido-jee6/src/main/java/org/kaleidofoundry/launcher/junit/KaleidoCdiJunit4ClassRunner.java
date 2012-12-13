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
package org.kaleidofoundry.launcher.junit;

import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.config.NamedConfigurations;
import org.kaleidofoundry.launcher.UnmanagedCdiInjector;

/**
 * Kaleido CDI junit runner (using Jboss Weld SE)
 * <p>
 * Supported Annotations :
 * <ul>
 * <li>{@link Inject} to inject a class instance (can be use with {@link PersistenceUnit} and {@link PersistenceContext})</li>
 * <li>{@link NamedConfiguration} to load a configuration resource</li>
 * <li>{@link NamedConfigurations} to load many configuration resources</li>
 * </ul>
 * </p>
 * <p>
 * Example :
 * 
 * <pre>
 * 
 *   	// allows to use @Inject in your test classes
 *   	&#064;RunWith(KaleidoCdiJunit4ClassRunner.class)
 *   	// registers if you need, a configuration to load (&#064;NamedConfigurations also exists)
 *   	&#064;NamedConfiguration(name = "configuration", uri = "classpath:/config/application.properties")
 *   	class MyTest {
 *     		
 *    	 	// you inject your JPA EntityManager
 *    	  	&#064;Inject &#064;PersistenceUnit
 *     	 	private EntityManagerFactory myEmf;
 *   
 *     		&#064;Inject &#064;PersistenceUnit
 *     		private EntityManager myEm;
 *     		
 *     	 	// inject kaleido components
 *     	 	&#064;Inject &#064;Context
 *   		private configuration;
 *   
 *   		&#064;Inject &#064;Context
 *      		private FileStore writer;
 *      
 *   		...
 *   	}
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class KaleidoCdiJunit4ClassRunner extends BlockJUnit4ClassRunner {

   public KaleidoCdiJunit4ClassRunner(final Class<?> klass) throws InitializationError {
	super(klass);
   }

   /*
    * (non-Javadoc)
    * @see org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier)
    */
   @Override
   public void run(final RunNotifier notifier) {

	// init once weld-se
	UnmanagedCdiInjector.init(getTestClass().getJavaClass());

	// run tests
	super.run(notifier);

	// cleanup at end
	UnmanagedCdiInjector.shutdown();
   }

   /*
    * (non-Javadoc)
    * @see org.junit.runners.BlockJUnit4ClassRunner#createTest()
    */
   @Override
   protected Object createTest() throws Exception {
	// create the unit test instance with weld instead of the default one
	return UnmanagedCdiInjector.getContainer().instance().select(getTestClass().getJavaClass()).get();
   }

}
