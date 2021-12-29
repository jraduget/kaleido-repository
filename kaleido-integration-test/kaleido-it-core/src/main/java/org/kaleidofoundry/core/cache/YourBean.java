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
import java.util.Calendar;

/**
 * @author jraduget
 */
public class YourBean implements Serializable {

   private static final long serialVersionUID = 7352309828392713308L;

   private String name;
   private Calendar currentDate;
   private boolean enabled;
   private int flag;

   public YourBean() {
   }

   public YourBean(final String name, final Calendar currentDate, final boolean enabled, final int flag) {
	super();
	this.name = name;
	this.currentDate = currentDate;
	this.enabled = enabled;
	this.flag = flag;
   }

   public String getName() {
	return name;
   }

   public void setName(final String name) {
	this.name = name;
   }

   public Calendar getCurrentDate() {
	return currentDate;
   }

   public void setCurrentDate(final Calendar currentDate) {
	this.currentDate = currentDate;
   }

   public boolean isEnabled() {
	return enabled;
   }

   public void setEnabled(final boolean enabled) {
	this.enabled = enabled;
   }

   public int getFlag() {
	return flag;
   }

   public void setFlag(final int flag) {
	this.flag = flag;
   }

   @Override
   public String toString() {
	return "YourBean [name=" + name + ", enabled=" + enabled + ", flag=" + flag + ", currentDate=" + currentDate + "]";
   }

}
