/*
 * $License$
 */
package org.kaleidofoundry.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class UriSample {

   private final static Logger LOGGER = LoggerFactory.getLogger(UriSample.class);

   @Test
   public void createUri() throws URISyntaxException {

	List<String> urisToTest = new ArrayList<String>();

	urisToTest.add("classpath:/context");
	urisToTest.add("classpath:/localpath1/localpath2/context.xml");

	urisToTest.add("classpath://context");
	urisToTest.add("classpath://localpath1/localpath2/context.xml");

	urisToTest.add("file:/c:/localpath1/localpath2/context.xml");
	urisToTest.add("file://c:/localpath1/localpath2/context.xml");
	urisToTest.add("file:///c:/localpath1/localpath2/context.xml");

	urisToTest.add("http://localhost:8080?id=1234");
	urisToTest.add("http://localhost/resourcePath/resourceName");

	// custom
	urisToTest.add("jdbc://context");
	urisToTest.add("ext://context");
	urisToTest.add("jpa://context");

	for (String uri : urisToTest) {
	   debugUri(new URI(uri));
	}
   }

   private void debugUri(final URI uri) {

	LOGGER.debug(uri.toString());
	LOGGER.debug("\tscheme=" + uri.getScheme());
	LOGGER.debug("\thost=" + uri.getHost());
	LOGGER.debug("\tpath=" + uri.getPath());
	LOGGER.debug("\tport=" + uri.getPort());
	LOGGER.debug("\tquery=" + uri.getQuery());
	LOGGER.debug("\trawpath=" + uri.getRawPath());
	LOGGER.debug("\trawFragment=" + uri.getRawFragment());
	LOGGER.debug("\trawAuthority=" + uri.getRawAuthority());
	LOGGER.debug("\trawUserInfo=" + uri.getRawUserInfo());
	LOGGER.debug("");
   }
}
