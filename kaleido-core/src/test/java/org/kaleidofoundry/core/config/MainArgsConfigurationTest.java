package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;

import org.junit.Test;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.properties.ExtendedProperties;

/**
 * @author Jerome RADUGET
 */
public class MainArgsConfigurationTest extends AbstractConfigurationTest {

   public MainArgsConfigurationTest() throws StoreException, URISyntaxException {
	super();
   }

   @Override
   protected Configuration newInstance() throws StoreException, URISyntaxException {

	String[] mainArgs = new String[] { "application.name=app", "application.description=description&nbsp;of&nbsp;the&nbsp;application...",
		"application.date=2006-09-01T00:00:00", "application.version=1.0.0", "application.librairies=dom4j.jar|log4j.jar|mail.jar",
		"application.modules.sales.name=Sales", "application.modules.sales.version=1.1.0", "application.modules.marketing.name=Market.",
		"application.modules.netbusiness.name=", "application.array.bigdecimal=987.5|1.123456789", "application.array.boolean=false|true",
		"application.single.boolean=true", "application.single.bigdecimal=1.123456789",
		"application.array.date=2009-01-02T00:00:00|2009-12-31T00:00:00|2012-05-15T00:00:00" };

	ExtendedProperties properties = new ExtendedProperties();
	properties.setMultiValueProperty(MainArgsConfiguration.ContextProperty.argsMainString.name(), mainArgs);

	return new MainArgsConfiguration("mainArgsConfig", new RuntimeContext<Configuration>(properties));
   }

   @Test(expected = IllegalStateException.class)
   @Override
   public void store() throws StoreException {
	assertNotNull(configuration);
	configuration.store();
   }

   @Test
   public void isReadonly() {
	assertNotNull(configuration);
	assertTrue(configuration.isReadOnly());
   }
}
