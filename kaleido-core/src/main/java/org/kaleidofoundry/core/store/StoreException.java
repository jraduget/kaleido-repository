/*
 * $License$
 */
package org.kaleidofoundry.core.store;

import java.io.IOException;
import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;
import org.kaleidofoundry.core.i18n.InternalBundleEnum;

/**
 * @author Jerome RADUGET
 */
public class StoreException extends I18nException {

   private static final long serialVersionUID = 980939390919586472L;

   /**
    * @param code
    * @param locale
    * @param args
    */
   public StoreException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   /**
    * @param code
    * @param locale
    */
   public StoreException(final String code, final Locale locale) {
	super(code, locale);
   }

   /**
    * @param code
    * @param args
    */
   public StoreException(final String code, final String... args) {
	super(code, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    * @param args
    */
   public StoreException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   /**
    * @param code
    * @param cause
    * @param locale
    */
   public StoreException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   /**
    * @param code
    * @param cause
    * @param args
    */
   public StoreException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   /**
    * @param code
    * @param cause
    */
   public StoreException(final String code, final Throwable cause) {
	super(code, cause);
   }

   /**
    * @param code
    */
   public StoreException(final String code) {
	super(code);
   }

   /**
    * @param ioe
    */
   public StoreException(final IOException ioe) {
	super("store.resource.ioe", ioe, ioe.getMessage());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.lang.I18nCodedException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return InternalBundleEnum.ResourceStore.getResourceName();
   }

}
