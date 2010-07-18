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

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.kaleidofoundry.core.plugin.Declare;

/**
 * Simple read only interface to handle MessageBundle
 * 
 * @author Jerome RADUGET
 */
@Declare(I18nMessageBundlePluginName)
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
    * @return the i18n message mapping to key parameter
    * @throws MissingResourceException
    */
   String getMessage(final String key) throws MissingResourceException;

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
   String getMessage(final String key, final String... array) throws MissingResourceException;

   /**
    * @return parent resource bundle
    */
   ResourceBundle getParent();

}
