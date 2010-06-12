package org.kaleidofoundry.core.config;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.store.ResourceHandler;
import org.kaleidofoundry.core.store.SingleResourceStore;
import org.kaleidofoundry.core.store.StoreException;
import org.kaleidofoundry.core.util.ConverterHelper;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * If you want to use a space in a property value, use "& nbsp;" instead<br/>
 * for the separator value use the character '|'
 * 
 * @author Jerome RADUGET
 */
@DeclarePlugin(ConfigurationConstants.MainArgsConfigurationPluginName)
public class MainArgsConfiguration extends AbstractConfiguration implements Configuration {

   /**
    * enumeration of local context property name
    */
   public static enum ContextProperty {
	/** string representation of the main arguments array */
	argsMainString,
	/** arguments separator character */
	argsSeparator;
   }

   /**
    * @param identifier
    * @param resourceUri ignored
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final URI resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, URI.create("memory:/internal/" + identifier + ".mainargs"), context);
   }

   /**
    * @param identifier
    * @param resourceUri
    * @param context
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final String resourceUri, final RuntimeContext<Configuration> context) throws StoreException {
	super(identifier, "memory:/internal/" + identifier + ".mainargs", context);
   }

   /**
    * @param identifier
    * @param runtimeContext
    * @throws StoreException
    */
   public MainArgsConfiguration(final String identifier, final RuntimeContext<Configuration> runtimeContext) throws StoreException {
	this(identifier, (String) null, runtimeContext);
   }

   @Override
   protected Cache<String, String> loadProperties(final ResourceHandler resourceHandler, final Cache<String, String> cacheProperties) throws StoreException,
	   ConfigurationException {

	String mainArgs = context.getProperty(ContextProperty.argsMainString.name());
	String argsSeparator = context.getProperty(ContextProperty.argsSeparator.name());

	final String[] args = ConverterHelper.stringToArray(mainArgs, argsSeparator != null ? argsSeparator : " ");
	Map<String, String> argsMap = ConverterHelper.argsToMap(args);

	for (Entry<String, String> entry : argsMap.entrySet()) {
	   String rawArgValue = entry.getValue();
	   if (rawArgValue != null && rawArgValue.contains("|")) {
		cacheProperties.put(entry.getKey(), StringHelper.replaceAll(rawArgValue, "|", " "));
	   } else {
		cacheProperties.put(entry.getKey(), StringHelper.replaceAll(rawArgValue != null ? rawArgValue : "", "&nbsp;", " "));
	   }
	}

	return cacheProperties;
   }

   @Override
   protected Cache<String, String> storeProperties(final Cache<String, String> cacheProperties, final SingleResourceStore resourceStore) throws StoreException,
	   ConfigurationException {
	return cacheProperties; // unused code because read only is set to true
   }

   @Override
   public boolean isReadOnly() {
	return true;
   }

}
