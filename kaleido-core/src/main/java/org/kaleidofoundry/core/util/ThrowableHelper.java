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
