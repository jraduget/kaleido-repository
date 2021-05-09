/*
 *  Copyright 2008-2021 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.model.ConfigurationModel;
import org.kaleidofoundry.core.config.model.ConfigurationProperty;
import org.kaleidofoundry.core.config.model.FireChangesReport;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * {@link ConfigurationController} integration test
 * 
 * @author jraduget
 */
public abstract class AbstractRestConfigurationTest  {

   //private static final String SERVER_BASE_URL = "http://localhost:8080/kaleido-it";
   private static final String SERVER_BASE_URL = "http://localhost:8380/kaleido-it";

   private static final List<String> KEYS = Arrays.asList("//myapp/name", "//myapp/admin/email", "//myapp/sample/date", "//myapp/sample/float",
	   "//myapp/sample/boolean");

   private Client client;

   @Before
   public void setup() {
	// client configuration
	ClientConfig config = new DefaultClientConfig();
	client = Client.create(config);
	// remove myConfig items if needed
	try {
	   getMyConfigBaseResource().delete();
	} catch (UniformInterfaceException cnfe) {
	}
	
	// remove myNewConfig items if needed
	try {
	   getBaseResource().path("myNewConfig").delete();
	} catch (UniformInterfaceException cnfe) {
	}
	
	if (Boolean.valueOf(getBaseResource().path("myNewConfig").path("registered").get(String.class))) {
	   getBaseResource().path("myNewConfig").path("unregister").put();
	}
   }

   public abstract MediaType getMedia();

   @Test
   public void getConfigurationModel() {
	try {
	   getBaseResource().path("noConfig").path("myapp.name").accept(getMedia().getType()).get(String.class);
	   fail("noConfig must not be registered as a valid configuration");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("noConfig"));
	}

	ConfigurationModel model = getMyConfigBaseResource().accept(getMedia().getType()).get(ConfigurationModel.class);
	assertNotNull(model);
	assertNull(model.getId());
	assertEquals("myConfig", model.getName());
	assertEquals("classpath:/config/myConfig.properties", model.getUri());
	assertEquals(KEYS.size(), model.getProperties().size());
   }

   @Test
   public void findConfigurationModel() {
	List<ConfigurationModel> response = getBaseResource().accept(getMedia().getType()).get(new GenericType<List<ConfigurationModel>>() {
	});
	assertNotNull(response);
	assertEquals(4, response.size());

	response = getBaseResource().queryParam("text", "my").accept(getMedia().getType()).get(new GenericType<List<ConfigurationModel>>() {
	});
	assertNotNull(response);
	assertEquals(4, response.size());

	response = getBaseResource().queryParam("text", "myCacheConfig").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationModel>>() {
		});
	assertNotNull(response);
	assertEquals(1, response.size());
   }

   @Test
   public void removeConfigurationModel() {
	try {
	   getBaseResource().path("noConfig").delete();
	   fail("noConfig must not be registered as a valid configuration");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("noConfig"));
	}
   }

   @Test
   public void registerConfiguration() {
	assertFalse(Boolean.valueOf(getBaseResource().path("myNewConfig").path("registered").get(String.class)));

	getBaseResource().path("myNewConfig").path("register").queryParam("resourceURI", "classpath:/config/myNewConfig.properties").put();

	assertTrue(Boolean.valueOf(getBaseResource().path("myNewConfig").path("registered").get(String.class)));
	assertEquals("my new registerd application", getBaseResource().path("myNewConfig").path("myapp.name").accept(getMedia().getType()).get(String.class));
	assertEquals("myadmin@mynewsociete.com", getBaseResource().path("myNewConfig").path("myapp.admin.email").accept(getMedia().getType()).get(String.class));
	assertEquals("54.321", getBaseResource().path("myNewConfig").path("myapp.sample.float").accept(getMedia().getType()).get(String.class));
	assertEquals("true", getBaseResource().path("myNewConfig").path("myapp.sample.boolean").accept(getMedia().getType()).get(String.class));
   }

   @Test
   public void unregisterConfiguration() {
	try {
	   getBaseResource().path("noConfig").path("unregister").put();
	   fail("noConfig must not be registered as a valid configuration");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("noConfig"));
	}
   }

   @Test
   public void getPropertyValue() {
	String strResponse = getMyConfigBaseResource().path("myapp.name").accept(getMedia().getType()).get(String.class);
	System.out.println(strResponse);
	assertNotNull(strResponse);
	assertEquals("my new application", strResponse);

	strResponse = getMyConfigBaseResource().path("myapp.sample.float").accept(getMedia().getType()).get(String.class);
	System.out.println(strResponse);
	assertNotNull(strResponse);
	assertEquals("123.45", strResponse);

	try {
	   strResponse = getMyConfigBaseResource().path("noKey").accept(getMedia().getType()).get(String.class);
	   fail("noKey property must not exists");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("myConfig"));
	   assertTrue(cnfe.getMessage().contains("noKey"));
	}
   }

   @Test
   public void getProperty() {
	ConfigurationProperty response = getMyConfigBaseResource().path("property").path("myapp.name").accept(getMedia().getType())
		.get(ConfigurationProperty.class);
	System.out.println(response);
	assertNotNull(response);
	// assertEquals("myConfig", response.getConfiguration().getName());
	assertEquals("//myapp/name", response.getName());
	assertEquals(ConfigurationController.DefaultDescription, response.getDescription());

	response = getMyConfigBaseResource().path("property").path("myapp.sample.float").accept(getMedia().getType()).get(ConfigurationProperty.class);
	System.out.println(response);
	assertNotNull(response);
	// assertEquals("myConfig", response.getConfiguration().getName());
	assertEquals("//myapp/sample/float", response.getName());
	assertEquals(ConfigurationController.DefaultDescription, response.getDescription());

	try {
	   response = getMyConfigBaseResource().path("noKey").accept(getMedia().getType()).get(ConfigurationProperty.class);
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
	String strResponse = getMyConfigBaseResource().path("myapp.name").accept(getMedia().getType()).get(String.class);
	assertNotNull(strResponse);
	assertEquals("my new application", strResponse);
	try {
	   // update an exiting property
	   getMyConfigBaseResource().path("myapp.name").queryParam("value", "my updated application").put();
	   // check update getting new value
	   strResponse = getMyConfigBaseResource().path("myapp.name").accept(getMedia().getType()).get(String.class);
	   assertNotNull(strResponse);
	   assertEquals("my updated application", strResponse);

	   // create a new property
	   getMyConfigBaseResource().path("newProp").queryParam("value", "my new prop value").put();
	   // check update getting new value
	   strResponse = getMyConfigBaseResource().path("newProp").accept(getMedia().getType()).get(String.class);
	   assertNotNull(strResponse);
	   assertEquals("my new prop value", strResponse);

	} finally {
	   // restore initial value
	   getMyConfigBaseResource().path("myapp.name").queryParam("value", "my new application").put();
	   // remove created property
	   getMyConfigBaseResource().path("property").path("newProp").delete();
	}

	// check fired event count
	FireChangesReport report = getMyConfigBaseResource().path("fireChanges").accept(getMedia().getType()).get(FireChangesReport.class);
	assertNotNull(report);
	assertEquals(new Integer(1), report.getCreated());
	assertEquals(new Integer(2), report.getUpdated());
	assertEquals(new Integer(1), report.getRemoved());
   }

   @Test
   public void removeProperty() {
	// set initial value
	getMyConfigBaseResource().path("keyToRemove").queryParam("value", "eheh").put();
	// remove it
	getMyConfigBaseResource().path("property").path("keyToRemove").delete();
	// check fired event count
	FireChangesReport report = getMyConfigBaseResource().path("fireChanges").accept(getMedia().getType()).get(FireChangesReport.class);
	assertNotNull(report);
	assertEquals(new Integer(1), report.getCreated());
	assertEquals(new Integer(0), report.getUpdated());
	assertEquals(new Integer(1), report.getRemoved());
	// remove it
	try {
	   getMyConfigBaseResource().path("property").path("keyToRemove").delete();
	   fail("property keyToRemove was not removed");
	} catch (UniformInterfaceException cnfe) {
	   assertEquals(404, cnfe.getResponse().getStatus());
	   assertTrue(cnfe.getMessage().contains("myConfig"));
	   assertTrue(cnfe.getMessage().contains("keyToRemove"));
	}
   }

   @Test
   public void findPropertiesByConfig() {
	List<ConfigurationProperty> response = getBaseResource().path("properties").queryParam("config", "myConfig").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(5, response.size());
	for (ConfigurationProperty property : response) {
	   assertNotNull(property.getName());
	   assertTrue("can't found property" + property.getName(), KEYS.contains(property.getName()));
	}
   }

   @Test
   public void findPropertiesByText() {
	List<ConfigurationProperty> response = getBaseResource().path("properties").queryParam("config", "myConfig").queryParam("text", "myapp")
		.accept(getMedia().getType()).get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(5, response.size());
	for (ConfigurationProperty property : response) {
	   assertNotNull(property.getName());
	   assertTrue("can't found property" + property.getName(), KEYS.contains(property.getName()));
	}

	response = getBaseResource().path("properties").queryParam("config", "myConfig").queryParam("text", "myapp/sample").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(3, response.size());

	response = getBaseResource().path("properties").queryParam("config", "myConfig").queryParam("text", "myapp/admin/email").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(1, response.size());

	response = getBaseResource().path("properties").queryParam("config", "myConfig").queryParam("text", "?").accept(getMedia().getType())
		.get(new GenericType<List<ConfigurationProperty>>() {
		});
	assertNotNull(response);
	assertEquals(0, response.size());

   }

   @Test
   public void putProperty() {

	// get initial value
	ConfigurationProperty property = getMyConfigBaseResource().path("property").path("myapp.name").accept(getMedia().getType())
		.get(ConfigurationProperty.class);
	assertNotNull(property);
	assertEquals("//myapp/name", property.getName());
	assertEquals(ConfigurationController.DefaultDescription, property.getDescription());

	// create or update an exiting property
	ClientResponse response = getMyConfigBaseResource().path("property").accept(getMedia().getType())
		.put(ClientResponse.class, new ConfigurationProperty("myapp.name", "my new application", String.class, "new description"));
	assertNotNull(response);
	assertEquals(204, response.getStatus());

	// check updates
	property = getMyConfigBaseResource().path("property").path("myapp.name").accept(getMedia().getType()).get(ConfigurationProperty.class);
	assertNotNull(property);
	assertEquals("//myapp/name", property.getName());
	assertEquals("new description", property.getDescription());

	// update an exiting property
	response = getMyConfigBaseResource().path("property").accept(getMedia().getType())
		.put(ClientResponse.class, new ConfigurationProperty("myapp.name", "my new application", String.class, "new description2"));

	// check updates
	property = getMyConfigBaseResource().path("property").path("myapp.name").accept(getMedia().getType()).get(ConfigurationProperty.class);
	assertNotNull(property);
	assertEquals("//myapp/name", property.getName());
	assertEquals("new description2", property.getDescription());
   }

   private URI getBaseURI() {
	return UriBuilder.fromUri(SERVER_BASE_URL).build();
   }

   private WebResource getBaseResource() {
	WebResource resource = client.resource(getBaseURI());
	return resource.path("rest").path("configurations");
   }

   private WebResource getMyConfigBaseResource() {
	WebResource resource = client.resource(getBaseURI());
	return resource.path("rest").path("configurations").path("myConfig");
   }

}
