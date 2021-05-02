/*
 * Copyright 2008-2021 the original author or authors
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
package org.kaleidofoundry.core.config;

import java.net.URISyntaxException;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author jraduget
 */
public class YamlConfigurationTest extends AbstractConfigurationTest {

   private final TimeZone defaultTimeZone = TimeZone.getDefault();

   public YamlConfigurationTest() throws ResourceException, URISyntaxException {
	super();
   }

   @Override
   @Before
   public void setup() throws ResourceException, URISyntaxException {
	// avoid date offset in yaml
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	// ancestor setup
	super.setup();
   }

   @Override
   @After
   public void cleanup() throws ResourceException {
	super.cleanup();
	// restore default system timezone
	TimeZone.setDefault(defaultTimeZone);
   }

   @Override
   protected Configuration newInstance() throws ResourceException, URISyntaxException {
	return new YamlConfiguration("yamlConfig", "classpath:/config/test.yaml", new RuntimeContext<Configuration>(Configuration.class));
   }

   // @Test
   public void store2() throws ResourceException {
	Configuration config = new YamlConfiguration("yaml2", "file:/test.yaml", new RuntimeContext<Configuration>(Configuration.class));
	config.load();
	config.store();
   }
}
