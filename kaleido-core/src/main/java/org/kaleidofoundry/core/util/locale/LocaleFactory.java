package org.kaleidofoundry.core.util.locale;

import java.util.Locale;

import org.kaleidofoundry.core.lang.annotation.NotNull;

/**
 * @author Jérôme RADUGET
 */
public abstract class LocaleFactory {

   static enum LocaleEnum {
	Defaulf;
   }

   /**
    * @return current locale for the user or server
    */
   public abstract Locale getCurrentLocale();

   /**
    * @return default locale factory
    */
   public static LocaleFactory getDefaultFactory() {
	return getFactory(LocaleEnum.Defaulf);
   }

   /**
    * @param factoryID
    * @return localeFactory implementation
    */
   @NotNull
   public static LocaleFactory getFactory(@NotNull final LocaleEnum factoryID) {

	switch (factoryID) {

	case Defaulf: {
	   return new DefaultLocalFactory();
	}

	default: {
	   throw new IllegalArgumentException("Incorrect argument value factoryID:" + factoryID);
	}

	}
   }

}
