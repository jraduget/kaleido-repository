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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.UtilMessageBundle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.kaleidofoundry.core.lang.NotYetImplementedException;
import org.kaleidofoundry.core.lang.annotation.Review;
import org.kaleidofoundry.core.lang.annotation.ReviewCategoryEnum;

/**
 * @author Jerome RADUGET
 */
public abstract class AbstractSerializer {

   /** Multiple value separator */
   public static final String DefaultMultiValuesSeparator = " ";
   /** String Date Formatter */
   public static final String DefaultDateFormat = "yyyy-MM-dd'T'hh:mm:ss"; // yyyy-MM-ddThh:mm:ss
   /** String Number Formatter */
   public static final String DefaultNumberFormat = "##0.0####";

   /** Multiple value separator */
   protected final String MultiValuesSeparator;
   /** Date Formatter */
   protected final String DateFormat;
   /** Number Formatter */
   protected final String NumberFormat;

   /**
    * 
    */
   public AbstractSerializer() {
	this(DefaultMultiValuesSeparator, DefaultDateFormat, DefaultNumberFormat);
   }

   /**
    * @param multiValuesSeparator
    * @param dateFormat
    * @param numberFormat
    */
   public AbstractSerializer(final String multiValuesSeparator, final String dateFormat, final String numberFormat) {
	MultiValuesSeparator = (multiValuesSeparator != null ? multiValuesSeparator : DefaultMultiValuesSeparator);
	DateFormat = dateFormat != null ? dateFormat : DefaultDateFormat;
	NumberFormat = numberFormat != null ? numberFormat : DefaultNumberFormat;
   }

   /**
    * @param key
    * @return value of the property
    */
   public abstract Serializable getProperty(final String key);

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************
   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getString(java.lang.String)
    */
   public String getString(final String key) {
	return valueOf(getProperty(key), String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getString(java.lang .String, java.lang.String)
    */
   public String getString(final String key, final String defaultValue) {
	final String s = getString(key);
	return s == null ? defaultValue : s;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getStringList(java.lang.String)
    */
   public List<String> getStringList(final String key) {
	return valuesOf(getProperty(key), String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String)
    */
   public BigDecimal getBigDecimal(final String key) {
	return valueOf(getProperty(key), BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String, java.math.BigDecimal)
    */
   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
	final BigDecimal bd = getBigDecimal(key);
	return bd == null ? defaultValue : bd;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimalList (java.lang.String)
    */
   public List<BigDecimal> getBigDecimalList(final String key) {
	return valuesOf(getProperty(key), BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String)
    */
   public BigInteger getBigInteger(final String key) {
	return valueOf(getProperty(key), BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String, java.math.BigInteger)
    */
   public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
	final BigInteger bi = getBigInteger(key);
	return bi == null ? defaultValue : bi;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigIntegerList (java.lang.String)
    */
   public List<BigInteger> getBigIntegerList(final String key) {
	return valuesOf(getProperty(key), BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String)
    */
   public Boolean getBoolean(final String key) {
	return valueOf(getProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String, java.lang.Boolean)
    */
   public Boolean getBoolean(final String key, final Boolean defaultValue) {
	final Boolean b = getBoolean(key);
	return b == null ? defaultValue : b;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBooleanList( java.lang.String)
    */
   public List<Boolean> getBooleanList(final String key) {
	return valuesOf(getProperty(key), Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String)
    */
   public Byte getByte(final String key) {
	return valueOf(getProperty(key), Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String, java.lang.Byte)
    */
   public Byte getByte(final String key, final Byte defaultValue) {
	final Byte b = getByte(key);
	return b == null ? defaultValue : b;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByteList(java .lang.String)
    */
   public List<Byte> getByteList(final String key) {
	return valuesOf(getProperty(key), Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String)
    */
   public Double getDouble(final String key) {
	return valueOf(getProperty(key), Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String, double)
    */
   public Double getDouble(final String key, final Double defaultValue) {
	final Double d = getDouble(key);
	return d == null ? defaultValue : d;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDoubleList(java .lang.String)
    */
   public List<Double> getDoubleList(final String key) {
	return valuesOf(getProperty(key), Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String)
    */
   public Float getFloat(final String key) {
	return valueOf(getProperty(key), Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String, java.lang.Float)
    */
   public Float getFloat(final String key, final Float defaultValue) {
	final Float f = getFloat(key);
	return f == null ? defaultValue : f;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloatList(java .lang.String)
    */
   public List<Float> getFloatList(final String key) {
	return valuesOf(getProperty(key), Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInt(java.lang .String)
    */
   public Integer getInteger(final String key) {
	return valueOf(getProperty(key), Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInteger(java. lang.String, java.lang.Integer)
    */
   public Integer getInteger(final String key, final Integer defaultValue) {
	final Integer i = getInteger(key);
	return i == null ? defaultValue : i;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getIntList(java .lang.String)
    */
   public List<Integer> getIntegerList(final String key) {
	return valuesOf(getProperty(key), Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String)
    */
   public Long getLong(final String key) {
	return valueOf(getProperty(key), Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String, java.lang.Long)
    */
   public Long getLong(final String key, final Long defaultValue) {
	final Long l = getLong(key);
	return l == null ? defaultValue : l;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLongList(java .lang.String)
    */
   public List<Long> getLongList(final String key) {
	return valuesOf(getProperty(key), Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String)
    */
   public Short getShort(final String key) {
	return valueOf(getProperty(key), Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String, java.lang.Short)
    */
   public Short getShort(final String key, final Short defaultValue) {
	final Short s = getShort(key);
	return s == null ? defaultValue : s;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShortList(java .lang.String)
    */
   public List<Short> getShortList(final String key) {
	return valuesOf(getProperty(key), Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String)
    */
   public Date getDate(final String key) {
	return valueOf(getProperty(key), Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getDateList(java.lang.String)
    */
   public List<Date> getDateList(final String key) {
	return valuesOf(getProperty(key), Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String, java.lang.Date)
    */
   public Date getDate(final String key, final Date defaultValue) {
	final Date d = getDate(key);
	return d == null ? defaultValue : d;
   }

   /**
    * String to Object Converter
    * 
    * @param <T>
    * @param value
    * @param cl Target class
    * @return Requested conversion of the string argument. If {@link NumberFormatException} or date {@link ParseException}, silent exception
    *         will be logged, and null return
    * @throws IllegalStateException for date or number parse error
    */
   @SuppressWarnings("unchecked")
   @Review(comment = "use SimpleDateFormat has a thread local", category = ReviewCategoryEnum.Improvement)
   public <T> T valueOf(final Serializable value, final Class<T> cl) throws IllegalStateException {

	if (value == null) { return null; }

	if (value instanceof String) {

	   final String strValue = (String) value;

	   if (Boolean.class.isAssignableFrom(cl)) { return (T) Boolean.valueOf(strValue); }

	   if (Number.class.isAssignableFrom(cl)) {
		try {
		   if (Byte.class == cl) { return (T) Byte.valueOf(strValue); }
		   if (Short.class == cl) { return (T) Short.valueOf(strValue); }
		   if (Integer.class == cl) { return (T) Integer.valueOf(strValue); }
		   if (Long.class == cl) { return (T) Long.valueOf(strValue); }
		   if (Float.class == cl) { return (T) Float.valueOf(strValue); }
		   if (Double.class == cl) { return (T) Double.valueOf(strValue); }
		   if (BigInteger.class == cl) { return (T) new BigInteger(strValue); }
		   if (BigDecimal.class == cl) { return (T) new BigDecimal(strValue); }
		} catch (final NumberFormatException nfe) {
		   throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.number.format.error", strValue), nfe);
		}
	   }

	   if (Date.class.isAssignableFrom(cl)) {
		try {
		   return (T) new SimpleDateFormat(DateFormat).parse(strValue);
		} catch (final ParseException pe) {
		   throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.date.format.error", strValue, DateFormat), pe);
		}
	   }

	   if (String.class.isAssignableFrom(cl)) { return (T) (StringHelper.isEmpty(strValue) ? "" : strValue); }

	   throw new IllegalStateException(UtilMessageBundle.getMessage("serializer.illegal.class", cl.getName()));

	} else if (value instanceof Number) {
	   throw new NotYetImplementedException();
	} else if (value instanceof Boolean) {
	   throw new NotYetImplementedException();
	} else if (value instanceof Date) { throw new NotYetImplementedException(); }

	return null;
   }

   /**
    * @param <T>
    * @param values can be a String with multiple values separate by {@link #DefaultMultiValuesSeparator}, or
    * @param cl
    * @return multiple value
    */
   public <T> List<T> valuesOf(final Serializable values, final Class<T> cl) {

	if (values == null) { return null; }

	List<T> result = null;
	if (values instanceof String) {
	   final String strValue = (String) values;
	   if (!StringHelper.isEmpty(strValue)) {
		result = new LinkedList<T>();
		final StringTokenizer strToken = new StringTokenizer(strValue, MultiValuesSeparator);
		while (strToken.hasMoreTokens()) {
		   result.add(valueOf(strToken.nextToken(), cl));
		}
	   }
	} else {
	   throw new NotYetImplementedException();
	}
	return result;
   }

   /**
    * {@link Serializable} to String Converter
    * 
    * @param value instance to convert
    * @return String conversion of the requested object
    */
   @Review(comment = "use SimpleDateFormat has a thread local", category = ReviewCategoryEnum.Improvement)
   protected String serializableToString(final Serializable value) {

	if (value != null) {

	   if (value instanceof Number) {
		return ((Number) value).toString();
	   } else if (value instanceof Date) {
		return new SimpleDateFormat(DateFormat).format(value);
	   } else if (value instanceof String) {
		return (String) value;
	   } else if (value instanceof Boolean) {
		return String.valueOf(value);
	   } else if (value instanceof Byte) {
		return String.valueOf(value);
	   } else {
		return value.toString();
	   }
	} else {
	   return null;
	}
   }
}
