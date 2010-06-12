/* 
 * $License$ 
 */
package org.kaleidofoundry.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Constants
 * 
 * @author Jerome RADUGET
 */
public interface ConfigurationConstants {

   /** Logger */
   static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

   /** Configuration java environment name : java -Dkaleido.configuration=classpath:/datasource.properties ... */
   static final String JavaEnvProperties = "kaleido.configuration";

   // ** Plugin part ***************************************************************************************************

   /**
    * configuration resource store handled extension<br/>
    */
   static enum Extension {
	// if you change enum name, please report changed to plugin name
	properties,
	xmlproperties,
	xml,
	javasystem,
	osenv,
	mainargs
   }

   /** configuration interface plugin name */
   public static final String ConfigurationPluginName = "configuration";
   /** classic properties implementation configuration plugin name - configuration resource uri have to end with '.properties' */
   public static final String PropertiesConfigurationPluginName = "configuration.properties";
   /** xml properties implementation configuration plugin name - configuration resource uri have to end with '.xmlproperties' */
   public static final String XmlPropertiesConfigurationPluginName = "configuration.properties.xml";
   /** xml implementation configuration plugin name - configuration resource uri have to end with '.xml' */
   public static final String XmlConfigurationPluginName = "configuration.xml";
   /** java env variable implementation configuration plugin name - configuration resource uri have to end with '.javasystem' */
   public static final String JavaSystemConfigurationPluginName = "configuration.javasystem";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.osenv' */
   public static final String OsEnvConfigurationPluginName = "configuration.osenv";
   /** operating system env variable implementation configuration plugin name - configuration resource uri have to end with '.mainargs' */
   public static final String MainArgsConfigurationPluginName = "configuration.mainargs";

   // ** Key and property serialization part ***************************************************************************

   /** Separator to specify root */
   static final String KeyRoot = "//";
   /** Default separator for key name */
   static final String KeySeparator = "/";
   /** Separator to specify root for properties keys */
   static final String KeyPropertiesRoot = "";
   /** Default separator for properties name */
   static final String KeyPropertiesSeparator = ".";
   /** Multiple value separator */
   static final String MultiValDefaultSeparator = " ";
   /** String Date Formatter */
   static final String StrDateFormat = "yyyy-MM-dd'T'hh:mm:ss"; // yyyy-MM-ddThh:mm:ss
   /** String Number Formatter */
   static final String StrNumberFormat = "##0.0####";

}
