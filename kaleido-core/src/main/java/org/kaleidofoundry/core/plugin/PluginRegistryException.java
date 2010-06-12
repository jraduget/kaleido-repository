/*
 * $License$
 */
package org.kaleidofoundry.core.plugin;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * Plugin Registry I18n Exception
 * 
 * @author Jerome RADUGET
 */
public class PluginRegistryException extends I18nRuntimeException {

   private static final long serialVersionUID = 8617595959218104906L;

   /**
    * @param code
    * @param locale
    * @param args
    */
   public PluginRegistryException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
   public PluginRegistryException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
   public PluginRegistryException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   public PluginRegistryException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   public PluginRegistryException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   public PluginRegistryException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
   public PluginRegistryException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
   public PluginRegistryException(final String code) {
	super(code);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.exception.I18nCodedRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.Plugin.getResourceName();
   }

}
