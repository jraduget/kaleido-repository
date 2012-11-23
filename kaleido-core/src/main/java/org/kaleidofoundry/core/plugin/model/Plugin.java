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
package org.kaleidofoundry.core.plugin.model;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.plugin.ClassXmlAdpater;
import org.kaleidofoundry.core.plugin.Declare;

/**
 * Plugin bean class representation <br/>
 * 
 * @author Jerome RADUGET
 * @param <T> Interface plugin type
 */
@XmlRootElement(name = "plugin")
@XmlAccessorType(XmlAccessType.FIELD)
@Immutable
public class Plugin<T> implements Serializable, Comparator<Plugin<T>> {

   private static final long serialVersionUID = -5591060344949290055L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************
   private final String name;
   private final boolean standard;
   @XmlJavaTypeAdapter(ClassXmlAdpater.class)
   private final Class<? extends T> annotatedClass;
   private final String description;
   private final String version;
   private final boolean enable;

   // CONSTRUCTOR *****************************************************************************************************
   
   // JAXB needed
   protected Plugin() {
	this.name = null;
	this.standard = false;
	this.annotatedClass = null;
	this.description = null;
	this.version = null;
	this.enable = false;
   }
   
   /**
    * Field constructor
    * 
    * @param name
    * @param standard
    * @param annotatedClass
    * @param description
    * @param version
    * @param enable
    */
   public Plugin(final String name, final boolean standard, final Class<? extends T> annotatedClass, final String description, final String version,
	   final boolean enable) {
	this.name = name;
	this.standard = standard;
	this.annotatedClass = annotatedClass;
	this.description = description;
	this.version = version;
	this.enable = enable;
   }

   /**
    * @return does the plugin is an interface
    */
   public boolean isInterface() {
	return annotatedClass.isInterface();
   }

   /**
    * @return does the plugin is an class implementation
    */
   public boolean isClass() {
	return !annotatedClass.isInterface() && !Modifier.isAbstract(annotatedClass.getModifiers());
   }

   // GETTER & SETTERS FOR POJO ***************************************************************************************

   /**
    * @return unique name of the plugin
    */
   public String getName() {
	return name;
   }

   /**
    * @return is plugin is project standard
    */
   public boolean isStandard() {
	return standard;
   }

   /**
    * @return annotated {@link Declare} class
    */
   public Class<? extends T> getAnnotatedClass() {
	return annotatedClass;
   }

   /**
    * @return the description
    */
   public String getDescription() {
	return description;
   }

   /**
    * @return is plugin enable
    */
   public boolean isEnable() {
	return enable;
   }

   /**
    * @return version information
    */
   public String getVersion() {
	return version;
   }

   // EQUALS / HASHCODE / TOSTRING / COMPARE / CLONE... FOR POJO ******************************************************

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	final StringBuilder str = new StringBuilder();
	str.append(name).append(" -> ").append(annotatedClass != null ? annotatedClass.getName() : "null");
	return str.toString();
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (annotatedClass == null ? 0 : annotatedClass.hashCode());
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (enable ? 1231 : 1237);
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (standard ? 1231 : 1237);
	result = prime * result + (version == null ? 0 : version.hashCode());
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
	if (!(obj instanceof Plugin<?>)) { return false; }
	final Plugin<?> other = (Plugin<?>) obj;
	if (annotatedClass == null) {
	   if (other.annotatedClass != null) { return false; }
	} else if (!annotatedClass.equals(other.annotatedClass)) { return false; }
	if (description == null) {
	   if (other.description != null) { return false; }
	} else if (!description.equals(other.description)) { return false; }
	if (enable != other.enable) { return false; }
	if (name == null) {
	   if (other.name != null) { return false; }
	} else if (!name.equals(other.name)) { return false; }
	if (standard != other.standard) { return false; }
	if (version == null) {
	   if (other.version != null) { return false; }
	} else if (!version.equals(other.version)) { return false; }
	return true;
   }

   /*
    * (non-Javadoc)
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(final Plugin<T> o1, final Plugin<T> o2) {

	if (o1 == null && o2 == null) {
	   return 0;
	} else if (o1 == null || o1.getClass() == null) {
	   return -1;
	} else if (o2 == null || o2.getClass() == null) {
	   return 1;
	} else {
	   return o1.getClass().getName().compareTo(o1.getClass().getName());
	}
   }

}
