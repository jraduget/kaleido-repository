package org.kaleidofoundry.mail.session;

import java.util.Locale;

import org.kaleidofoundry.mail.MailException;

/**
 * MailSession Exception
 * 
 * @author Jerome RADUGET
 */
public class MailSessionException extends MailException {

   private static final long serialVersionUID = 8604765471487464677L;

   public MailSessionException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public MailSessionException(final String code, final Locale locale) {
	super(code, locale);
   }

   public MailSessionException(final String code, final String... args) {
	super(code, args);
   }

   public MailSessionException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public MailSessionException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public MailSessionException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public MailSessionException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public MailSessionException(final String code) {
	super(code);
   }

}
