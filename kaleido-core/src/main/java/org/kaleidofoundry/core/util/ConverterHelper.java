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
package org.kaleidofoundry.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Utility class converter
 * 
 * @author Jerome RADUGET
 */
public abstract class ConverterHelper {

   /**
    * Example : <br/>
    * <code>
    * config.file=c:/test.config server.name=toto ....
    * </code> -> Map { {"config.file", "c:/test.."} ; {"server.name", "toto"} ; ... }
    * 
    * @param args
    * @return convert args arrays (main method for example), to map (key -> value) <br/>
    *         (valueDelimiter is '=') <br/>
    *         return null if args is null
    */
   @Nullable
   public static Map<String, String> argsToMap(final String[] args) {
	return argsToMap(args, "=");
   }

   /**
    * Exemple (valueDelimiter is '=') : <br/>
    * <code>
    * config.file=c:/test.config server.name=toto ....
    * </code> -> Map { {"config.file", "c:/test.."} ; {"server.name", "toto"} ; ... }
    * 
    * @param args
    * @param valueDelimiter
    * @return convert args arrays (main method for example), to map (key -> value) <br/>
    *         return null if args or valueDelimiter is null
    */
   @Nullable
   public static Map<String, String> argsToMap(final String[] args, final String valueDelimiter) {
	if (args == null || valueDelimiter == null) { return null; }

	final Map<String, String> results = new HashMap<String, String>();

	for (final String arg : args) {
	   if (arg != null && arg.indexOf(valueDelimiter) >= 0) {
		final String[] paramValue = StringHelper.split(arg, valueDelimiter);
		if (paramValue.length == 1) {
		   results.put(paramValue[0], null);
		} else if (paramValue.length == 2) {
		   results.put(paramValue[0], paramValue[1]);
		} else if (paramValue.length > 2) {
		   final StringBuilder str = new StringBuilder();
		   for (int j = 1; j < paramValue.length; j++) {
			str.append(paramValue[j]).append(j + 1 == paramValue.length ? "" : valueDelimiter);
		   }
		   results.put(paramValue[0], str.toString());
		}
	   }
	}

	return results;
   }

   /**
    * @param values Table of values that we want a string representation
    * @param delimiter Separator value to use
    * @return Converting a String array representation with delemiteur value given in argument <br/>
    *         return null if args values is null
    */
   @Nullable
   public static String arrayToString(final String[] values, final String delimiter) {
	if (values == null) { return null; }

	final StringBuilder buffer = new StringBuilder();
	if (values.length > 0) {
	   for (int i = 0; i < values.length - 1; i++) {
		buffer.append(values[i]).append(delimiter);
	   }
	   buffer.append(values[values.length - 1]);
	}
	return buffer.toString();
   }

   /**
    * @param values Collections of values that we want a string representation
    * @param delimiter Separator value to use
    * @return Converting a collection of elements into a String representation delemiteur with the value given in argument <br/>
    *         use toString() for having element string representation <br/>
    *         return null if args values is null
    */
   @Nullable
   public static String collectionToString(final Collection<?> values, final String delimiter) {
	if (values == null) { return null; }

	final StringBuilder buffer = new StringBuilder();

	for (final Iterator<?> it = values.iterator(); it.hasNext();) {
	   buffer.append(String.valueOf(it.next()));
	   if (it.hasNext()) {
		buffer.append(delimiter);
	   }
	}

	return buffer.toString();
   }

   /**
    * @param values
    * @param delimiter separator
    * @return Converts a string with separator, to a array<br/>
    *         return null if args or delimiter is null
    */
   @Nullable
   public static String[] stringToArray(final String values, final String delimiter) {
	if (values == null || delimiter == null) { return null; }

	final StringTokenizer st = new StringTokenizer(values, delimiter);
	final String[] result = new String[st.countTokens()];

	for (int i = 0; st.hasMoreTokens(); i++) {
	   result[i] = st.nextToken();
	}

	return result;
   }

   /**
    * @param values
    * @param delimiter separator
    * @return Converts a string with separator in list of values<br/>
    *         return null if args or delimiter is null
    */
   @Nullable
   public static List<String> stringToCollection(final String values, final String delimiter) {
	if (values == null || delimiter == null) { return null; }

	final StringTokenizer st = new StringTokenizer(values, delimiter);
	final List<String> result = new ArrayList<String>(st.countTokens());

	while (st.hasMoreTokens()) {
	   result.add(st.nextToken());
	}

	return result;
   }

   /**
    * @param values
    * @param delimiter separator
    * @return Converts a string with separator in LinkedHashSet of values<br/>
    *         return null if args or delimiter is null
    */
   @Nullable
   public static Set<String> stringToSet(final String values, final String delimiter) {
	if (values == null || delimiter == null) { return null; }

	final Set<String> set = new LinkedHashSet<String>();
	set.addAll(stringToCollection(values, delimiter));
	return set;
   }

}
