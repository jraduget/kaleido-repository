/*
 * $License$
 */
package org.kaleidofoundry.core.config;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nRuntimeException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * <p>
 * Configuration exception is class ancestor for configuration package
 * </p>
 * <p>
 * Warning: this class extends I18nCodedRuntimeException and so RuntimeException. <br/>
 * Any ConfigurationException will therefore not be trapped by default (directly propagated).<br/>
 * If you want trap explicit configuration errors :
 * </p>
 * 
 * <pre>
 * 	try {
 * 		// handle configuration exception
 *  } catch (ConfigurationException) {
 *  	// specific process
 *  }
 * </pre>
 * 
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class ConfigurationException extends I18nRuntimeException {

   private static final long serialVersionUID = 1125414504901L;

   /**
    * @param code
    * @param locale
    * @param args
    */
   public ConfigurationException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
   public ConfigurationException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
   public ConfigurationException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param defaultMsg
    */
   public ConfigurationException(final String code, final String defaultMsg) {
	super(code, defaultMsg);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   public ConfigurationException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   public ConfigurationException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   public ConfigurationException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
   public ConfigurationException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
   public ConfigurationException(final String code) {
	super(code);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.exception.I18nCodedRuntimeException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.Configuration.getResourceName();
   }

}
