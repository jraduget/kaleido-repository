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
package org.kaleidofoundry.core.cache;

import java.io.Serializable;

/**
 * 
 * @author jraduget
 *
 */
public class Address implements Serializable, Cloneable {

   private static final long serialVersionUID = -7805257002388054653L;
   
   private String adress1; 
   private String adress2;
   private String zipCode;
   private String city;
   
   public Address(String adress1, String adress2, String zipCode, String city) {
	super();
	this.adress1 = adress1;
	this.adress2 = adress2;
	this.zipCode = zipCode;
	this.city = city;
   }
   
   /**
    * @return the adress1
    */
   public String getAdress1() {
      return adress1;
   }


   /**
    * @param adress1 the adress1 to set
    */
   public void setAdress1(String adress1) {
      this.adress1 = adress1;
   }


   /**
    * @return the adress2
    */
   public String getAdress2() {
      return adress2;
   }


   /**
    * @param adress2 the adress2 to set
    */
   public void setAdress2(String adress2) {
      this.adress2 = adress2;
   }


   /**
    * @return the zipCode
    */
   public String getZipCode() {
      return zipCode;
   }


   /**
    * @param zipCode the zipCode to set
    */
   public void setZipCode(String zipCode) {
      this.zipCode = zipCode;
   }


   /**
    * @return the city
    */
   public String getCity() {
      return city;
   }


   /**
    * @param city the city to set
    */
   public void setCity(String city) {
      this.city = city;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((adress1 == null) ? 0 : adress1.hashCode());
	result = prime * result + ((adress2 == null) ? 0 : adress2.hashCode());
	result = prime * result + ((city == null) ? 0 : city.hashCode());
	result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
	return result;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof Address)) { return false; }
	Address other = (Address) obj;
	if (adress1 == null) {
	   if (other.adress1 != null) { return false; }
	} else if (!adress1.equals(other.adress1)) { return false; }
	if (adress2 == null) {
	   if (other.adress2 != null) { return false; }
	} else if (!adress2.equals(other.adress2)) { return false; }
	if (city == null) {
	   if (other.city != null) { return false; }
	} else if (!city.equals(other.city)) { return false; }
	if (zipCode == null) {
	   if (other.zipCode != null) { return false; }
	} else if (!zipCode.equals(other.zipCode)) { return false; }
	return true;
   }


   public Address clone() {
	try {
	   return (Address) super.clone();
	} catch (final CloneNotSupportedException cne) {
	   return null;
	}
   }
   
   
}
