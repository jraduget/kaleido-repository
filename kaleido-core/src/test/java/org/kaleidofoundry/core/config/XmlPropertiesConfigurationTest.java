package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;

/**
 * @author Jerome RADUGET
 */
public class XmlPropertiesConfigurationTest extends AbstractConfigurationTest {

   public XmlPropertiesConfigurationTest() throws StoreException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws StoreException, URISyntaxException {
	return new XmlPropertiesConfiguration("propXmlCpConfig", "classpath:/org/kaleidofoundry/core/config/test.properties.xml",
		new RuntimeContext<org.kaleidofoundry.core.config.Configuration>());
   }

}
