package org.kaleidofoundry.core.cache;

import java.util.Locale;

/**
 * @author Jerome RADUGET
 */
public class CacheConfigurationNotFoundException extends CacheConfigurationException {

   private static final long serialVersionUID = 6810618877888672609L;

   public CacheConfigurationNotFoundException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public CacheConfigurationNotFoundException(final String code, final Locale locale) {
	super(code, locale);
   }

   public CacheConfigurationNotFoundException(final String code, final String... args) {
	super(code, args);
   }

   public CacheConfigurationNotFoundException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public CacheConfigurationNotFoundException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public CacheConfigurationNotFoundException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public CacheConfigurationNotFoundException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public CacheConfigurationNotFoundException(final String code) {
	super(code);
   }

}
