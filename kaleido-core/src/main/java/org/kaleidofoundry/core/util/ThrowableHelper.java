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
package org.kaleidofoundry.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * Some helper methods for exception
 * 
 * @author Jerome RADUGET
 */
public abstract class ThrowableHelper {

   // Get javax.servlet.ServletException dynamically if no web container in classpath (no need to have servlet.jar in classpath)
   final static Class<?> ServletExceptionClass;

   static {
	Class<?> servletExceptionClass = null;
	try {
	   servletExceptionClass = Class.forName("javax.servlet.ServletException");
	} catch (final Throwable th) {
	}
	ServletExceptionClass = servletExceptionClass;
   }

   /**
    * @param th
    * @return the string representation of an exception stack trace
    */
   public static String getStackTrace(final Throwable th) {

	final StringWriter writer = new StringWriter();
	final PrintWriter pw = new PrintWriter(writer);
	fillStackTrace(th, pw);

	return writer.toString();
   }

   /*
    * @param ex
    * @param pw
    */
   private static void fillStackTrace(final Throwable ex, final PrintWriter pw) {

	if (ex == null) {
	   pw.print("");
	   return;
	}

	// exception stack trace
	ex.printStackTrace(pw);

	// servlet exception root cause
	if (ServletExceptionClass != null && ex.getClass().isAssignableFrom(ServletExceptionClass)) {
	   try {
		final Method getRootCauseMethod = ex.getClass().getMethod("getRootCause");
		final Throwable cause = (Throwable) getRootCauseMethod.invoke(ex, new Object[] {});
		if (null != cause) {
		   pw.println("Root Cause:");
		   fillStackTrace(cause, pw);
		   return;
		}
	   } catch (final Throwable th) {
	   }
	}

	// otherwise
	final Throwable cause = ex.getCause();
	if (null != cause) {
	   pw.println("Cause:");
	   fillStackTrace(cause, pw);
	}

   }
}
