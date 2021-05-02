/*
 * Copyright 2008-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.util.locale;

import java.util.Locale;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * @author jraduget
 */
public abstract class LocaleFactory {

   static enum LocaleEnum {
	Default;
   }

   /**
    * @return current locale for the user or server
    */
   public abstract Locale getCurrentLocale();

   /**
    * @return default locale factory
    */
   public static LocaleFactory getDefaultFactory() {
	return getFactory(LocaleEnum.Default);
   }

   /**
    * @param factoryID
    * @return localeFactory implementation
    */
   @NotNull
   public static LocaleFactory getFactory(@NotNull final LocaleEnum factoryID) {

	switch (factoryID) {

	case Default: {
	   return new DefaultLocalFactory();
	}

	default: {
	   throw new IllegalArgumentException("Incorrect argument value factoryID:" + factoryID);
	}

	}
   }

   /**
    * Parse a string locale
    * 
    * @param localeString
    * @return locale that map the given string parameter
    */
   public static Locale parseLocale(final String localeString) {

	if (!StringHelper.isEmpty(localeString)) {

	   final String[] localeParts = StringHelper.split(localeString, "_");
	   final String language = (localeParts.length > 0 ? localeParts[0] : null);
	   final String country = (localeParts.length > 1 ? localeParts[1] : null);
	   final String variant = (localeParts.length > 2 ? localeParts[2] : null);

	   if (language != null) {
		if (country != null) {
		   if (variant != null) {
			return new Locale(language, country, variant);
		   } else {
			return new Locale(language, country);
		   }
		} else {
		   return new Locale(language);
		}
	   }
	}

	return null;
   }
}
