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
package org.kaleidofoundry.core.cache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Mock test class
 * 
 * @author Jerome RADUGET
 */
public class Person implements Serializable, Cloneable {

   private static final long serialVersionUID = -3988383210224387387L;

   private Integer id;
   private String lastName;
   private String firstName;
   private Date birthdate;
   private Address mainAdress;
   private final List<Address> adresses;

   public Person() {
	adresses = new LinkedList<Address>();
   }

   public Person(Integer id, String lastName, String firstName, Date birthdate, Address mainAdress) {
	this();
	this.id = id;
	this.lastName = lastName;
	this.firstName = firstName;
	this.birthdate = birthdate;
	this.mainAdress = mainAdress;
   }

   /**
    * @param a
    * @return person instance
    */
   public Person addAdress(Address a) {
	if (a != null) {
	   adresses.add(a);
	}
	return this;
   }

   /**
    * @param a
    * @return person instance
    */
   public Person removeAdress(Address a) {
	if (a != null) {
	   adresses.remove(a);
	}
	return this;
   }

   /**
    * @return the adresses
    */
   public List<Address> getAdresses() {
	return adresses;
   }

   /**
    * @return the id
    */
   public Integer getId() {
	return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(Integer id) {
	this.id = id;
   }

   /**
    * @return the lastName
    */
   public String getLastName() {
	return lastName;
   }

   /**
    * @param lastName the lastName to set
    */
   public void setLastName(String lastName) {
	this.lastName = lastName;
   }

   /**
    * @return the firstName
    */
   public String getFirstName() {
	return firstName;
   }

   /**
    * @param firstName the firstName to set
    */
   public void setFirstName(String firstName) {
	this.firstName = firstName;
   }

   /**
    * @return the birthdate
    */
   public Date getBirthdate() {
	return birthdate;
   }

   /**
    * @param birthdate the birthdate to set
    */
   public void setBirthdate(Date birthdate) {
	this.birthdate = birthdate;
   }

   /**
    * @return the mainAdress
    */
   public Address getMainAdress() {
	return mainAdress;
   }

   /**
    * @param mainAdress the mainAdress to set
    */
   public void setMainAdress(Address mainAdress) {
	this.mainAdress = mainAdress;
   }

   @Override
   public Person clone() {
	try {
	   return (Person) super.clone();
	} catch (final CloneNotSupportedException cne) {
	   return null;
	}
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof Person)) { return false; }
	Person other = (Person) obj;
	if (id == null) {
	   if (other.id != null) { return false; }
	} else if (!id.equals(other.id)) { return false; }
	return true;
   }

   /**
    * @return static mock instance
    */
   public static Person newMockInstance() {
	final Calendar birthdate = new GregorianCalendar();
	final Person mockPerson = new Person();
	mockPerson.id = 10;
	mockPerson.firstName = "firstname";
	mockPerson.lastName = "lastname";

	birthdate.set(Calendar.YEAR, 2009);
	birthdate.set(Calendar.MONTH, 10);
	birthdate.set(Calendar.DAY_OF_MONTH, 1);
	birthdate.set(Calendar.SECOND, 0);
	birthdate.set(Calendar.MILLISECOND, 0);
	birthdate.set(Calendar.MINUTE, 0);
	birthdate.set(Calendar.HOUR, 0);
	mockPerson.birthdate = birthdate.getTime();
	return mockPerson;
   }
}
