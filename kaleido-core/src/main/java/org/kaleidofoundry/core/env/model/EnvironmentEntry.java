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
package org.kaleidofoundry.core.env.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jraduget
 */
@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentEntry {

   private String name;
   private String value;

   public EnvironmentEntry(){	
   }
   
   public EnvironmentEntry(String name, String value) {
	super();
	this.name = name;
	this.value = value;
   }

   public String getName() {
	return name;
   }

   public void setName(String name) {
	this.name = name;
   }

   public String getValue() {
	return value;
   }

   public void setValue(String values) {
	this.value = values;
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
	return result;
   }

   @Override
   public boolean equals(Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	EnvironmentEntry other = (EnvironmentEntry) obj;
	if (name == null) {
	   if (other.name != null) return false;
	} else if (!name.equals(other.name)) return false;
	if (value == null) {
	   if (other.value != null) return false;
	} else if (!value.equals(other.value)) return false;
	return true;
   }

}
