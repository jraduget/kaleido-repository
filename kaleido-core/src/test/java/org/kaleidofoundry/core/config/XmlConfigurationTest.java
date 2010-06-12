package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class XmlConfigurationTest extends AbstractConfigurationTest {

   public XmlConfigurationTest() throws StoreException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws StoreException, URISyntaxException {
	return new org.kaleidofoundry.core.config.XmlConfiguration("xmlCpConfig", "classpath:/org/kaleidofoundry/core/config/test.xml",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>());
   }

}
