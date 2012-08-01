/*
 *  Copyright 2008-2011 the original author or authors.
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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.UtilMessageBundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.TaskLabel;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Primitives types (java.lang type) toString serializer
 * 
 * @author Jerome RADUGET
 */
@ThreadSafe
public class PrimitiveTypeToStringSerializer implements ToStringSerializer {

   /** The multi-value separator by default is | */
   public static final String DefaultMultiValuesSeparator = "|";
   /** The date format pattern by default */
   public static final String DefaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss"; // yyyy-MM-ddTHH:mm:ss
   /** The number format pattern by default */
   public static final String DefaultNumberFormat = "##0.0####";

   /** The multi-value separator to use */
   protected final String MultiValuesSeparator;
   /** The date format pattern to use */
   protected final String DateFormat;
   /** The number format pattern to use */
   protected final String NumberFormat;

   /**
    * 
    */
   public PrimitiveTypeToStringSerializer() {
	this(DefaultMultiValuesSeparator, DefaultDateFormat, DefaultNumberFormat);
   }

   /**
    * @param multiValuesSeparator
    * @param dateFormat
    * @param numberFormat
    */
   public PrimitiveTypeToStringSerializer(final String multiValuesSeparator, final String dateFormat, final String numberFormat) {
	MultiValuesSeparator = (multiValuesSeparator != null ? multiValuesSeparator : DefaultMultiValuesSeparator);
	DateFormat = dateFormat != null ? dateFormat : DefaultDateFormat;
	NumberFormat = numberFormat != null ? numberFormat : DefaultNumberFormat;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.util.ToStringSerializer#serialize(T, java.lang.Class)
    */
   @Override
   @Task(comment = "use SimpleDateFormat has a thread local", labels = TaskLabel.Enhancement)
   public <T extends Serializable> String serialize(final T value, final Class<T> type) {

	if (value == null) { return null; }

	if (Boolean.class.isAssignableFrom(type)) { return value.toString(); }

	if (Number.class.isAssignableFrom(type)) { return value.toString(); }

	if (Date.class.isAssignableFrom(type) || (value instanceof Date && String.class.isAssignableFrom(type))) { return new SimpleDateFormat(DateFormat)
	.format((Date) value); }

	if (String.class.isAssignableFrom(type)) { return String.valueOf(value); }

	if (Character.class.isAssignableFrom(type)) {
	   String valueStr = String.valueOf(value);
	   return StringHelper.isEmpty(valueStr) ? null : String.valueOf(valueStr.charAt(0));
	}

	throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.illegal.class", type.getName()));

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.util.ToStringSerializer#deserialize(java.lang.String, java.lang.Class)
    */
   @Override
   @SuppressWarnings("unchecked")
   @Task(comment = "use SimpleDateFormat has a thread local", labels = TaskLabel.Enhancement)
   public <T extends Serializable> T deserialize(final String value, final Class<T> type) throws IllegalStateException {

	if (value == null) { return null; }

	if (Boolean.class.isAssignableFrom(type)) { return (T) Boolean.valueOf(value); }

	if (Number.class.isAssignableFrom(type)) {
	   try {
		if (Byte.class == type) { return (T) Byte.valueOf(value); }
		if (Short.class == type) { return (T) Short.valueOf(value); }
		if (Integer.class == type) { return (T) Integer.valueOf(value); }
		if (Long.class == type) { return (T) Long.valueOf(value); }
		if (Float.class == type) { return (T) Float.valueOf(value); }
		if (Double.class == type) { return (T) Double.valueOf(value); }
		if (BigInteger.class == type) { return (T) new BigInteger(value); }
		if (BigDecimal.class == type) { return (T) new BigDecimal(value); }
	   } catch (final NumberFormatException nfe) {
		throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.number.format.error", value), nfe);
	   }
	}

	if (Date.class.isAssignableFrom(type)) {
	   try {
		return (T) new SimpleDateFormat(DateFormat).parse(value);
	   } catch (final ParseException pe) {
		throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.date.format.error", value, DateFormat), pe);
	   }
	}

	if (String.class.isAssignableFrom(type)) { return (T) (StringHelper.isEmpty(value) ? "" : value); }

	if (Character.class.isAssignableFrom(type)) { return (T) (StringHelper.isEmpty(value) ? null : Character.valueOf(value.charAt(0))); }

	throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.illegal.class", type.getName()));

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.util.ToStringSerializer#serialize(java.util.Collection, java.lang.Class)
    */
   @Override
   public <T extends Serializable> String serialize(final Collection<T> values, final Class<T> type) {

	if (values == null) {
	   return null;
	} else {
	   List<String> valuesList = new LinkedList<String>();
	   for (T v : values) {
		valuesList.add(serialize(v, type));
	   }
	   return StringHelper.unsplit(MultiValuesSeparator, valuesList.toArray());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.util.ToStringSerializer#deserializeToList(java.lang.String, java.lang.Class)
    */
   @Override
   public <T extends Serializable> List<T> deserializeToList(final String values, final Class<T> type) {

	if (values == null) { return null; }

	List<T> result = null;

	if (!StringHelper.isEmpty(values)) {
	   result = new LinkedList<T>();
	   final StringTokenizer strToken = new StringTokenizer(values, MultiValuesSeparator);
	   while (strToken.hasMoreTokens()) {
		result.add(deserialize(strToken.nextToken(), type));
	   }
	}

	return result;
   }



   /**
    * convert a value to another type
    * 
    * @param <T>
    * @param value
    * @param type
    * @return
    * @throws IllegalArgumentException
    */
   @SuppressWarnings("unchecked")
   protected <T extends Serializable> T convert(final Serializable value, final Class<T> type) {

	if (value == null) { return null; }

	if (type.isAssignableFrom(value.getClass())) {
	   return (T) value;
	} else if (value instanceof String) {
	   return deserialize((String) value, type);
	} else if (BigDecimal.class.isAssignableFrom(type)) {
	   return (T) new BigDecimal(value.toString());
	} else if (BigInteger.class.isAssignableFrom(type)) {
	   return (T) new BigInteger(value.toString());
	} else if (Long.class.isAssignableFrom(type)) {
	   return (T) new Long(value.toString());
	} else if (Integer.class.isAssignableFrom(type)) {
	   return (T) new Integer(value.toString());
	} else if (Double.class.isAssignableFrom(type)) {
	   return (T) new Double(value.toString());
	} else if (Float.class.isAssignableFrom(type)) {
	   return (T) new Float(value.toString());
	} else if (Short.class.isAssignableFrom(type)) {
	   return (T) new Short(value.toString());
	} else if (type.isAssignableFrom(String.class)) {
	   return (T) serialize((T) value, type);
	} else {
	   throw new IllegalArgumentException(UtilMessageBundle.getMessage("serializer.illegal.argument", value.getClass().getName(), type.getName()));
	}

   }

   /**
    * convert a list value to another list type
    * 
    * @param <T>
    * @param values
    * @param type
    * @return
    * @throws IllegalArgumentException
    */
   @SuppressWarnings("unchecked")
   protected <T extends Serializable> List<T> convertToList(final Serializable values, final Class<T> type) {

	if (values == null) { return null; }

	if (values instanceof String) {
	   return deserializeToList((String) values, type);
	} else if (values instanceof Collection) {
	   Collection<? extends Serializable> current = (Collection<? extends Serializable>) values;
	   List<T> result = new ArrayList<T>();
	   for (Serializable item : current) {
		result.add(convert(item, type));
	   }
	   return result;
	} else {
	   return Arrays.asList(convert(values, type));
	}
   }

}
