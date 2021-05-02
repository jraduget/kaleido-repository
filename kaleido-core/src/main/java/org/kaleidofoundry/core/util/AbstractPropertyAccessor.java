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
package org.kaleidofoundry.core.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Typed property accessors
 * 
 * @author jraduget
 */
@ThreadSafe
public abstract class AbstractPropertyAccessor extends PrimitiveTypeToStringSerializer {

   /**
    * 
    */
   public AbstractPropertyAccessor() {
	super();
   }

   /**
    * @param multiValuesSeparator
    * @param dateFormat
    * @param numberFormat
    */
   public AbstractPropertyAccessor(final String multiValuesSeparator, final String dateFormat, final String numberFormat) {
	super(multiValuesSeparator, dateFormat, numberFormat);
   }

   /**
    * @param key
    * @return value of the property
    */
   public abstract Serializable getProperty(final String key);

   /**
    * @param key property name
    * @param type type of the return value
    * @return value of the property
    * @param <T>
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <T extends Serializable> T getProperty(final String key, final Class<T> type) {

	Serializable value = getProperty(key);

	if (value == null) {
	   return null;
	} else if (type.isAssignableFrom(value.getClass())) {
	   return (T) value;
	} else if (value instanceof Collection) {
	   return (T) serialize((Collection) value, type);	   
	} else if (String.class == type) {
	   return (T) serialize((T) value, type);
	} else {	    
	    if (type.isArray()) {
		  return (T) convertToList(value, type).toArray(new Object[0]);
	    } else {
		  return convert(value, type);
	    }
	}
   }

   /**
    * @param key property name
    * @param type type of the return value
    * @return values of the property
    * @param <T>
    */
   @SuppressWarnings("unchecked")
   public <T extends Serializable> List<T> getPropertyList(final String key, final Class<T> type) {

	Serializable value = getProperty(key);

	if (value == null) {
	   return Collections.emptyList();
	} else if (value instanceof Collection) {

	   List<T> values = new ArrayList<T>();
	   for (Object v : (Collection<?>) value) {
		if (v == null) {
		   values.add(null);
		} else if (type.isAssignableFrom(v.getClass())) {
		   values.add((T) v);
		} else if (String.class == type) {
		   values.add((T) serialize((T) v, type));
		} else if (Character.class == type) {
		   T sv = (T) serialize((T) v, type);
		   String csv;
		   if (sv instanceof String) {
			csv = (String) sv;
			values.add(StringHelper.isEmpty(csv) ? null : (T) Character.valueOf(csv.charAt(0)));
		   } else {
			csv = String.valueOf(sv);
			values.add((T) Character.valueOf(csv.charAt(0)));
		   }
		} else {
		   values.add(convert((Serializable) v, type));
		}
	   }
	   return values;

	} else {
	   return convertToList(value, type);
	}

   }

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************
   public Character getCharacter(final String key) {
	return getProperty(key, Character.class);
   }

   public Character getCharacter(final String key, final Character defaultValue) {
	final Character s = getCharacter(key);
	return s == null ? defaultValue : s;
   }

   public List<Character> getCharacterList(final String key) {
	return getPropertyList(key, Character.class);
   }

   public String getString(final String key) {
	return getProperty(key, String.class);
   }

   public String getString(final String key, final String defaultValue) {
	final String s = getString(key);
	return s == null ? defaultValue : s;
   }

   public List<String> getStringList(final String key) {
	return getPropertyList(key, String.class);
   }

   public String[] getStrings(final String key) {
	return getPropertyList(key, String.class).toArray(new String[0]);
   }

   public String[] getStrings(final String key, final String[] defaultValues) {
	final String[] values = getStrings(key);
	return values == null || values.length == 0 ? defaultValues : values;
   }
   
   public BigDecimal getBigDecimal(final String key) {
	return getProperty(key, BigDecimal.class);
   }

   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue) {
	final BigDecimal bd = getBigDecimal(key);
	return bd == null ? defaultValue : bd;
   }

   public List<BigDecimal> getBigDecimalList(final String key) {
	return getPropertyList(key, BigDecimal.class);
   }
   
   public BigDecimal[] getBigDecimals(final String key) {
	return getPropertyList(key, BigDecimal.class).toArray(new BigDecimal[0]);
   }

   public BigDecimal[] getBigDecimals(final String key, final BigDecimal[] defaultValues) {
	final BigDecimal[] values = getBigDecimals(key);
	return values == null || values.length == 0 ? defaultValues : values;
   }   

   public BigInteger getBigInteger(final String key) {
	return getProperty(key, BigInteger.class);
   }

   public BigInteger getBigInteger(final String key, final BigInteger defaultValue) {
	final BigInteger bi = getBigInteger(key);
	return bi == null ? defaultValue : bi;
   }

   public List<BigInteger> getBigIntegerList(final String key) {
	return getPropertyList(key, BigInteger.class);
   }

   public BigInteger[] getBigIntegers(final String key) {
	return getPropertyList(key, BigInteger.class).toArray(new BigInteger[0]);
   }

   public BigInteger[] getBigIntegers(final String key, final BigInteger[] defaultValues) {
	final BigInteger[] values = getBigIntegers(key);
	return values == null || values.length == 0 ? defaultValues : values;
   }   
   
   public Boolean getBoolean(final String key) {
	return getProperty(key, Boolean.class);
   }

   public Boolean getBoolean(final String key, final Boolean defaultValue) {
	final Boolean b = getBoolean(key);
	return b == null ? defaultValue : b;
   }

   public List<Boolean> getBooleanList(final String key) {
	return getPropertyList(key, Boolean.class);
   }

   public Boolean[] getBooleans(final String key) {	
	return getPropertyList(key, Boolean.class).toArray(new Boolean[0]);
   }

   public Boolean[] getBooleans(final String key, final Boolean[] defaultValues) {
	final Boolean[] values = getBooleans(key);
	return values == null || values.length == 0 ? defaultValues : values;
   }  
   
   public Byte getByte(final String key) {
	return getProperty(key, Byte.class);
   }

   public Byte getByte(final String key, final Byte defaultValue) {
	final Byte b = getByte(key);
	return b == null ? defaultValue : b;
   }

   public List<Byte> getByteList(final String key) {
	return getPropertyList(key, Byte.class);
   }

   public Byte[] getBytes(final String key) {
	return getPropertyList(key, Byte.class).toArray(new Byte[0]);
   }

   public Byte[] getBytes(final String key, final Byte[] defaultValues) {
	final Byte[] values = getBytes(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   public Double getDouble(final String key) {
	return getProperty(key, Double.class);
   }

   public Double getDouble(final String key, final Double defaultValue) {
	final Double d = getDouble(key);
	return d == null ? defaultValue : d;
   }

   public List<Double> getDoubleList(final String key) {
	return getPropertyList(key, Double.class);
   }


   public Double[] getDoubles(final String key) {
	return getPropertyList(key, Double.class).toArray(new Double[0]);
   }

   public Double[] getDoubles(final String key, final Double[] defaultValues) {
	final Double[] values = getDoubles(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   public Float getFloat(final String key) {
	return getProperty(key, Float.class);
   }

   public Float getFloat(final String key, final Float defaultValue) {
	final Float f = getFloat(key);
	return f == null ? defaultValue : f;
   }

   public List<Float> getFloatList(final String key) {
	return getPropertyList(key, Float.class);
   }

   public Float[] getFloats(final String key) {
	return getPropertyList(key, Float.class).toArray(new Float[0]);
   }

   public Float[] getFloats(final String key, final Float[] defaultValues) {
	final Float[] values = getFloats(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   
   public Integer getInteger(final String key) {
	return getProperty(key, Integer.class);
   }

   public Integer getInteger(final String key, final Integer defaultValue) {
	final Integer i = getInteger(key);
	return i == null ? defaultValue : i;
   }

   public List<Integer> getIntegerList(final String key) {
	return getPropertyList(key, Integer.class);
   }

   public Integer[] getIntegers(final String key) {
	return getPropertyList(key, Integer.class).toArray(new Integer[0]);
   }

   public Integer[] getIntegers(final String key, final Integer[] defaultValues) {
	final Integer[] values = getIntegers(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   
   public Long getLong(final String key) {
	return getProperty(key, Long.class);
   }

   public Long getLong(final String key, final Long defaultValue) {
	final Long l = getLong(key);
	return l == null ? defaultValue : l;
   }

   public List<Long> getLongList(final String key) {
	return getPropertyList(key, Long.class);
   }

   public Long[] getLongs(final String key) {
	return getPropertyList(key, Long.class).toArray(new Long[0]);
   }

   public Long[] getLongs(final String key, final Long[] defaultValues) {
	final Long[] values = getLongs(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   
   public Short getShort(final String key) {
	return getProperty(key, Short.class);
   }

   public Short getShort(final String key, final Short defaultValue) {
	final Short s = getShort(key);
	return s == null ? defaultValue : s;
   }

   public List<Short> getShortList(final String key) {
	return getPropertyList(key, Short.class);
   }

   public Short[] getShorts(final String key) {
	return getPropertyList(key, Short.class).toArray(new Short[0]);
   }

   public Short[] getShorts(final String key, final Short[] defaultValues) {
	final Short[] values = getShorts(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   
   public Date getDate(final String key) {
	return getProperty(key, Date.class);
   }

   public List<Date> getDateList(final String key) {
	return getPropertyList(key, Date.class);
   }

   public Date getDate(final String key, final Date defaultValue) {
	final Date d = getDate(key);
	return d == null ? defaultValue : d;
   }

   public Date[] getDates(final String key) {
	return getPropertyList(key, Date.class).toArray(new Date[0]);
   }

   public Date[] getDates(final String key, final Date[] defaultValues) {
	final Date[] values = getDates(key);
	return values == null || values.length == 0 ? defaultValues : values;
   } 
   
   
}
