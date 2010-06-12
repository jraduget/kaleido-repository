/*
 * $License$
 */
package org.kaleidofoundry.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class UrlSample {

   private final static Logger LOGGER = LoggerFactory.getLogger(UrlSample.class);

   @Test
   public void createUrl() throws MalformedURLException {

	List<String> urlsToTest = new ArrayList<String>();

	urlsToTest.add("file:/c:/localpath1/localpath2/context.xml");
	urlsToTest.add("http://localhost:8080?id=1234");
	urlsToTest.add("http://localhost/resourcePath/resourceName");

	for (String uri : urlsToTest) {
	   debugUrl(new URL(uri));
	}
   }

   private void debugUrl(final URL uri) {

	LOGGER.debug("protocol={}", uri.getProtocol());
	LOGGER.debug("host={}", uri.getHost());
	LOGGER.debug("path={}", uri.getPath());
	LOGGER.debug("file={}", uri.getFile());
	LOGGER.debug("port={}", uri.getPort());
	LOGGER.debug("query={}", uri.getQuery());
	LOGGER.debug("user info={}", uri.getUserInfo());
	LOGGER.debug("");
   }
}
