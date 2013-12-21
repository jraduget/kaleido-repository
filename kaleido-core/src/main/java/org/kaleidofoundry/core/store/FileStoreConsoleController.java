/*
 * Copyright 2008-2014 the original author or authors
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
package org.kaleidofoundry.core.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kaleidofoundry.core.lang.annotation.NotNull;
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
 * @author jraduget
 */
@Stateless(mappedName = "ejb/console/filestores")
@Path("/console/filestores/")
public class FileStoreConsoleController {

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
   public static final String BEGINLINE_ARGS = "begin";
   /** Argument to specify index of end line */
   public static final String MAXLINE_COUNT_ARGS = "max";
   /** Argument to specify the Charset encoding */
   public static final String CHARSET_ARGS = "charset";
   /** Argument to specify the highlight of a text in the output */
   public static final String HIGHLIGHT_ARGS = "highlight";
   /** Argument to specify if we add the line count as prefix to the output */
   public static final String COUNTLINE_ARGS = "count";
   /** Argument to specify how to cut the line length */
   public static final String CUTLINE_ARGS = "cut";
   /** Argument to specify if output must be rendered as html */
   public static final String HTML_ARGS = "html";

   /** The default line count result of a tail command. it will be used if {@link #MAXLINE_COUNT_ARGS} is not specified, */
   public static final Long DEFAULT_MAXLINE_COUNT = 10L;

   /** Set of all parameters names */
   public static final Set<String> ARGS = Collections.synchronizedSet(new TreeSet<String>());

   /** All registered resources */
   public static final Set<String> REGISTERED_RESOURCES = Collections.synchronizedSet(new HashSet<String>());

   static {
	ARGS.add(BEGINLINE_ARGS);
	ARGS.add(CHARSET_ARGS);
	ARGS.add(MAXLINE_COUNT_ARGS);
	ARGS.add(HIGHLIGHT_ARGS);
	ARGS.add(OPERATION_ARGS);
	ARGS.add(CUTLINE_ARGS);
	ARGS.add(HTML_ARGS);
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
	str.append("<li>").append(CHARSET_ARGS).append("=UTF-8|ISO-8859-1|...</li>");
	str.append("<li>").append(COUNTLINE_ARGS).append("=true|false</li>");
	str.append("<li>").append(CUTLINE_ARGS).append("=120</li>");
	str.append("<li>").append(HTML_ARGS).append("=true|false</li>");
	str.append("</ul>");
	str.append("</p>");

	str.append("<p>");
	str.append("<h2>Registered resources:</h2>");
	str.append("<ul>");
	for (String resource : REGISTERED_RESOURCES) {
	   str.append("<li>").append(resource).append("</li>");
	}
	str.append("</ul>");
	str.append("</p>");
	return str.toString();
   }

   /**
    * add a resource to the console registry
    * 
    * @param resource resource {@link URI}
    * @throws ResourceNotFoundException
    */
   public synchronized void register(final @NotNull URI resource) throws ResourceNotFoundException {
	register(resource.toString());
   }

   /**
    * add a resource to the console registry
    * 
    * @param resource resource {@link URI}
    * @throws ResourceNotFoundException
    */
   @GET
   @Path("register")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public synchronized String register(@QueryParam("resource") final String resource) throws ResourceNotFoundException {

	if (!REGISTERED_RESOURCES.contains(resource)) {
	   boolean found = false;
	   // looking at registered file store
	   for (FileStore storeEntry : FileStoreFactory.getRegistry().values()) {
		if (resource.toLowerCase().contains(storeEntry.getBaseUri().toLowerCase())) {
		   if (storeEntry.isUriManageable(resource)) {
			found = true;
			break;
		   }
		}
	   }

	   if (found) {
		REGISTERED_RESOURCES.add(resource);
	   } else {

		// if no file store found try to create a new one
		int localResourcePos = resource.lastIndexOf("/");

		if (localResourcePos >= 0) {
		   // register a new file store to handle this resource
		   FileStoreFactory.provides(resource.substring(0, localResourcePos));
		   // the input console resource is register in console manager
		   REGISTERED_RESOURCES.add(resource);
		} else {
		   throw new ResourceNotFoundException(resource);
		}
	   }
	}

	return info();
   }

   /**
    * remove a resource to the console registry
    * 
    * @param resource
    * @throws ResourceNotFoundException
    */
   @GET
   @Path("unregister")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public synchronized String unregister(@QueryParam("resource") final String resource) throws ResourceNotFoundException {

	if (!REGISTERED_RESOURCES.contains(resource)) {
	   REGISTERED_RESOURCES.remove(resource);
	   return info();
	} else {
	   throw new ResourceNotFoundException(resource);
	}

   }

   /**
    * head command on a file resource
    * 
    * @param resource the resource which we want to extract the contents
    * @return head of the file
    * @throws ResourceException
    * @throws IOException
    */
   @GET
   @Path("head")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String head(@QueryParam("resource") final String resource) throws ResourceException, IOException {
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
    * @throws ResourceException
    * @throws IOException
    */
   public String head(final String resource, final long maxLineCount) throws ResourceException, IOException {
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
    * @throws ResourceException
    * @throws IOException
    */
   public String head(final String resource, final Map<String, Serializable> parameters) throws ResourceException, IOException {
	final Map<String, Serializable> typedParameters = typedParameters(parameters);
	final Number maxLineCountArg = (Number) typedParameters.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;

	final Reader reader = new InputStreamReader(in(resource, typedParameters).getInputStream());
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
    * @throws ResourceException
    * @throws IOException
    */
   @GET
   @Path("tail")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String tail(@QueryParam("resource") final String resource) throws ResourceException, IOException {
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
    * @throws ResourceException
    */
   public String tail(final String resource, final long maxLineCount) throws ResourceException, IOException {
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
    * @throws ResourceException
    * @see #BEGINLINE_ARGS
    * @see #MAXLINE_COUNT_ARGS
    */
   public String tail(final String resource, final Map<String, Serializable> parameters) throws ResourceException, IOException {
	final Map<String, Serializable> typedParameters = typedParameters(parameters);
	final Number maxLineCountArg = (Number) typedParameters.get(MAXLINE_COUNT_ARGS);
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;
	final Reader reader = new InputStreamReader(in(resource, typedParameters).getInputStream());
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
    * @throws ResourceException
    * @throws IOException
    */
   @GET
   @Path("extract")
   @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
   public String extract(@QueryParam("resource") final String resource, @QueryParam(BEGINLINE_ARGS) final long beginLine,
	   @QueryParam(MAXLINE_COUNT_ARGS) final long maxLineCount) throws ResourceException, IOException {
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
    * @throws ResourceException
    * @throws IOException
    */
   public String extract(final String resource, final Map<String, Serializable> parameters) throws ResourceException, IOException {

	final Map<String, Serializable> typepParameters = typedParameters(parameters);
	final Number beginLineArg = (Number) typepParameters.get(BEGINLINE_ARGS);
	final Number maxLineCountArg = (Number) typepParameters.get(MAXLINE_COUNT_ARGS);

	final long beginLine = beginLineArg != null && beginLineArg.longValue() > 0 ? beginLineArg.longValue() : 1;
	final long maxLine = maxLineCountArg != null ? maxLineCountArg.longValue() : DEFAULT_MAXLINE_COUNT;
	final Charset charset = (Charset) typepParameters.get(CHARSET_ARGS);

	Reader reader = null;
	BufferedReader buffReader = null;

	try {
	   String currentLine;
	   long index = 1;
	   LinkedList<String> queue = new LinkedList<String>();

	   if (charset == null) {
		reader = new InputStreamReader(in(resource, typepParameters).getInputStream());
	   } else {
		reader = new InputStreamReader(in(resource, typepParameters).getInputStream(), charset);
	   }
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
	   final Serializable charset = parameters.get(CHARSET_ARGS);
	   if (charset != null) {
		if (charset instanceof String) {
		   Charset.forName((String) charset); // throws illegal charset name if needed
		   typedParameters.put(CHARSET_ARGS, charset);
		   isOk = true;
		} else {
		   isOk = false;
		   msgErr = "Parameter '" + CHARSET_ARGS + "' must be java.lang.String instance.";
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
	   final Serializable cutNumber = parameters.get(CUTLINE_ARGS);
	   if (cutNumber != null) {
		if (cutNumber instanceof Number) {
		   isOk = true;
		   typedParameters.put(CUTLINE_ARGS, cutNumber);
		} else {
		   if (cutNumber instanceof String) {
			typedParameters.put(CUTLINE_ARGS, Integer.valueOf((String) cutNumber));
		   } else {
			isOk = false;
			msgErr = "Parameter '" + CUTLINE_ARGS + "' must be java.lang.Integer instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	{
	   final Serializable html = parameters.get(HTML_ARGS);
	   if (html != null) {
		if (html instanceof Boolean) {
		   isOk = true;
		   typedParameters.put(HTML_ARGS, html);
		} else {
		   if (html instanceof String) {
			typedParameters.put(HTML_ARGS, Boolean.valueOf((String) html));
			isOk = true;
		   } else {
			isOk = false;
			msgErr = "Parameter '" + HTML_ARGS + "' must be java.lang.Boolean instance.";
		   }
		}

		if (!isOk) { throw new IllegalArgumentException(msgErr); }
	   }
	}

	return typedParameters;
   }

   /**
    * resource input stream handler
    * 
    * @param resource the resource which we want to extract the contents
    * @param typedParameters
    * @return handler on the resource
    * @throws ResourceException
    */
   protected ResourceHandler in(final String resource, final Map<String, Serializable> typedParameters) throws ResourceException {

	// search in console resource registry
	if (REGISTERED_RESOURCES.contains(resource)) {
	   // looking at registered file store
	   for (FileStore storeEntry : FileStoreFactory.getRegistry().values()) {
		if (resource.toLowerCase().contains(storeEntry.getBaseUri().toLowerCase())) { return storeEntry.get(
			resource.substring(storeEntry.getBaseUri().length())); }
	   }
	}

	throw new ResourceNotFoundException(resource);
   }

   /**
    * format output queue
    * 
    * @param queue
    * @param parameters
    * @param fromIndex
    * @return formated output (highlight, line count, encoding, cut parameter)
    */
   @Task(comment = "formated output : highlight, cut parameter")
   protected StringBuilder format(final LinkedList<String> queue, final Map<String, Serializable> parameters, long fromIndex) {
	final StringBuilder buffer = new StringBuilder();
	Boolean countLine = (Boolean) parameters.get(COUNTLINE_ARGS);
	String highlightText = (String) parameters.get(HIGHLIGHT_ARGS);
	int prefixLength = String.valueOf(fromIndex + queue.size()).length() + 1;
	boolean highlight = !StringHelper.isEmpty(highlightText);
	Number cutLine = (Number) parameters.get(CUTLINE_ARGS);
	Boolean html = (Boolean) parameters.get(HTML_ARGS);

	if (countLine == null) {
	   countLine = Boolean.FALSE;
	}

	if (html == null) {
	   html = Boolean.FALSE;
	}

	for (String line : queue) {
	   if (countLine) {
		buffer.append(StringHelper.leftPad(String.valueOf(++fromIndex), prefixLength, '0')).append(" ");
	   }
	   if (highlight && html) {
		line = StringHelper.replaceAll(line, highlightText, "<span style=\"background-color:yellow;\">" + highlightText + "</span>");
	   }
	   buffer.append(cutLine == null ? line : StringHelper.truncate(line, cutLine.intValue()));
	   buffer.append(html ? "<br/>" : "\n");
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
		if (queryParam.getValue().size() == 1) {
		   parameters.put(queryParam.getKey(), queryParam.getValue().get(0));
		} else {
		   parameters.put(queryParam.getKey(), String.valueOf(queryParam.getValue()));
		}
	   }
	}
	return parameters;
   }
}
