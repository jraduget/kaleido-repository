package org.kaleidofoundry.core.i18n;

/**
 * I18n internal helper
 * 
 * @author Jerome RADUGET
 */
public interface InternalBundleHelper {

   /** Core message bundle */
   I18nMessages CoreMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Core.getResourceName());
   /** Cache message bundle */
   I18nMessages CacheMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Cache.getResourceName(), CoreMessageBundle);
   /** Message bundle bundle */
   I18nMessages MessageBundleMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.MessageBundle.getResourceName(), CoreMessageBundle);
   /** Plugin message bundle */
   I18nMessages PluginMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Plugin.getResourceName(), CoreMessageBundle);
   /** ResourcStore message bundle */
   I18nMessages ResourceStoreMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.ResourceStore.getResourceName(), CoreMessageBundle);
   /** Configuration message bundle */
   I18nMessages ConfigurationMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Configuration.getResourceName(), CoreMessageBundle);
   /** RuntimeContext message bundle */
   I18nMessages RuntimeContextMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Runtimecontext.getResourceName(), CoreMessageBundle);
   /** Jndi message bundle */
   I18nMessages JndiContextMessageBundle = I18nMessagesFactory.getMessages(InternalBundleEnum.Jndi.getResourceName(), CoreMessageBundle);

}
