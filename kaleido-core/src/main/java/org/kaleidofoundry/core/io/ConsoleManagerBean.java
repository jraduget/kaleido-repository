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
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.util.StringHelper;

/**
 * Simulate some Unix command (like head, tail, ..) on a text file content (like logging, traces...) <br/>
 * <br/>
 * It can be used as :
 * <ul>
 * <li>a classic class, that you instantiate</li>
 * <li>an EJB service, then you benefit from the JavaEE resource injection</li>
 * <li>as a rest web service, then you benefit from a portable rest web service</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
@Stateless(mappedName = "ejb/console/manager")
@Path("/consoles/")
public class ConsoleManagerBean {

   /**
    * Enumeration of console operation
    */
   public static enum Operation {
	Head,
	Tail,
	Extract
   }

   /** Argument to specify the operation type */
   public static final String OPERATION_ARGS = "operation";
   /** Argument to specify index of begin line */
   public static final String BEGINLINE_ARGS = "beginLine";
   /** Argument to specify index of end line */
   public static final String MAXLINE_COUNT_ARGS = "maxLineCount";
   /** Argument to specify the input encoding */
   public static final String ENCODING_ARGS = "encoding";
   /** Argument to specify the highlight of a text in the output */
   public static final String HIGHLIGHT_ARGS = "highLight";
   /** Argument to specify if we add the line count as prefix to the output */
   public static final String COUNTLINE_ARGS = "countLine";
   /** Argument to specify how to cut the line length */
   public static final String CUT_ARGS = "cut";

   /** The default line count result of a tail command. it will be used if {@link #MAXLINE_COUNT_ARGS} is not specified, */
   public static final Long DEFAULT_MAXLINE_COUNT = 10L;

   /** Set of all parameters names */
   public static final Set<String> ARGS = Collections.synchronizedSet(new TreeSet<String>());

   /** Map of all registered resource uri by name */
   public static final Map<String, String> RESOURCES = Collections.synchronizedMap(new HashMap<String, String>());

   static {
	ARGS.add(BEGINLINE_ARGS);
	ARGS.add(ENCODING_ARGS);
	ARGS.add(MAXLINE_COUNT_ARGS);
	ARGS.add(HIGHLIGHT_ARGS);
	ARGS.add(OPERATION_ARGS);
	ARGS.add(CUT_ARGS);
   }

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   /**
    * @return console informations
    */
   @GET
   @Path("info")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String info() {
	StringBuilder str = new StringBuilder();
	str.append("<p>");
	str.append("<h2>Query parameters:</h2>");
	str.append("<ul>");
	str.append("<li>").append(HIGHLIGHT_ARGS).append("=...text to highlight...</li>");
	str.append("<li>").append(ENCODING_ARGS).append("=UTF-8|ISO-8859-1|...</li>");
	str.append("<li>").append(COUNTLINE_ARGS).append("=true|false</li>");
	str.append("<li>").append(CUT_ARGS).append("=120</li>");
	str.append("</ul>");
	str.append("</p>");

	str.append("<p>");
	str.append("<h2>Registered resources:</h2>");
	str.append("<ul>");
	for (Entry<String, String> entry : RESOURCES.entrySet()) {
	   str.append("<li>").append(entry.getKey()).append(" : ").append(entry.getValue()).append("</li>");
	}
	str.append("</ul>");
	str.append("</p>");
	return str.toString();
   }

   /**
    * add the resource to the console registry
    * 
    * @param name
    * @param uri
    */
   @PUT
   @Path("register")
   public void putResource(@QueryParam("name") final String name, @QueryParam("uri") final String uri) {
	RESOURCES.put(name, uri);
   }

   /**
    * head command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @return head of the file
    * @throws IOException
    */
   @GET
   @Path("{resource}/head")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String head(@PathParam("resource") final String resource) throws IOException {
	Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	parameters.put(MAXLINE_COUNT_ARGS, DEFAULT_MAXLINE_COUNT);
	return head(resource, addUriParameters(parameters));
   }

   /**
    * head command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @param maxLineCount
    * @return head of the file
    * @throws IOException
    */
   public String head(final String resource, final long maxLineCount) throws IOException {
	final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	parameters.put(OPERATION_ARGS, Operation.Tail);
	if (maxLineCount >= 0) {
	   parameters.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}
	return head(resource, addUriParameters(parameters));
   }

   /**
    * head command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @param parameters
    * @return head of the file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public String head(final String resource, final Map<String, Serializable> parameters) throws FileNotFoundException, IOException {
	final Map<String, Serializable> typedParameters = typedParameters(parameters);
	final Number maxLineCountArg = (Number) typedParameters.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resource, typedParameters));
	final LinkedList<String> queue = new LinkedList<String>();
	BufferedReader buffReader = null;

	try {
	   long index = 0;
	   String currentLine;
	   buffReader = new BufferedReader(reader);
	   while ((currentLine = buffReader.readLine()) != null && queue.size() < maxLine) {
		queue.add(currentLine);
		index++;
	   }

	   return format(queue, typedParameters, index - queue.size()).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * tail command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @return tail of the file
    * @throws IOException
    */
   @GET
   @Path("{resource}/tail")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String tail(@PathParam("resource") final String resource) throws IOException {
	Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	parameters.put(MAXLINE_COUNT_ARGS, DEFAULT_MAXLINE_COUNT);
	return tail(resource, addUriParameters(parameters));
   }

   /**
    * tail command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @param maxLineCount number of line you wish to keep in result buffer
    * @return list of n last line of the buffer
    * @throws IOException
    */
   public String tail(final String resource, final long maxLineCount) throws IOException {
	final Map<String, Serializable> args = new HashMap<String, Serializable>();
	args.put(OPERATION_ARGS, Operation.Tail);
	if (maxLineCount >= 0) {
	   args.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}
	return tail(resource, addUriParameters(args));
   }

   /**
    * tail command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @param parameters
    * @return line of the buffer filter by args arguments
    * @throws IOException
    * @see #BEGINLINE_ARGS
    * @see #MAXLINE_COUNT_ARGS
    * @throws FileNotFoundException
    */
   public String tail(final String resource, final Map<String, Serializable> parameters) throws FileNotFoundException, IOException {
	final Map<String, Serializable> typedParameters = typedParameters(parameters);
	final Number maxLineCountArg = (Number) typedParameters.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;
	final Reader reader = new InputStreamReader(in(resource, typedParameters));
	BufferedReader buffReader = null;

	try {
	   int index = 0;
	   String currentLine;
	   LinkedList<String> queue = new LinkedList<String>();
	   buffReader = new BufferedReader(reader);
	   while ((currentLine = buffReader.readLine()) != null) {
		if (queue.size() >= maxLine) {
		   queue.remove();
		}
		queue.offerLast(currentLine);
		index++;
	   }

	   return format(queue, typedParameters, index - queue.size()).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * @param resource the resource which we want to extract the contents
    * @param beginLine index of beginning line of file you wish
    * @param maxLineCount index of the last line of file you wish
    * @return list of n last line of the buffer
    * @throws IOException
    */
   @GET
   @Path("{resource}/extract")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String extract(@PathParam("resource") final String resource, @QueryParam(BEGINLINE_ARGS) final long beginLine,
	   @QueryParam(MAXLINE_COUNT_ARGS) final long maxLineCount) throws IOException {
	final Map<String, Serializable> parameters = new HashMap<String, Serializable>();

	parameters.put(OPERATION_ARGS, Operation.Extract);

	if (beginLine >= 0) {
	   parameters.put(BEGINLINE_ARGS, Long.valueOf(beginLine));
	}
	if (maxLineCount >= 0) {
	   parameters.put(MAXLINE_COUNT_ARGS, Long.valueOf(maxLineCount));
	}

	return extract(resource, addUriParameters(parameters));
   }

   /**
    * @param resource the resource which we want to extract the contents
    * @param parameters
    * @return extract of the file
    * @throws FileNotFoundException
    * @throws IOException
    */
   public String extract(final String resource, final Map<String, Serializable> parameters) throws FileNotFoundException, IOException {

	final Map<String, Serializable> typepParameters = typedParameters(parameters);
	final Number beginLineArg = (Number) typepParameters.get(BEGINLINE_ARGS);
	final Number maxLineCountArg = (Number) typepParameters.get(MAXLINE_COUNT_ARGS);

	final long beginLine = beginLineArg != null && beginLineArg.longValue() > 0 ? beginLineArg.longValue() : 1;
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resource, typepParameters));
	BufferedReader buffReader = null;

	try {
	   String currentLine;
	   long index = 1;
	   LinkedList<String> queue = new LinkedList<String>();
	   buffReader = new BufferedReader(reader);

	   while ((currentLine = buffReader.readLine()) != null && index < beginLine + maxLine) {
		if (index >= beginLine) {
		   queue.add(currentLine);
		}
		index++;
	   }

	   return format(queue, typepParameters, index - 1 - queue.size()).toString();
	} finally {
	   if (buffReader != null) {
		buffReader.close();
	   }

	}
   }

   /**
    * Type the parameters input
    * 
    * @param parameters the resource which we want to extract the contents
    * @return Arguments correctly typed
    * @throws IllegalArgumentException
    */
   protected Map<String, Serializable> typedParameters(final Map<String, Serializable> parameters) throws IllegalArgumentException {
	boolean isOk = false;
	String msgErr = null;
	final Map<String, Serializable> typedParameters = new HashMap<String, Serializable>();

	// Begin line (optional)
	{
	   final Serializable beginLine = parameters.get(BEGINLINE_ARGS);
	   if (beginLine != null) {
		if (beginLine instanceof Number) {
		   isOk = true;
		   typedParameters.put(BEGINLINE_ARGS, beginLine);
		} else if (beginLine instanceof String) {
		   try {
			typedParameters.put(BEGINLINE_ARGS, Long.valueOf((String) beginLine));
			isOk = true;
		   } catch (final NumberFormatException nbe) {
			isOk = false;
			msgErr = "Parameter '" + BEGINLINE_ARGS + "' must be java.lang.Number instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	// Max n line
	{
	   final Serializable lastLineCount = parameters.get(MAXLINE_COUNT_ARGS);
	   if (lastLineCount != null) {
		if (lastLineCount instanceof Number) {
		   isOk = true;
		   typedParameters.put(MAXLINE_COUNT_ARGS, lastLineCount);
		} else if (lastLineCount instanceof String) {
		   try {
			typedParameters.put(MAXLINE_COUNT_ARGS, Long.valueOf((String) lastLineCount));
			isOk = true;
		   } catch (final NumberFormatException nbe) {
			isOk = false;
			msgErr = "Parameter '" + MAXLINE_COUNT_ARGS + "' must be java.lang.Number instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	// Highlight text
	{
	   final Serializable highlight = parameters.get(HIGHLIGHT_ARGS);
	   if (highlight != null) {
		if (highlight instanceof String) {
		   typedParameters.put(HIGHLIGHT_ARGS, highlight);
		   isOk = true;
		} else {
		   isOk = false;
		   msgErr = "Parameter '" + HIGHLIGHT_ARGS + "' must be java.lang.String instance.";
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	// Encoding
	{
	   final Serializable encoding = parameters.get(ENCODING_ARGS);
	   if (encoding != null) {
		if (encoding instanceof String) {
		   Charset.forName((String) encoding); // throw sillegal charset name if needed
		   typedParameters.put(ENCODING_ARGS, encoding);
		   isOk = true;
		} else {
		   isOk = false;
		   msgErr = "Parameter '" + ENCODING_ARGS + "' must be java.lang.String instance.";
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	// Add line count
	{
	   final Serializable countLines = parameters.get(COUNTLINE_ARGS);
	   if (countLines != null) {
		if (countLines instanceof Boolean) {
		   isOk = true;
		   typedParameters.put(COUNTLINE_ARGS, countLines);
		} else {
		   if (countLines instanceof String) {
			typedParameters.put(COUNTLINE_ARGS, Boolean.valueOf((String) countLines));
			isOk = true;
		   } else {
			isOk = false;
			msgErr = "Parameter '" + COUNTLINE_ARGS + "' must be java.lang.Boolean instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	// Cut parameter
	{
	   final Serializable cutNumber = parameters.get(CUT_ARGS);
	   if (cutNumber != null) {
		if (cutNumber instanceof Number) {
		   isOk = true;
		   typedParameters.put(CUT_ARGS, cutNumber);
		} else {
		   if (cutNumber instanceof String) {
			typedParameters.put(CUT_ARGS, Integer.valueOf((String) cutNumber));
		   } else {
			isOk = false;
			msgErr = "Parameter '" + CUT_ARGS + "' must be java.lang.Integer instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	return typedParameters;
   }

   /**
    * resource input stream
    * 
    * @param resource the resource which we want to extract the contents
    * @param typedParameters
    * @return input stream of the resource
    * @throws FileNotFoundException
    */
   @Task(comment = "use file store plugin instead of classpath input stream")
   protected InputStream in(String resource, final Map<String, Serializable> typedParameters) throws FileNotFoundException {

	// search in console resource registry first
	// TODO
	resource = RESOURCES.get(resource);

	// map FileNotFound .. to ResourceNotFound rest exception
	// FileStoreFactory.provides("/").get(resourceRelativePath);

	// search in the classpath if not found
	final InputStream classpathFileToMonitorIn = ConsoleManagerBean.class.getClassLoader().getResourceAsStream(resource);
	final InputStream fileToMonitorIn = classpathFileToMonitorIn != null ? classpathFileToMonitorIn : new FileInputStream(resource);
	return fileToMonitorIn;

   }

   /**
    * format output queue
    * 
    * @param queue
    * @param parameters
    * @param fromIndex
    * @return formated output (highlight, line count, encoding, cut parameter)
    */
   @Task(comment = "formated output : highlight, encoding, cut parameter")
   protected StringBuilder format(final LinkedList<String> queue, final Map<String, Serializable> parameters, final long fromIndex) {
	final StringBuilder buffer = new StringBuilder();
	Boolean countLine = (Boolean) parameters.get(COUNTLINE_ARGS);
	int prefixLength = String.valueOf(fromIndex + queue.size()).length();

	if (countLine == null) {
	   countLine = Boolean.FALSE;
	}

	for (String line : queue) {
	   if (countLine) {
		buffer.append(StringHelper.leftPad(String.valueOf(fromIndex), prefixLength, '0'));
	   }
	   buffer.append(line).append("\n");
	}
	return buffer;
   }

   /**
    * add uri parameter (if needed)
    * 
    * @return enriched the input parameters with url parameters
    */
   protected Map<String, Serializable> addUriParameters(final Map<String, Serializable> parameters) {
	if (uriInfo != null) {
	   for (Entry<String, List<String>> queryParam : uriInfo.getQueryParameters().entrySet()) {
		parameters.put(queryParam.getKey(), String.valueOf(queryParam.getValue()));
	   }
	}
	return parameters;
   }
}
