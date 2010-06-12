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
   private Adress mainAdress;
   private final List<Adress> adresses;

   public Person() {
	adresses = new LinkedList<Adress>();
   }

   public Person(Integer id, String lastName, String firstName, Date birthdate, Adress mainAdress) {
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
   public Person addAdress(Adress a) {
	if (a != null) {
	   adresses.add(a);
	}
	return this;
   }

   /**
    * @param a
    * @return person instance
    */
   public Person removeAdress(Adress a) {
	if (a != null) {
	   adresses.remove(a);
	}
	return this;
   }

   /**
    * @return the adresses
    */
   public List<Adress> getAdresses() {
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
   public Adress getMainAdress() {
	return mainAdress;
   }

   /**
    * @param mainAdress the mainAdress to set
    */
   public void setMainAdress(Adress mainAdress) {
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
