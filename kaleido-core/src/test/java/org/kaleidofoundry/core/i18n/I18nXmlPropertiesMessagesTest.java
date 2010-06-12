package org.kaleidofoundry.core.i18n;

import org.junit.After;
import org.junit.Before;

/**
 * @author Jerome RADUGET
 */
public class I18nXmlPropertiesMessagesTest extends AbstractI18nMessagesTest {

   @Override
   String getResourceRoot() {
	return "org/kaleidofoundry/core/i18n/properties-xml/";
   }

   @Before
   public void setup() {
	I18nMessagesFactory.disableJpaControl();
   }

   @After
   @Override
   public void cleanup() {
	super.cleanup();
	I18nMessagesFactory.enableJpaControl();
   }

}
