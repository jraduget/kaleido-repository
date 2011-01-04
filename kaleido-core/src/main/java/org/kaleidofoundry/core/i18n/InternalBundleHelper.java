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

/**
 * I18n internal helper
 * 
 * @author Jerome RADUGET
 */
public interface InternalBundleHelper {

   /** Core message bundle */
   I18nMessages CoreMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.CORE.getResourceName());
   /** Cache message bundle */
   I18nMessages CacheMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.CACHE.getResourceName(), CoreMessageBundle);
   /** Message bundle bundle */
   I18nMessages I18nMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.MESSAGE_BUNDLE.getResourceName(), CoreMessageBundle);
   /** Plugin message bundle */
   I18nMessages PluginMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.PLUGIN.getResourceName(), CoreMessageBundle);
   /** ResourcStore message bundle */
   I18nMessages StoreMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.STORE.getResourceName(), CoreMessageBundle);
   /** Configuration message bundle */
   I18nMessages ConfigurationMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.CONFIGURATION.getResourceName(), CoreMessageBundle);
   /** RuntimeContext message bundle */
   I18nMessages ContextMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.CONTEXT.getResourceName(), CoreMessageBundle);
   /** Jndi message bundle */
   I18nMessages NamingMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.NAMING.getResourceName(), CoreMessageBundle);
   /** Web message bundle */
   I18nMessages WebMessageBundle = I18nMessagesFactory.provides(InternalBundleEnum.WEB.getResourceName(), CoreMessageBundle);

}
