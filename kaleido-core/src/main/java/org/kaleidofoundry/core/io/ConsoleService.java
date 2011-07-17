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
package org.kaleidofoundry.core.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kaleidofoundry.core.lang.annotation.Stateless;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.lang.annotation.ThreadSafe;

/**
 * Simulate some unix command (like head, tail, ..) on a text file content (like logging, traces...) <br/>
 * 
 * @author Jerome RADUGET
 */
@Stateless
@ThreadSafe
public class ConsoleService {

   /** Argument to specify index of begin line */
   public static final String BEGINLINE_ARGS = "beginLine";
   /** Argument to specify index of end line */
   public static final String MAXLINE_COUNT_ARGS = "maxLineCount";
   /** Argument to specify the input encoding */
   public static final String ENCODING_ARGS = "encoding";
   /** Argument to specify the output type */
   public static final String HIGHLIGHT_ARGS = "highlight";
   /** Argument to specify the operation type */
   public static final String OPERATION_ARGS = "operation";
   /** Argument to specify how to cut the line length */
   public static final String CUT_ARGS = "cut";

   /** The default line count result of a tail command. it will be used if {@link #MAXLINE_COUNT_ARGS} is not specified, */
   public static final Long DEFAULT_MAXLINE_COUNT = 10L;

   /** Set of all arguments names */
   public static final Set<String> ARGS = new TreeSet<String>();

   static {
	ARGS.add(BEGINLINE_ARGS);
	ARGS.add(ENCODING_ARGS);
	ARGS.add(MAXLINE_COUNT_ARGS);
	ARGS.add(HIGHLIGHT_ARGS);
	ARGS.add(OPERATION_ARGS);
	ARGS.add(CUT_ARGS);
   }

   /**
    * Operation
    */
   public static enum Operation {
	Head,
	Tail,
	Extract
   }

   /**
    * head command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @return head of the file
    * @throws IOException
    */
   public String head(final String resourcePath) throws IOException {
	return head(resourcePath, DEFAULT_MAXLINE_COUNT);

   }

   /**
    * head command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @param maxLineCount
    * @return head of the file
    * @throws IOException
    */
   public String head(final String resourcePath, final long maxLineCount) throws IOException {
	final Map<String, Object> args = new HashMap<String, Object>();

	args.put(OPERATION_ARGS, Operation.Tail);

	if (maxLineCount >= 0) {
	   args.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}

	return head(resourcePath, args);
   }

   /**
    * head command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @param args
    * @return head of the file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public String head(final String resourcePath, final Map<String, Object> args) throws FileNotFoundException, IOException {
	final Map<String, Object> typepArgs = typeArgs(args);
	final Number maxLineCountArg = (Number) typepArgs.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resourcePath, typepArgs));
	final LinkedList<String> queue = new LinkedList<String>();
	BufferedReader buffReader = null;

	try {
	   String currentLine;
	   buffReader = new BufferedReader(reader);
	   while ((currentLine = buffReader.readLine()) != null && queue.size() < maxLine) {
		queue.add(currentLine);
	   }

	   return format(queue, typepArgs).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * tail command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @return tail of the file
    * @throws IOException
    */
   public String tail(final String resourcePath) throws IOException {
	return tail(resourcePath, DEFAULT_MAXLINE_COUNT);
   }

   /**
    * tail command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @param maxLineCount number of line you wish to keep in result buffer
    * @return list of n last line of the buffer
    * @throws IOException
    */
   public String tail(final String resourcePath, final long maxLineCount) throws IOException {
	final Map<String, Object> args = new HashMap<String, Object>();

	args.put(OPERATION_ARGS, Operation.Tail);

	if (maxLineCount >= 0) {
	   args.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}

	return tail(resourcePath, args);
   }

   /**
    * tail command on a file resource
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @param args
    * @return line of the buffer filter by args arguments
    * @throws IOException
    * @see #BEGINLINE_ARGS
    * @see #MAXLINE_COUNT_ARGS
    * @throws FileNotFoundException
    */
   public String tail(final String resourcePath, final Map<String, Object> args) throws FileNotFoundException, IOException {

	final Map<String, Object> typepArgs = typeArgs(args);
	final Number maxLineCountArg = (Number) typepArgs.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resourcePath, typepArgs));
	BufferedReader buffReader = null;

	try {
	   String currentLine;
	   LinkedList<String> queue = new LinkedList<String>();
	   buffReader = new BufferedReader(reader);
	   while ((currentLine = buffReader.readLine()) != null) {
		if (queue.size() >= maxLine) {
		   queue.remove();
		}
		queue.offerLast(currentLine);
	   }

	   return format(queue, typepArgs).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * @param resourcePath the resource which we want to extract the contents
    * @param beginLine index of beginning line of file you wish
    * @param maxLineCount index of the last line of file you wish
    * @return list of n last line of the buffer
    * @throws IOException
    */
   public String extract(final String resourcePath, final long beginLine, final long maxLineCount) throws IOException {
	final Map<String, Object> args = new HashMap<String, Object>();

	args.put(OPERATION_ARGS, Operation.Extract);

	if (beginLine >= 0) {
	   args.put(BEGINLINE_ARGS, Long.valueOf(beginLine));
	}
	if (maxLineCount >= 0) {
	   args.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}

	return extract(resourcePath, args);
   }

   /**
    * @param resourcePath the resource which we want to extract the contents
    * @param args
    * @return extract of the file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public String extract(final String resourcePath, final Map<String, Object> args) throws FileNotFoundException, IOException {

	final Map<String, Object> typepArgs = typeArgs(args);
	final Number beginLineArg = (Number) typepArgs.get(BEGINLINE_ARGS);
	final Number maxLineCountArg = (Number) typepArgs.get(MAXLINE_COUNT_ARGS);

	final long beginLine = beginLineArg != null && beginLineArg.longValue() > 0 ? beginLineArg.longValue() : 1;
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resourcePath, typepArgs));
	BufferedReader buffReader = null;

	try {
	   String currentLine;
	   long currentLineCount = 1;
	   LinkedList<String> queue = new LinkedList<String>();
	   buffReader = new BufferedReader(reader);

	   while ((currentLine = buffReader.readLine()) != null && currentLineCount < beginLine + maxLine) {
		if (currentLineCount >= beginLine) {
		   queue.add(currentLine);
		}
		currentLineCount++;
	   }

	   return format(queue, typepArgs).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * @param args the resource which we want to extract the contents
    * @return Arguments correctly typed
    * @throws IllegalArgumentException
    */
   protected Map<String, Object> typeArgs(final Map<String, Object> args) throws IllegalArgumentException {
	boolean isOk = false;
	String msgErr = null;
	final Map<String, Object> argsResult = new HashMap<String, Object>();

	// Begin line (optional)
	{
	   final Object beginLine = args.get(BEGINLINE_ARGS);
	   if (beginLine == null || beginLine instanceof Number) {
		isOk = true;
		argsResult.put(BEGINLINE_ARGS, beginLine);
	   } else if (beginLine instanceof String) {
		try {
		   argsResult.put(BEGINLINE_ARGS, Long.valueOf((String) beginLine));
		   isOk = true;
		} catch (final NumberFormatException nbe) {
		   msgErr = "Argument '" + BEGINLINE_ARGS + "' must be java.lang.Number instance.";
		}
	   }

	   if (!isOk) { throw new IllegalArgumentException(msgErr); }
	}

	// Max n line
	{
	   final Object lastLineCount = args.get(MAXLINE_COUNT_ARGS);
	   if (lastLineCount instanceof Number) {
		isOk = true;
		argsResult.put(MAXLINE_COUNT_ARGS, lastLineCount);
	   } else if (lastLineCount instanceof String) {
		try {
		   argsResult.put(MAXLINE_COUNT_ARGS, Long.valueOf((String) lastLineCount));
		   isOk = true;
		} catch (final NumberFormatException nbe) {
		   msgErr = "Argument '" + MAXLINE_COUNT_ARGS + "' must be java.lang.Number instance.";
		}
	   }

	   if (!isOk) { throw new IllegalArgumentException(msgErr); }
	}

	return argsResult;
   }

   /**
    * resource input stream
    * 
    * @param resourcePath the resource which we want to extract the contents
    * @return input stream of the resource
    * @throws FileNotFoundException
    */
   @Task(comment = "use file store plugin instead of classpath input stream")
   protected InputStream in(final String resourcePath, final Map<String, Object> typepArg) throws FileNotFoundException {
	final InputStream classpathFileToMonitorIn = ConsoleService.class.getClassLoader().getResourceAsStream(resourcePath);
	final InputStream fileToMonitorIn = classpathFileToMonitorIn != null ? classpathFileToMonitorIn : new FileInputStream(resourcePath);
	return fileToMonitorIn;

   }

   /**
    * format output queue
    * 
    * @param queue
    * @param typepArg
    * @return formated output (highlight, line count, encoding, cut parameter)
    */
   @Task(comment = "formated output : highlight, line count, encoding, cut parameter")
   protected StringBuilder format(final LinkedList<String> queue, final Map<String, Object> typepArg) {
	final StringBuilder buffer = new StringBuilder();
	for (String line : queue) {
	   buffer.append(line).append("\n");
	}
	return buffer;
   }
}
