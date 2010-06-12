package org.kaleidofoundry.core.util.locale;

import java.util.Locale;

/**
 * @author Jérôme RADUGET
 */
public class DefaultLocalFactory extends LocaleFactory {

   @Override
   public Locale getCurrentLocale() {
	// TODO detect web, ejb, ws container & context to compute "user" session context locale
	return Locale.getDefault();
   }

}
