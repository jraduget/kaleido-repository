package org.kaleidofoundry.core.config;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.store.StoreException;

/**
 * Xml properties implementation
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(ConfigurationConstants.XmlPropertiesConfigurationPluginName)
public class XmlPropertiesConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public XmlPropertiesConfiguration(final String identifier, final URI resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, resourceUri, context);
   }

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public XmlPropertiesConfiguration(final String identifier, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, resourceUri, context);
   }

   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> properties) throws StoreException,
	   ConfigurationException {
	try {

	   Properties lprops = new Properties();
	   lprops.loadFromXML(resourceHandler.getInputStream());

	   for (String propName : lprops.stringPropertyNames()) {
		properties.put(propName, lprops.getProperty(propName));
	   }

	   return properties;
	} catch (IOException ioe) {
	   throw new StoreException(ioe);
	}
   }

   @Override
   @NotYetImplemented
   protected Cache<String, String> storeProperties(final Cache<String, String> cacheProperties, final SingleResourceStore resourceStore) throws StoreException,
	   ConfigurationException {
	// try {
	// properties.storeToXML(os, comment, encoding);
	// return properties;
	// } catch (IOException ioe) {
	// throw new StoreException(ioe);
	// }
	return null; // annotation @NotYetImplemented handle throw new NotYetImplementedException()...
   }

}
