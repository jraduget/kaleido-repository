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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Typed property accessor
 * 
 * @author Jerome RADUGET
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
   public <T extends Serializable> T getProperty(final String key, final Class<T> type) {
	return _deserialize(getProperty(key), type);
   }

   /**
    * @param key property name
    * @param type type of the return value
    * @return values of the property
    * @param <T>
    */
   public <T extends Serializable> List<T> getPropertyList(final String key, final Class<T> type) {
	return _deserializeToList(getProperty(key), type);
   }

   // ***************************************************************************
   // -> Typed property value accessors
   // ***************************************************************************
   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getString(java.lang.String)
    */
   public String getString(final String key) {
	return getProperty(key, String.class);
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
	return getPropertyList(key, String.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigDecimal(java .lang.String)
    */
   public BigDecimal getBigDecimal(final String key) {
	return getProperty(key, BigDecimal.class);
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
	return getPropertyList(key, BigDecimal.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBigInteger(java .lang.String)
    */
   public BigInteger getBigInteger(final String key) {
	return getProperty(key, BigInteger.class);
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
	return getPropertyList(key, BigInteger.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getBoolean(java. lang.String)
    */
   public Boolean getBoolean(final String key) {
	return getProperty(key, Boolean.class);
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
	return getPropertyList(key, Boolean.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getByte(java.lang .String)
    */
   public Byte getByte(final String key) {
	return getProperty(key, Byte.class);
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
	return getPropertyList(key, Byte.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDouble(java.lang .String)
    */
   public Double getDouble(final String key) {
	return getProperty(key, Double.class);
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
	return getPropertyList(key, Double.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getFloat(java.lang .String)
    */
   public Float getFloat(final String key) {
	return getProperty(key, Float.class);
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
	return getPropertyList(key, Float.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getInt(java.lang .String)
    */
   public Integer getInteger(final String key) {
	return getProperty(key, Integer.class);
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
	return getPropertyList(key, Integer.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getLong(java.lang .String)
    */
   public Long getLong(final String key) {
	return getProperty(key, Long.class);
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
	return getPropertyList(key, Long.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getShort(java.lang .String)
    */
   public Short getShort(final String key) {
	return getProperty(key, Short.class);
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
	return getPropertyList(key, Short.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String)
    */
   public Date getDate(final String key) {
	return getProperty(key, Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.Configuration#getDateList(java.lang.String)
    */
   public List<Date> getDateList(final String key) {
	return getPropertyList(key, Date.class);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.config.config.Configuration#getDate(java.lang .String, java.lang.Date)
    */
   public Date getDate(final String key, final Date defaultValue) {
	final Date d = getDate(key);
	return d == null ? defaultValue : d;
   }

}
