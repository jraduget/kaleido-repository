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
package org.kaleidofoundry.core.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Person entity test
 * 
 * @author jraduget
 */
@Entity
public class PersonEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personEntityGen")
   Integer id;
   private String firstname;
   private String lastname;
   @Temporal(TemporalType.DATE)
   private Date birthdate;

   public PersonEntity() {
   }

   public PersonEntity(final String firstname, final String lastname, final Date birthdate) {
	super();
	this.firstname = firstname;
	this.lastname = lastname;
	this.birthdate = birthdate;
   }

   public String getFirstname() {
	return firstname;
   }

   public void setFirstname(final String firstname) {
	this.firstname = firstname;
   }

   public String getLastname() {
	return lastname;
   }

   public void setLastname(final String lastname) {
	this.lastname = lastname;
   }

   public Date getBirthdate() {
	return birthdate;
   }

   public void setBirthdate(final Date birthdate) {
	this.birthdate = birthdate;
   }

   public Integer getId() {
	return id;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (birthdate == null ? 0 : birthdate.hashCode());
	result = prime * result + (firstname == null ? 0 : firstname.hashCode());
	result = prime * result + (id == null ? 0 : id.hashCode());
	result = prime * result + (lastname == null ? 0 : lastname.hashCode());
	return result;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof PersonEntity)) { return false; }
	PersonEntity other = (PersonEntity) obj;
	if (birthdate == null) {
	   if (other.birthdate != null) { return false; }
	} else if (!birthdate.equals(other.birthdate)) { return false; }
	if (firstname == null) {
	   if (other.firstname != null) { return false; }
	} else if (!firstname.equals(other.firstname)) { return false; }
	if (id == null) {
	   if (other.id != null) { return false; }
	} else if (!id.equals(other.id)) { return false; }
	if (lastname == null) {
	   if (other.lastname != null) { return false; }
	} else if (!lastname.equals(other.lastname)) { return false; }
	return true;
   }

}
