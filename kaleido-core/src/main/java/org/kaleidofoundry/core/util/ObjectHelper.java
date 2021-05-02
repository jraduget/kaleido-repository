/*
 *  Copyright 2008-2021 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.util;

import java.util.ArrayList;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * @author jraduget
 */
public class ObjectHelper {

   /**
    * Returns the first not null value in argument
    * 
    * @param <T>
    * @param value1
    * @param value2
    * @return {@code value1} if {@code value1} if not null, or {@code value2} if {@code value1} is null and {@code value2} not null
    */
   @Nullable
   public static <T> T firstNonNull(final T value1, final T value2) {
	return value1 == null ? value2 : value1;
   }

   /**
    * Returns the first not null value in argument
    * 
    * @param <T>
    * @param value1
    * @param value2
    * @param value3
    * @return first not null value in {value1, value2, value3}
    */
   @Nullable
   public static <T> T firstNonNull(final T value1, final T value2, final T value3) {
	return value1 != null ? value1 : (value2 != null ? value2 : value3);
   }

   /**
    * Returns the first not null value in argument
    * 
    * @param <T>
    * @param values
    * @return first not null value in argument
    */
   @Nullable
   @SafeVarargs
   public static <T> T firstNonNull(final T... values) {
	for (T value : values) {
	   if (value != null) { return value; }
	}
	return null;
   }

   /**
    * Returns a list without the null argument
    * 
    * @param <T>
    * @param values
    * @return list without the null argument
    */
   @NotNull
   @SafeVarargs
   public static <T> List<T> withNoNull(final T... values) {
	List<T> list = new ArrayList<T>();
	if (values != null) {
	   for (T value : values) {
		if (value != null) {
		   list.add(value);
		}
	   }
	}
	return list;
   }

}
