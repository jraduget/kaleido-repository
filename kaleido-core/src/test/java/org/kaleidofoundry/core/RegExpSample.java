/*  
 * Copyright 2008-2016 the original author or authors 
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
package org.kaleidofoundry.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple sample for using regexp
 * 
 * @author jraduget
 */
public class RegExpSample  {

   static final Logger LOGGER = LoggerFactory.getLogger(RegExpSample.class);

   /**
    * simple sample enum of uri type and regExp
    */
   public enum ResourceUriEnum {

	/**
	 * <ul>
	 * <li>classpath:/filename</li>
	 * <li>classpath:/path/filename</li>
	 * <li>classpath:/directory1/directory2/filename</li>
	 * </ul>
	 */
	classpath("/((PATH)/(FILENAME))|/(FILENAME)"),

	/**
	 * <ul>
	 * <li>file:/c:/filename</li>
	 * <li>file:/c:/path/filename</li>
	 * <li>file:/c:/directory1/directory2/filename</li>
	 * </ul>
	 */
	file("/((PATH)/(FILENAME))|/(FILENAME)"),

	/**
	 * <ul>
	 * <li>http://localhost/filename</li>
	 * <li>http://localhost/path/filename</li>
	 * <li>http://localhost:8080?id=1234</li>
	 * </ul>
	 */
	http("//(HOST)[/|?]((PATH)/(FILENAME))|(FILENAME)|(RESOURCEID)"), ;

	// *** static part **************************************************************************************************

	private static final Pattern MainUriPattern;

	static {

	   LOGGER.debug("compile main uri regExp: {}", mainUriRegExp());

	   try {
		MainUriPattern = Pattern.compile(mainUriRegExp());
	   } catch (final PatternSyntaxException pse) {
		LOGGER.error("illegal main uri regExp: {}", mainUriRegExp());
		throw pse;
	   }

	}

	// *** instance part *********************************************************************************************

	private final Pattern pattern;

	ResourceUriEnum(final String regExp) {

	   final Map<String, String> WORDS = new HashMap<String, String>();
	   WORDS.put("PATH", ".+");
	   WORDS.put("FILENAME", ".+");
	   WORDS.put("RESOURCEID", ".+");
	   WORDS.put("HOST", ".+");

	   String mergeRegExp = regExp;

	   for (final Entry<String, String> entry : WORDS.entrySet()) {
		mergeRegExp = mergeRegExp.replace(entry.getKey(), WORDS.get(entry.getKey()));
	   }

	   LOGGER.debug("{} compile local uri regExp: {}", new Object[] { name(), mergeRegExp });

	   try {
		pattern = Pattern.compile(mergeRegExp);
	   } catch (final PatternSyntaxException pse) {
		LOGGER.error("illegal local uri regExp: {}", mergeRegExp);
		throw pse;
	   }
	}

	/**
	 * @return local pattern of the binding type
	 */
	public Pattern getPattern() {
	   return pattern;
	}

	/**
	 * @return main regExp used to identify the type (classpath,file,...) of an uri
	 */
	private static String mainUriRegExp() {
	   final StringBuilder str = new StringBuilder();
	   final ResourceUriEnum[] values = ResourceUriEnum.values();
	   str.append("(");
	   for (int i = 0; i < values.length - 1; i++) {
		str.append(values[i]).append("|");
	   }
	   str.append(values[values.length - 1]);
	   str.append("):(.*)");
	   return str.toString();
	}

	/**
	 * matcher method
	 * 
	 * @param uri
	 * @return bean representation of the uri
	 */
	public static RegExpSample.ResourceBean match(final String uri) {

	   if (StringHelper.isEmpty(uri)) { return null; }

	   final Matcher mainMatcher = MainUriPattern.matcher(uri);

	   LOGGER.debug("\nmatching uri: {} \nwith regExp: {}", new Object[] { uri, mainUriRegExp() });

	   if (mainMatcher.find() && mainMatcher.groupCount() >= 2) {

		final String strType = mainMatcher.group(1); // extract bindind type
		final String localUri = mainMatcher.group(2); // extract the rest of the uri

		ResourceBean bean = null;
		final ResourceUriEnum type = ResourceUriEnum.valueOf(strType);
		final Matcher localMatcher = type.getPattern().matcher(localUri); // create matcher for rest of uri

		if (localMatcher.find()) {
		   if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("matcher group count: {}", localMatcher.groupCount());
			for (int i = 0; i <= localMatcher.groupCount(); i++) {
			   LOGGER.debug("token {} : {}", new Object[] { i, localMatcher.group(i) });
			}
			if (localMatcher.groupCount() >= 4) {
			   bean = new ResourceBean();
			   bean.setType(type);
			   if (localMatcher.group(4) == null) {
				bean.setName(localMatcher.group(3));
				bean.setPath(localMatcher.group(2));
			   } else {
				bean.setName(localMatcher.group(4));
				bean.setPath(null);
			   }
			} else {
			   bean = null;
			}
		   }

		   return bean;
		}

		return null;
	   }
	   return null;
	}
   }

   /**
    * POJO representation of a string uri resource which have been match by regexp
    * 
    * @author jraduget
    */
   public static class ResourceBean {

	private ResourceUriEnum type;
	private String name;
	private String path;

	public ResourceBean() {
	}

	public ResourceBean(final ResourceUriEnum type, final String path, final String name) {
	   super();
	   this.type = type;
	   this.name = name;
	   this.path = path;
	}

	public static ResourceBean parse(final String resourceUri) {
	   return ResourceUriEnum.match(resourceUri);
	}

	public ResourceUriEnum getType() {
	   return type;
	}

	public void setType(final ResourceUriEnum type) {
	   this.type = type;
	}

	public String getName() {
	   return name;
	}

	public void setName(final String name) {
	   this.name = name;
	}

	public String getPath() {
	   return path;
	}

	public void setPath(final String path) {
	   this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	   final int prime = 31;
	   int result = 1;
	   result = prime * result + (name == null ? 0 : name.hashCode());
	   result = prime * result + (path == null ? 0 : path.hashCode());
	   result = prime * result + (type == null ? 0 : type.hashCode());
	   return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
	   if (this == obj) { return true; }
	   if (obj == null) { return false; }
	   if (!(obj instanceof ResourceBean)) { return false; }
	   final ResourceBean other = (ResourceBean) obj;
	   if (name == null) {
		if (other.name != null) { return false; }
	   } else if (!name.equals(other.name)) { return false; }
	   if (path == null) {
		if (other.path != null) { return false; }
	   } else if (!path.equals(other.path)) { return false; }
	   if (type == null) {
		if (other.type != null) { return false; }
	   } else if (!type.equals(other.type)) { return false; }
	   return true;
	}

   }

   // ** test part ****************************************************************************************************
   @Test
   public void parseIllegal() {
	final Map<String, ResourceBean> illegalTests = new LinkedHashMap<String, ResourceBean>();

	illegalTests.put("unknown", null);
	illegalTests.put("unknown:/", null);
	illegalTests.put("unknown://", null);

	assertIllegal(illegalTests);
   }

   @Test
   public void parseIllegalClasspath() {
	final Map<String, ResourceBean> illegalTests = new LinkedHashMap<String, ResourceBean>();

	illegalTests.put("classpath", null);
	illegalTests.put("classpath:/", null);
	// illegalTests.put("classpath://", null);

	assertIllegal(illegalTests);
   }

   @Test
   public void parseLegalClasspath() {
	final Map<String, ResourceBean> legalTests = new LinkedHashMap<String, ResourceBean>();

	legalTests.put("classpath:/context", new ResourceBean(ResourceUriEnum.classpath, null, "context"));
	legalTests.put("classpath:/context.xml", new ResourceBean(ResourceUriEnum.classpath, null, "context.xml"));
	legalTests.put("classpath:/localpath1/localpath2/context.xml", new ResourceBean(ResourceUriEnum.classpath, "localpath1/localpath2", "context.xml"));
	legalTests.put("classpath:/localpath/context.xml", new ResourceBean(ResourceUriEnum.classpath, "localpath", "context.xml"));

	assertLegal(legalTests);
   }

   @Test
   public void parseLegalFile() {
	final Map<String, ResourceBean> legalTests = new LinkedHashMap<String, ResourceBean>();

	legalTests.put("file:/c:/context", new ResourceBean(ResourceUriEnum.file, "c:", "context"));
	legalTests.put("file:/c:/context.xml", new ResourceBean(ResourceUriEnum.file, "c:", "context.xml"));
	legalTests.put("file:/c:/localpath1/localpath2/context.xml", new ResourceBean(ResourceUriEnum.file, "c:/localpath1/localpath2", "context.xml"));
	legalTests.put("file:/c:/localpath/context.xml", new ResourceBean(ResourceUriEnum.file, "c:/localpath", "context.xml"));

	assertLegal(legalTests);
   }

   /**
    * global assertions for legal uri
    * 
    * @param legalTests pair of uri / expected value
    */
   void assertLegal(final Map<String, ResourceBean> legalTests) {

	for (final Entry<String, ResourceBean> entry : legalTests.entrySet()) {
	   final ResourceBean bean = ResourceBean.parse(entry.getKey());
	   assertNotNull("assertion error for: " + entry.getKey(), bean);
	   assertEquals("assertion error for: " + entry.getKey(), entry.getValue(), bean);
	   LOGGER.debug(StringHelper.replicate("*", 120));
	}
   }

   /**
    * global assertions for illegal uri
    * 
    * @param illegalTests pair of uri / expected value
    */
   void assertIllegal(final Map<String, ResourceBean> illegalTests) {

	for (final Entry<String, ResourceBean> entry : illegalTests.entrySet()) {
	   final ResourceBean bean = ResourceBean.parse(entry.getKey());
	   if (entry.getValue() == null) {
		assertNull("assertion error for: " + entry.getKey(), bean);
	   } else {
		assertEquals("assertion error for: " + entry.getKey(), entry.getValue(), bean);
	   }
	   LOGGER.debug(StringHelper.replicate("*", 120));
	}
   }

   /**
    * 
    */
   @Test
   public void singleTest() {
	assertEquals("file:/_/lulu", "file:/${gg}/lulu".replaceFirst("\\$\\{.+\\}", "_"));
	assertEquals("file:/_/lulu/_/", "file:/${gg}/lulu/${hhhh}/".replaceAll("\\$\\{.+\\}", "_"));
   }
}
