/*  
 * Copyright 2008-2010 the original author or authors 
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

/**
 * @author Jerome RADUGET
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
