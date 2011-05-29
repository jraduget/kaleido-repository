/*
 *  Copyright 2008-2011 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.config;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.entity.ConfigurationProperty;
import org.kaleidofoundry.core.config.entity.FireChangesReport;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * {@link ConfigurationManagerBean} integration test
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractRestConfigurationTest extends Assert {

   private Client client;

   private final List<String> keys = Arrays.asList("//myapp/name", "//myapp/admin/email", "//myapp/sample/date", "//myapp/sample/float",
	   "//myapp/sample/boolean");

   @Before
   public void setup() {
	ClientConfig config = new DefaultClientConfig();
	client = Client.create(config);
   }

   public abstract MediaType getMedia();

   @Test
   public void notFoundConfiguration() {
	try {
	   WebResource resource = client.resource(getBaseURI());
	   resource.path("rest").path("configurations").path("noConfig").path("get").path("myapp.name").accept(getMedia().getType()).get(String.class);
	   fail("noConfig must not be registered as a valid configuration");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("noConfig"));
	}
   }

   @Test
   public void get() {
	String strResponse = getBaseResource().path("get").path("myapp.name").accept(getMedia().getType()).get(String.class);
	System.out.println(strResponse);
	assertNotNull(strResponse);
	assertEquals("my new application", strResponse);

	strResponse = getBaseResource().path("get").path("myapp.sample.float").accept(getMedia().getType()).get(String.class);
	System.out.println(strResponse);
	assertNotNull(strResponse);
	assertEquals("123.45", strResponse);

	try {
	   strResponse = getBaseResource().path("get").path("noKey").accept(getMedia().getType()).get(String.class);
	   fail("noKey property must not exists");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("myConfig"));
	   assertTrue(cnfe.getMessage().contains("noKey"));
	}
   }

   @Test
   public void getProperty() {
	ConfigurationProperty response = getBaseResource().path("getProperty").path("myapp.name").accept(getMedia().getType()).get(ConfigurationProperty.class);
	System.out.println(response);
	assertNotNull(response);
	// assertEquals("myConfig", response.getConfiguration().getName());
	assertEquals("//myapp/name", response.getName());
	assertEquals(ConfigurationManagerBean.DefaultDescription, response.getDescription());

	response = getBaseResource().path("getProperty").path("myapp.sample.float").accept(getMedia().getType()).get(ConfigurationProperty.class);
	System.out.println(response);
	assertNotNull(response);
	// assertEquals("myConfig", response.getConfiguration().getName());
	assertEquals("//myapp/sample/float", response.getName());
	assertEquals(ConfigurationManagerBean.DefaultDescription, response.getDescription());

	try {
	   response = getBaseResource().path("get").path("noKey").accept(getMedia().getType()).get(ConfigurationProperty.class);
	   fail("noKey property must not exists");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("myConfig"));
	   assertTrue(cnfe.getMessage().contains("noKey"));
	}
   }

   @Test
   public void set() {
	// get initial value
	String strResponse = getBaseResource().path("get").path("myapp.name").accept(getMedia().getType()).get(String.class);
	assertNotNull(strResponse);
	assertEquals("my new application", strResponse);
	try {
	   // update an exiting property
	   strResponse = getBaseResource().path("set").path("myapp.name").queryParam("value", "my updated application").accept(getMedia().getType())
		   .get(String.class);
	   // check update getting new value
	   strResponse = getBaseResource().path("get").path("myapp.name").accept(getMedia().getType()).get(String.class);
	   assertNotNull(strResponse);
	   assertEquals("my updated application", strResponse);

	   // create a new property
	   strResponse = getBaseResource().path("set").path("newProp").queryParam("value", "my new prop value").accept(getMedia().getType()).get(String.class);
	   // check update getting new value
	   strResponse = getBaseResource().path("get").path("newProp").accept(getMedia().getType()).get(String.class);
	   assertNotNull(strResponse);
	   assertEquals("my new prop value", strResponse);

	} finally {
	   // restore initial value
	   getBaseResource().path("set").path("myapp.name").queryParam("value", "my new application").accept(getMedia().getType())
		   .get(ConfigurationProperty.class);
	   // remove created property
	   getBaseResource().path("remove").path("newProp").delete();
	}

	// check fired event count
	FireChangesReport report = getBaseResource().path("fireChanges").accept(getMedia().getType()).get(FireChangesReport.class);
	assertNotNull(report);
	assertEquals(new Integer(1), report.getCreated());
	assertEquals(new Integer(2), report.getUpdated());
	assertEquals(new Integer(1), report.getRemoved());
   }

   @Test
   public void remove() {
	// set initial value
	getBaseResource().path("set").path("keyToRemove").queryParam("value", "eheh").accept(getMedia().getType()).get(ConfigurationProperty.class);
	// remove it
	getBaseResource().path("remove").path("keyToRemove").delete();
	// check fired event count
	FireChangesReport report = getBaseResource().path("fireChanges").accept(getMedia().getType()).get(FireChangesReport.class);
	assertNotNull(report);
	assertEquals(new Integer(1), report.getCreated());
	assertEquals(new Integer(0), report.getUpdated());
	assertEquals(new Integer(1), report.getRemoved());
	// remove it
	try {
	   getBaseResource().path("remove").path("keyToRemove").delete();
	   fail("property keyToRemove was not removed");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("myConfig"));
	   assertTrue(cnfe.getMessage().contains("keyToRemove"));
	}
   }

   @Test
   public void keys() {
	List<ConfigurationProperty> response = getBaseResource().path("keys").accept(getMedia().getType()).get(new GenericType<List<ConfigurationProperty>>() {
	});
	assertNotNull(response);
	assertEquals(5, response.size());
	for (ConfigurationProperty property : response) {
	   assertNotNull(property.getName());
	   assertTrue("can't found property" + property.getName(), keys.contains(property.getName()));
	}
   }

   @Test
   public void keysWithPrefix() {
	List<ConfigurationProperty> response = getBaseResource().path("keys").queryParam("prefix", "myapp").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(5, response.size());
	for (ConfigurationProperty property : response) {
	   assertNotNull(property.getName());
	   assertTrue("can't found property" + property.getName(), keys.contains(property.getName()));
	}

	response = getBaseResource().path("keys").queryParam("prefix", "myapp/sample").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(3, response.size());

	response = getBaseResource().path("keys").queryParam("prefix", "myapp/admin/email").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(1, response.size());

	response = getBaseResource().path("keys").queryParam("prefix", "?").accept(getMedia().getType()).get(new GenericType<List<ConfigurationProperty>>() {
	});
	assertNotNull(response);
	assertEquals(0, response.size());

   }

   @Test
   public void putProperty() {

	// get initial value
	ConfigurationProperty property = getBaseResource().path("getProperty").path("myapp.name").accept(getMedia().getType()).get(ConfigurationProperty.class);
	assertNotNull(property);
	assertEquals("//myapp/name", property.getName());
	assertEquals(ConfigurationManagerBean.DefaultDescription, property.getDescription());

	// update an exiting property
	ClientResponse response = getBaseResource().path("putProperty").accept(getMedia().getType())
		.put(ClientResponse.class, new ConfigurationProperty("myapp.name", "my new application", String.class, "new description"));
	assertNotNull(response);
	assertEquals(204, response.getStatus());

	// check updates
	property = getBaseResource().path("getProperty").path("myapp.name").accept(getMedia().getType()).get(ConfigurationProperty.class);
	assertNotNull(property);
	assertEquals("//myapp/name", property.getName());
	// assertEquals("new description", property.getDescription());

   }

   private URI getBaseURI() {
	return UriBuilder.fromUri("http://localhost:8080/kaleido-it").build();
   }

   private WebResource getBaseResource() {
	WebResource resource = client.resource(getBaseURI());
	return resource.path("rest").path("configurations").path("myConfig");
   }
}
