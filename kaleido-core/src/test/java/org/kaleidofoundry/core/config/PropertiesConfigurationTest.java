package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class PropertiesConfigurationTest extends AbstractConfigurationTest {

   public PropertiesConfigurationTest() throws StoreException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws StoreException, URISyntaxException {

	return new PropertiesConfiguration("propCpConfig", "classpath:/org/kaleidofoundry/core/config/test.properties",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>());
   }

}
