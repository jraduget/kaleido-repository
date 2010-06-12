package org.kaleidofoundry.messaging;

import java.util.Locale;

/**
 * Registry Transport Exception
 * 
 * @author Jerome RADUGET
 */
public class TransportRegistryException extends TransportMessagingException {

   private static final long serialVersionUID = -1264807012506733853L;

   public TransportRegistryException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public TransportRegistryException(final String code, final Locale locale) {
	super(code, locale);
   }

   public TransportRegistryException(final String code, final String... args) {
	super(code, args);
   }

   public TransportRegistryException(final String code, final Throwable cause, final Locale locale,
	   final String... args) {
	super(code, cause, locale, args);
   }

   public TransportRegistryException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public TransportRegistryException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public TransportRegistryException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public TransportRegistryException(final String code) {
	super(code);
   }

}
