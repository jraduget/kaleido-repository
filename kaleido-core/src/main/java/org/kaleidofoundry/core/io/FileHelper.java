/*
 * $License$
 */
package org.kaleidofoundry.core.io;

import java.io.File;

import org.kaleidofoundry.core.lang.annotation.Tested;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Utilities for path / file handle
 * 
 * @author Jerome RADUGET
 */
public abstract class FileHelper {

   /** MSWindows */
   public final static String WINDOWS_SEP = "\\";
   /** Unix separator */
   public final static String UNIX_SEPARATOR = "/";
   /** Windows separator */
   public final static String WEBAPP_SEPARATOR = "/";

   /**
    * Build a valid & normalized path name for current OS (File.separator will be used as directory separator)
    * It supplements if necessary, the name of the path entry with "/" or "\" (mixed)
    * respecting the separation of the OS. It also will add to the end File.separator (if needed)
    * 
    * @param path
    * @return Normalizes the path given <br/>
    *         Sample : buildPath("/foo\\toto/tutu\\gaga") => "/foo/toto/tutu/gaga/" on a UNIX system
    */
   @Tested
   public static String buildPath(final String path) {
	return buildCustomPath(path, null);
   }

   /**
    * Build a valid & normalized path name for UNIX
    * It replaces the "\" with "/", and add at the end "/" if needed
    * 
    * @param path
    * @return Normalizes the path given <br/>
    *         Sample : buildUnixAppPath("/foo\\toto/tutu\\gaga") => "/foo/toto/tutu/gaga/" on a UNIX system
    */
   @Tested
   public static String buildUnixAppPath(final String path) {
	return buildCustomPath(path, UNIX_SEPARATOR);
   }

   /**
    * Build a valid & normalized path name for WINDOWS
    * It replaces the "/" with "\", and add at the end "\" if needed
    * 
    * @param path
    * @return Normalizes the path given <br/>
    *         Sample : buildWindowsAppPath("/foo\\toto/tutu\\gaga") => "\foo\toto\tutu\gaga\" on a WINDOWS system
    */
   @Tested
   public static String buildWindowsAppPath(final String path) {
	return buildCustomPath(path, WINDOWS_SEP);
   }

   /**
    * Build a valid & normalized name for java web application (webapp)
    * It replaces the "\" with "/", and add at the end "/" if needed
    * 
    * @param path
    * @return Normalizes the path given <br/>
    *         Sample : buildWebAppPath("/foo\\toto/tutu\\gaga") => "/foo/toto/tutu/gaga/" on a UNIX system
    */
   @Tested
   public static String buildWebAppPath(final String path) {
	return buildCustomPath(path, WEBAPP_SEPARATOR);
   }

   /**
    * Build a valid & normalized (pathSepToUse will be used as directory separator)
    * It replaces the "\" or "/" by 'pathSepToUse', and it will add at the end 'pathSepToUse'
    * 
    * @param path
    * @param pathSepToUse directory separator to use, if null {@link File}.separator will be used
    * @return Normalizes the path given <br/>
    *         Sample : buildCustomPath("/foo/toto/titi", "$") => "$foo$kaleidofoundry$core$io$" on a UNIX system
    */
   @Tested
   public static String buildCustomPath(final String path, String pathSepToUse) {

	if (path != null) {

	   final StringBuilder str = new StringBuilder("");

	   if (pathSepToUse == null) {
		pathSepToUse = File.separator;
	   }

	   char c = ' ';
	   for (int i = 0; i < path.length(); i++) {
		c = path.charAt(i);

		if (c == '/' || c == '\\') {
		   str.append(pathSepToUse);
		} else {
		   str.append(c);
		}
	   }

	   if (c != '/' && c != '\\' && c != ' ') {
		str.append(pathSepToUse);
	   }

	   return str.toString();
	}

	return null;
   }

   /**
    * @param obj Name of the object, you want the path of his class
    * @return relative path (path package) to the class of the object obj <br/>
    *         Sample : if obj instance of "org.kaleidofoundry.core.io.FileHelper" => "/org/kaleidofoundry/core/io" on a
    *         UNIX system
    */
   @Tested
   public static String getRelativeClassPath(final Object obj) {
	if (obj != null) {
	   return getRelativeClassPath(obj.getClass());
	} else {
	   return null;
	}
   }

   /**
    * @param c class you want the path of his class
    * @return relative path (path package) to the class as argument <br/>
    *         Sample : "org.kaleidofoundry.core.io.FileHelper" => "/org/kaleidofoundry/core/io" on a UNIX system
    */
   @Tested
   public static String getRelativeClassPath(final Class<?> c) {
	if (c != null) {
	   return StringHelper.replaceAll(c.getPackage().getName(), ".", File.separator) + File.separator;
	} else {
	   return null;
	}

   }

   /**
    * @param path
    * @return parent directory as input, without path separator at end <br/>
    *         Sample : "/foo/toto/tutu" => "/foo/toto"
    */
   @Tested
   public static String buildParentPath(String path) {

	if (path == null) { return null; }

	int pos = -1;

	// On supprime � la fin, le separateur de repertoire eventuel
	path = path.substring(0, path.length() - 1);

	if (pos < 0) {
	   pos = path.lastIndexOf(UNIX_SEPARATOR);
	}
	if (pos < 0) {
	   pos = path.lastIndexOf(WINDOWS_SEP);
	}
	if (pos < 0) {
	   pos = path.lastIndexOf(WEBAPP_SEPARATOR);
	}

	return path.substring(0, pos);
   }

   /**
    * @param filename
    * @return extension of the filename argument <br/>
    *         Sample : "/foo/filename.txt" => "txt"
    */
   @Tested
   public static String getFileNameExtension(final String filename) {

	if (filename != null) {

	   String str = "";

	   char c = ' ';
	   boolean findext = false;
	   for (int i = filename.length() - 1; i >= 0; i--) {
		c = filename.charAt(i);

		if (c == '.') {
		   findext = true;
		   break;
		} else {
		   str = c + str;
		}
	   }

	   if (findext) {
		return str.toString();
	   } else {
		return "";
	   }
	}

	return null;
   }

   /**
    * @param filename name directory entry
    * @return extension of the filename argument without its extension <br/>
    *         Sample : "/foo/filename.txt" => "/foo/filename"
    */
   @Tested
   public static String getFileNameWithoutExt(final String filename) {

	if (filename != null) {

	   char c = ' ';
	   int i = 0;
	   boolean findext = false;
	   for (i = filename.length() - 1; i >= 0; i--) {
		c = filename.charAt(i);
		if (c == '.') {
		   findext = true;
		   break;
		}
	   }

	   if (!findext) { return filename; }

	   final StringBuilder str = new StringBuilder();
	   for (int j = 0; j < i; j++) {
		c = filename.charAt(j);
		str.append(c);
	   }

	   return str.toString();
	}

	return null;
   }

   /**
    * @param fullPath name directory entry
    * @return extension of the filename argument <br/>
    *         Sample : "/foo/filename.txt" => "filename.txt"
    */
   @Tested
   public static String getFileName(final String fullPath) {

	if (fullPath != null) {

	   char c = ' ';
	   int i = 0;
	   boolean findext = false;
	   for (i = fullPath.length() - 1; i >= 0; i--) {
		c = fullPath.charAt(i);
		if (c == UNIX_SEPARATOR.charAt(0)) {
		   findext = true;
		   break;
		}
		if (c == WINDOWS_SEP.charAt(0)) {
		   findext = true;
		   break;
		}
		if (c == WEBAPP_SEPARATOR.charAt(0)) {
		   findext = true;
		   break;
		}
	   }

	   if (!findext) { return fullPath; }

	   final StringBuilder str = new StringBuilder();
	   for (int j = i + 1; j < fullPath.length(); j++) {
		c = fullPath.charAt(j);
		str.append(c);
	   }

	   return str.toString();
	}

	return null;
   }

}
