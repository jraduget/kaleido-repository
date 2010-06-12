package org.kaleidofoundry.core.cache;

import java.util.Locale;

/**
 * @author Jerome RADUGET
 */
public class CacheConfigurationException extends CacheException {

   private static final long serialVersionUID = 5706242505837363556L;

   public CacheConfigurationException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public CacheConfigurationException(final String code, final Locale locale) {
	super(code, locale);
   }

   public CacheConfigurationException(final String code, final String... args) {
	super(code, args);
   }

   public CacheConfigurationException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public CacheConfigurationException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public CacheConfigurationException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public CacheConfigurationException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public CacheConfigurationException(final String code) {
	super(code);
   }

}
