package org.kaleidofoundry.messaging;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;

/**
 * Exception I18n Messaging
 * 
 * @author Jerome RADUGET
 */
public class TransportMessagingException extends I18nException {

   private static final long serialVersionUID = 2245177065493060984L;

   public TransportMessagingException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public TransportMessagingException(final String code, final Locale locale) {
	super(code, locale);
   }

   public TransportMessagingException(final String code, final String... args) {
	super(code, args);
   }

   public TransportMessagingException(final String code, final Throwable cause, final Locale locale,
	   final String... args) {
	super(code, cause, locale, args);
   }

   public TransportMessagingException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public TransportMessagingException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public TransportMessagingException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public TransportMessagingException(final String code) {
	super(code);
   }

   @Override
   public String getI18nBundleName() {
	return MessagingConstant.I18N_RESOURCE;
   }

}
