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
package org.kaleidofoundry.core.plugin;

import static org.kaleidofoundry.core.plugin.PluginConstants.PACKAGE_STANDARD;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import org.kaleidofoundry.core.lang.annotation.Immutable;

/**
 * Plugin bean class representation <br/>
 * 
 * @author Jerome RADUGET
 * @param <T> Interface plugin type
 */
@Immutable
public class Plugin<T> implements Serializable, Comparator<Plugin<T>> {

   private static final long serialVersionUID = -5591060344949290055L;

   // PRIVATE VARIABLES INSTANCES *************************************************************************************
   private final String name;
   private final boolean standard;
   private final Class<? extends T> annotatedClass;
   private final String description;
   private final String version;
   private final boolean singleton;
   private final boolean enable;

   // CONSTRUCTOR *****************************************************************************************************

   /**
    * Field constructor
    * 
    * @param name
    * @param standard
    * @param annotatedClass
    * @param description
    * @param version
    * @param singleton
    * @param enable
    */
   public Plugin(final String name, final boolean standard, final Class<? extends T> annotatedClass, final String description, final String version,
	   final boolean singleton, final boolean enable) {
	this.name = name;
	this.standard = standard;
	this.annotatedClass = annotatedClass;
	this.description = description;
	this.version = version;
	this.singleton = singleton;
	this.enable = enable;
   }

   /**
    * @param declarePlugin
    * @param annotatedClass
    * @return
    */
   static <T> Plugin<T> create(final Declare declarePlugin, final Class<? extends T> annotatedClass) {
	final Plugin<T> plugin = new Plugin<T>(declarePlugin.value(), annotatedClass.getPackage().getName().contains(PACKAGE_STANDARD), annotatedClass,
		declarePlugin.description(), declarePlugin.version(), declarePlugin.singleton(), declarePlugin.enable());

	return plugin;
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

   /**
    * @return does plugin implementation have to be provided as a unique instance
    */
   public boolean isSingleton() {
	return singleton;
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
   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(final Object obj) {
	if (this == obj) { return true; }
	if (obj == null) { return false; }
	if (!(obj instanceof Plugin)) { return false; }
	Plugin other = (Plugin) obj;
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
