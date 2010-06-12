package org.kaleidofoundry.mail.sender;

import java.util.Locale;

import org.kaleidofoundry.mail.MailException;

/**
 * MailSender Exception
 * 
 * @author Jerome RADUGET
 */
public class MailSenderException extends MailException {

   private static final long serialVersionUID = -4072499048280078160L;

   public MailSenderException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public MailSenderException(final String code, final Locale locale) {
	super(code, locale);
   }

   public MailSenderException(final String code, final String... args) {
	super(code, args);
   }

   public MailSenderException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public MailSenderException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public MailSenderException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public MailSenderException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public MailSenderException(final String code) {
	super(code);
   }

}
