package org.kaleidofoundry.mail;

import java.util.Locale;

import org.kaleidofoundry.core.i18n.I18nException;

/**
 * MailSessionException
 * 
 * @author Jerome RADUGET
 */
public class MailException extends I18nException {

   private static final long serialVersionUID = 8604765471487464677L;

   public MailException(final String code, final Locale locale, final String... args) {
	super(code, locale, args);
   }

   public MailException(final String code, final Locale locale) {
	super(code, locale);
   }

   public MailException(final String code, final String... args) {
	super(code, args);
   }

   public MailException(final String code, final Throwable cause, final Locale locale, final String... args) {
	super(code, cause, locale, args);
   }

   public MailException(final String code, final Throwable cause, final Locale locale) {
	super(code, cause, locale);
   }

   public MailException(final String code, final Throwable cause, final String... args) {
	super(code, cause, args);
   }

   public MailException(final String code, final Throwable cause) {
	super(code, cause);
   }

   public MailException(final String code) {
	super(code);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.exception.I18nCodedException#getI18nBundleName()
    */
   @Override
   public String getI18nBundleName() {
	return MailConstants.I18nRessource;
   }

}
