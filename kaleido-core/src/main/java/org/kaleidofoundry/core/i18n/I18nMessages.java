package org.kaleidofoundry.core.i18n;

import java.util.MissingResourceException;

/**
 * Simple read only interface to handle MessageBundle
 * 
 * @author Jerome RADUGET
 */
public interface I18nMessages {

   /**
    * @param key message code
    * @return the i18n message mapping to key parameter
    * @throws MissingResourceException
    */
   String getMessage(String key) throws MissingResourceException;

   /**
    * <p>
    * Gets a string message from the resource bundle for the given key. The message may contain variables that will be substituted with the
    * given arguments. Variables have the format:
    * </p>
    * <code> This message has two variables: {0} and {1} </code>
    * 
    * @param key The resource key
    * @param array An array of objects to place in corresponding variables
    * @return the i18n message mapping to key parameter
    * @throws MissingResourceException
    */
   String getMessage(String key, String... array) throws MissingResourceException;

}
