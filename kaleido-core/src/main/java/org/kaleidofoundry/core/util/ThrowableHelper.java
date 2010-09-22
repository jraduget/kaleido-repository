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

import javax.servlet.ServletException;

/**
 * Exception Helper
 * 
 * @author Jerome RADUGET
 */
public abstract class ThrowableHelper {

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

	// exception statck trace
	ex.printStackTrace(pw);

	// exception cause
	if (ex instanceof ServletException) {
	   final Throwable cause = ((ServletException) ex).getRootCause();

	   if (null != cause) {
		pw.println("Root Cause:");
		fillStackTrace(cause, pw);
	   }
	} else {
	   final Throwable cause = ex.getCause();
	   if (null != cause) {
		pw.println("Cause:");
		fillStackTrace(cause, pw);
	   }
	}

   }
}
