package org.kaleidofoundry.core.naming;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * Jndi ClientException
 * 
 * @author Jerome RADUGET
 */
public class JndiResourceException extends I18nException {

   private static final long serialVersionUID = 1189942552569947656L;

   /**
    * @param code
    * @param locale
    * @param args
    */
   public JndiResourceException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
   public JndiResourceException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
   public JndiResourceException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   public JndiResourceException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   public JndiResourceException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   public JndiResourceException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
   public JndiResourceException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
   public JndiResourceException(final String code) {
	super(code);
   }

   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.Jndi.getResourceName();
   }

}
