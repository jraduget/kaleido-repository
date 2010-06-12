package org.kaleidofoundry.core.lang;

import java.util.Locale;

import org.kaleidofoundry.core.lang.annotation.NotImplemented;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;

/**
 * Throws this exception when portion of code is not yet implemented<br/>
 * You can use annotation {@link NotImplemented} on constructor / method / class , it will automated this behaviors without any code
 * 
 * @author Jerome RADUGET
 * @see NotYetImplemented
 */
public class NotYetImplementedException extends NotImplementedException {

   private static final long serialVersionUID = 5485178075125195470L;

   public final static String ERROR_NotYetImplemented = "exception.notyetimplemented";
   public final static String ERROR_NotYetImplementedCustom = "exception.notyetimplemented.custom";

   /**
    *
    */
   public NotYetImplementedException() {
	super(ERROR_NotYetImplemented);
   }

   /**
    * @param locale
    */
   public NotYetImplementedException(final Locale locale) {
	super(ERROR_NotYetImplemented, locale);
   }

   /**
    * @param customMessage
    */
   public NotYetImplementedException(final String customMessage) {
	super(ERROR_NotYetImplementedCustom, customMessage);
   }

   /**
    * @param locale
    * @param customMessage
    */
   public NotYetImplementedException(final Locale locale, final String customMessage) {
	super(ERROR_NotYetImplementedCustom, locale, customMessage);
   }

}
