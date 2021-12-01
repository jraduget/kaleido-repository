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
package org.kaleidofoundry.core.util.properties;

import java.util.Properties;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.lang.annotation.Nullable;

/**
 * Extension of Properties, managing multiple property values... <br/>
 * <code>
 * application.name="fooApp"
 * application.modules=module1 module2
 * </code>
 * 
 * @author jraduget
 */
public class ExtendedProperties extends Properties {

   private static final long serialVersionUID = 8223542956966149283L;

   /**
    * @param key the identifier of the property you want to get the value
    * @return multiple raw values of the property
    */
   @Nullable
   public String[] getMultiValueProperty(final String key) {
	final String values = super.getProperty(key);
	if (values == null) { return null; }

	final StringTokenizer st = new StringTokenizer(values, " ");
	final String[] result = new String[st.countTokens()];

	for (int i = 0; st.hasMoreTokens(); i++) {
	   result[i] = st.nextToken();
	}

	return result;
   }

   /**
    * @param key the identifier of the property you want to get the value
    * @param valueSeparator the values separator to use
    * @return multiple raw values of the property
    */
   @Nullable
   public String[] getMultiValueProperty(final String key, final String valueSeparator) {
	final String values = super.getProperty(key);
	if (values == null) { return null; }
	final StringTokenizer st = new StringTokenizer(values, valueSeparator);

	final String[] result = new String[st.countTokens()];

	for (int i = 0; st.hasMoreTokens(); i++) {
	   result[i] = st.nextToken();
	}

	return result;
   }

   /**
    * @param key the identifier of the property you want to change the value
    * @param values values to set to the key property (the valueSeparator will be a single space)
    * @return the previous value of the specified key in this property list, or null if it did not have one.
    */
   @Nullable
   public String setMultiValueProperty(final String key, final String... values) {
	final StringBuilder buffer = new StringBuilder();

	for (int i = 0; i < values.length - 1; i++) {
	   buffer.append(values[i] + ' ');
	}
	buffer.append(values[values.length - 1]);

	final String result = (String) super.setProperty(key, buffer.toString());

	return result;
   }

   /**
    * @param key the identifier of the property you want to change the value
    * @param valueSeparator the values separator to use
    * @param values values to set to the key property
    * @return the previous value of the specified key in this property list, or null if it did not have one.
    */
   @Nullable
   public String setMultiValueProperty(final String key, final String valueSeparator, final String... values) {
	final StringBuilder buffer = new StringBuilder();

	for (int i = 0; i < values.length - 1; i++) {
	   buffer.append(values[i] + valueSeparator);
	}
	buffer.append(values[values.length - 1]);

	final String result = (String) super.setProperty(key, buffer.toString());

	return result;
   }

}