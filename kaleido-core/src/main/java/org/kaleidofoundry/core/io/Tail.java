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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kaleidofoundry.core.lang.annotation.Tested;

/**
 * Simulate tail command (linux) on a file content. <br/>
 * <br/>
 * It is a statefull class, it keeps a internal buffer. You can use methods: <br/>
 * <ul>
 * <li>List<TailLine> getCurrentBuffer() - to get last requested buffered ( List<TailLine> buffer = tail(...))</li>
 * <li>void clearCurrentBuffer() - clear last computed buffer</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
@Tested
public class Tail {

   /** Argument to specify index of begin line */
   public static final String BEGINLINE_ARGS = "beginLine";
   /** Argument to specify index of end line */
   public static final String LASTLINE_COUNT_ARGS = "lastLineCount";
   /** Set of all arguments names */
   public static final Set<String> ARGS = new TreeSet<String>();

   static {
	ARGS.add(BEGINLINE_ARGS);
	ARGS.add(LASTLINE_COUNT_ARGS);
   }

   /**
    * @author Jerome RADUGET
    */
   public static class TailLine {

	/**
	 * 
	 */
	public TailLine() {
	}

	/**
	 * @param position
	 * @param text
	 */
	public TailLine(final long position, final String text) {
	   this.position = position;
	   this.text = text;
	}

	private String text;
	private long position;

	/**
	 * @return text of the line
	 */
	public String getText() {
	   return text;
	}

	/**
	 * @param text
	 */
	public void setText(final String text) {
	   this.text = text;
	}

	/**
	 * @return from position
	 */
	public long getPosition() {
	   return position;
	}

	/**
	 * @param position
	 */
	public void setPosition(final long position) {
	   this.position = position;
	}

	@Override
	public int hashCode() {
	   final int prime = 31;
	   int result = 1;
	   result = prime * result + (int) (position ^ position >>> 32);
	   result = prime * result + (text == null ? 0 : text.hashCode());
	   return result;
	}

	@Override
	public boolean equals(final Object obj) {
	   if (this == obj) { return true; }
	   if (obj == null) { return false; }
	   if (getClass() != obj.getClass()) { return false; }
	   final TailLine other = (TailLine) obj;
	   if (position != other.position) { return false; }
	   if (text == null) {
		if (other.text != null) { return false; }
	   } else if (!text.equals(other.text)) { return false; }
	   return true;
	}

	@Override
	public String toString() {
	   // Add LPAD "0" on position
	   return "[" + getPosition() + "] " + getText();
	}

   }

   private final InputStream fileToMonitor;
   private long lineCount;
   private List<TailLine> lastBuffer;

   /**
    * Resource loading
    * 
    * @param fileToMonitorPath path
    * @throws FileNotFoundException
    * @throws URISyntaxException
    */
   public Tail(final String fileToMonitorPath) throws FileNotFoundException, URISyntaxException {
	fileToMonitor = new FileInputStream(fileToMonitorPath);
	lastBuffer = new LinkedList<TailLine>();
   }

   /**
    * Classpath resource loading
    * 
    * @param loader
    * @param classPathResource classpath resource path
    * @throws FileNotFoundException
    * @throws FileNotFoundException
    */
   public Tail(final ClassLoader loader, final String classPathResource) throws FileNotFoundException {
	fileToMonitor = loader.getResourceAsStream(classPathResource);
	if (fileToMonitor == null) { throw new FileNotFoundException(classPathResource); }

	lastBuffer = new LinkedList<TailLine>();
   }

   /**
    * @return Last buffer compute by tail command
    */
   @Tested
   public List<TailLine> getCurrentBuffer() {
	return lastBuffer;
   }

   /**
    * Clear current buffer
    */
   @Tested
   public void clearCurrentBuffer() {
	lastBuffer.clear();
   }

   /**
    * @param lastLine number of line you wish to keep in result buffer
    * @return list of n last line of the buffer
    * @throws IOException
    */
   @Tested
   public List<TailLine> tail(final long lastLine) throws IOException {
	final Map<String, Object> args = new HashMap<String, Object>();

	if (lastLine >= 0) {
	   args.put(LASTLINE_COUNT_ARGS, Long.valueOf(lastLine));
	}

	return tail(args);
   }

   /**
    * @param beginLine index of beginning line of file you wish
    * @param lastLine index of the last line of file you wish
    * @return list of n last line of the buffer
    * @throws IOException
    */
   @Tested
   public List<TailLine> tail(final long beginLine, final long lastLine) throws IOException {
	final Map<String, Object> args = new HashMap<String, Object>();

	if (beginLine >= 0) {
	   args.put(BEGINLINE_ARGS, Long.valueOf(beginLine));
	}
	if (lastLine >= 0) {
	   args.put(LASTLINE_COUNT_ARGS, Long.valueOf(lastLine));
	}

	return tail(args);
   }

   /**
    * @param args
    * @return line of the buffer filter by args arguments
    * @throws IOException
    * @see #BEGINLINE_ARGS
    * @see #LASTLINE_COUNT_ARGS
    */
   @Tested
   public List<TailLine> tail(final Map<String, Object> args) throws IOException {
	final Map<String, Object> typepArgs = typeArgs(args);
	final Number beginLine = (Number) typepArgs.get(BEGINLINE_ARGS);
	final Number lastLineArgs = (Number) typepArgs.get(LASTLINE_COUNT_ARGS);

	final Reader reader = new InputStreamReader(fileToMonitor);
	final BufferedReader buffReader = new BufferedReader(reader);

	String currentLine = null;
	final long lastLine = lastLineArgs != null ? lastLineArgs.longValue() : Long.MAX_VALUE;

	List<TailLine> firstBuffer = new LinkedList<TailLine>();
	final List<TailLine> secondBuffer = new LinkedList<TailLine>();

	int bufferNum = 1;

	lineCount = 0;

	// begin tail at line ...
	if (beginLine != null) {

	   long nbToPaste = 1;
	   while (nbToPaste++ < beginLine.longValue()) {
		buffReader.readLine();
		lineCount++;
	   }
	}

	// use two buffer to store result
	while ((currentLine = buffReader.readLine()) != null && (beginLine == null || lineCount <= beginLine.longValue() + lastLine)) {
	   final List<TailLine> buffer = bufferNum == 1 ? firstBuffer : secondBuffer;
	   buffer.add(new TailLine(++lineCount, currentLine));

	   if (lineCount % lastLine == 0) {
		if (bufferNum == 1) {
		   bufferNum = 2;
		   secondBuffer.clear();
		} else {
		   bufferNum = 1;
		   firstBuffer.clear();
		}
	   }
	}

	// one of the two buffer contain all result
	if (firstBuffer.size() == lastLine && secondBuffer.isEmpty() || secondBuffer.size() == lastLine && firstBuffer.isEmpty()) {
	   firstBuffer = bufferNum == 2 ? firstBuffer : secondBuffer;
	   // secondBuffer contain begin of result
	} else if (bufferNum == 1 && firstBuffer.size() < lastLine) {
	   secondBuffer.addAll(firstBuffer);
	   firstBuffer = secondBuffer;
	   // firstBuffer contain begin of result
	} else if (bufferNum == 2 && secondBuffer.size() < lastLine) {
	   firstBuffer.addAll(secondBuffer);
	}

	// keep necessary entry
	final int toDelete = firstBuffer.size() - (int) lastLine;
	for (int index = 1; index <= toDelete; index++) {
	   firstBuffer.remove(0);
	}

	// Memorize last buffer
	lastBuffer.clear();
	lastBuffer = firstBuffer;

	return firstBuffer;
   }

   /**
    * @param args
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

	// Last n line
	{
	   final Object lastLineCount = args.get(LASTLINE_COUNT_ARGS);
	   if (lastLineCount instanceof Number) {
		isOk = true;
		argsResult.put(LASTLINE_COUNT_ARGS, lastLineCount);
	   } else if (lastLineCount instanceof String) {
		try {
		   argsResult.put(LASTLINE_COUNT_ARGS, Long.valueOf((String) lastLineCount));
		   isOk = true;
		} catch (final NumberFormatException nbe) {
		   msgErr = "Argument '" + LASTLINE_COUNT_ARGS + "' must be java.lang.Number instance.";
		}
	   }

	   if (!isOk) { throw new IllegalArgumentException(msgErr); }
	}

	return argsResult;
   }

}
