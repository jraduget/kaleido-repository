package org.kaleidofoundry.core.cache;

import java.util.Locale;

/**
 * Cache definition not found in configuration
 * 
 * @author Jerome RADUGET
 */
public class CacheDefinitionNotFoundException extends CacheException {

   private static final long serialVersionUID = -8868260474909513690L;

   public CacheDefinitionNotFoundException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public CacheDefinitionNotFoundException(final String code, final Locale locale) {
	super(code, locale);
   }

   public CacheDefinitionNotFoundException(final String code, final String... args) {
	super(code, args);
   }

   public CacheDefinitionNotFoundException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public CacheDefinitionNotFoundException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public CacheDefinitionNotFoundException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public CacheDefinitionNotFoundException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public CacheDefinitionNotFoundException(final String code) {
	super(code);
   }

}
