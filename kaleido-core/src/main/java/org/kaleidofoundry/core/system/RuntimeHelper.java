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
package org.kaleidofoundry.core.system;

/**
 * RuntimeHelper for getting java memory usage, java cpu information...
 * 
 * @author Jerome RADUGET
 */
public class RuntimeHelper {

   /**
    * @return JVM freeMemory in Mo
    */
   public static long getFreeMemory() {
	return (Runtime.getRuntime().freeMemory() / 1024);
   }

   /**
    * @return JVM maxMemory in Mo
    */
   public static long getMaxMemory() {
	return (Runtime.getRuntime().maxMemory() / 1024);
   }

   /**
    * @return JVM totalMemory in Mo
    */
   public static long getTotalMemory() {
	return (Runtime.getRuntime().totalMemory() / 1024);
   }

   /**
    * @return number of processors available to the JVM
    */
   public static long getAvailableProcessors() {
	return Runtime.getRuntime().availableProcessors();
   }

   /**
    * @return JVM freeMemory in Mo
    */
   public static long getMemoryUsage() {
	return getTotalMemory() - getFreeMemory();
   }

   /**
    * @return JVM freeMemory in Percent
    */
   public static double getPercentMemoryUsage() {
	return ((double) getMemoryUsage() / (double) getMaxMemory() * 100);
   }

}
