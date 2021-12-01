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
package org.kaleidofoundry.messaging;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * JavaBean Sample for Messaging transfert (JMS / Tibco...)
 * 
 * @author jraduget
 */
public class SerializableBeanSample implements Serializable {

   private static final long serialVersionUID = -3322515952639901764L;

   private String name;
   private String firstName;
   private Date birthDate;
   private int age;

   public SerializableBeanSample() {
	setName("name");
	setFirstName("firstName");
	setBirthDate(new GregorianCalendar(2008, 02, 06, 0, 0, 0).getTime());
	setAge(29);
   }

   public String getName() {
	return name;
   }

   public void setName(final String name) {
	this.name = name;
   }

   public String getFirstName() {
	return firstName;
   }

   public void setFirstName(final String firstName) {
	this.firstName = firstName;
   }

   public Date getBirthDate() {
	return birthDate;
   }

   public void setBirthDate(final Date birthDate) {
	this.birthDate = birthDate;
   }

   public int getAge() {
	return age;
   }

   public void setAge(final int age) {
	this.age = age;
   }

   @Override
   public String toString() {
	final StringBuilder str = new StringBuilder();
	str.append("{");
	str.append("name=").append(getName()).append(";");
	str.append("firstName=").append(getFirstName()).append(";");
	str.append("birthDate=").append(getBirthDate()).append(";");
	str.append("age=").append(getAge());
	str.append("}");
	return str.toString();
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + age;
	result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
   }

   @Override
   public boolean equals(Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof SerializableBeanSample)) { return false; }
	SerializableBeanSample other = (SerializableBeanSample) obj;
	if (age != other.age) { return false; }
	if (birthDate == null) {
	   if (other.birthDate != null) { return false; }
	} else if (!birthDate.equals(other.birthDate)) { return false; }
	if (firstName == null) {
	   if (other.firstName != null) { return false; }
	} else if (!firstName.equals(other.firstName)) { return false; }
	if (name == null) {
	   if (other.name != null) { return false; }
	} else if (!name.equals(other.name)) { return false; }
	return true;
   }

}
