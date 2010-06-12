/*
 * $License$
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
