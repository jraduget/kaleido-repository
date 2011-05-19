/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.i18n.I18nConstants.I18nMessageBundlePluginName;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.context.Provider;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Interface to handle resource bundles containing, locale-specific objects
 * 
 * @see ResourceBundle
 * @see MessageFormat
 * @see DefaultMessageBundle
 * @author Jerome RADUGET
 */
@Declare(I18nMessageBundlePluginName)
@Provider(value = I18nMessagesProvider.class, singletons = false)
@Task(comment = "javadoc description & samples")
public interface I18nMessages {

   /**
    * @return resource name of the bundle
    */
   String getResourceName();

   /*
    * (non-Javadoc)
    * @see java.util.ResourceBundle#getKeys()
    */
   public Enumeration<String> getKeys();

   /**
    * @param key message code
    * @return the i18n message, mapping to key parameter
    * @throws MissingResourceException
    */
   String getMessage(final String key) throws MissingResourceException;

   /**
    * <p>
    * Gets a string message from the resource bundle for the given key. The message may contain variables that will be substituted with the
    * given arguments. Variables have the format:
    * </p>
    * Example :
    * 
    * <pre>
    * # my i18n message properties
    * myMessage.code=Hello Sir {0}, you win {1,number,#.##}$ !
    * </pre>
    * 
    * Java program :
    * 
    * <pre>
    * I18nMessages myMessageBundle = ...;
    * ...
    * System.out.println(myMessageBundle.getMessage("myMessage.code", "foo", 123.45));
    * ...
    * </pre>
    * 
    * @param key The resource key
    * @param array An array of objects to place in corresponding variables
    * @return the i18n message mapping to key parameter
    * @throws MissingResourceException
    * @see MessageFormat
    */
   String getMessage(final String key, final Object... array) throws MissingResourceException;

   /**
    * @return parent resource bundle
    */
   ResourceBundle getParent();

}
