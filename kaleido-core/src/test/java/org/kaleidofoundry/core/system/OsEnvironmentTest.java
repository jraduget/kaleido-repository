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
package org.kaleidofoundry.core.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class OsEnvironmentTest extends Assert {

   private static final Logger LOGGER = LoggerFactory.getLogger(OsEnvironmentTest.class);

   private OsEnvironment environment;

   @Before
   public void setup() throws IOException {
	environment = new OsEnvironment();
   }

   @Test
   public void testGetProperty() throws IOException {
	String os = environment.getProperty("PATH");
	LOGGER.info(os);
	assertNotNull(os);
	assertFalse("".equals(os));
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalClear() throws IOException {
	environment.clear();
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalRemove() throws IOException {
	environment.remove("foo");
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalSetProperty() throws IOException {
	environment.setProperty("foo", "foo");
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalLoad() throws IOException {
	environment.load((InputStream) null);
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalPut() throws IOException {
	environment.put("foo", "foo");
   }

   @Test(expected = IllegalAccessError.class)
   public void testIllegalStore() throws IOException {
	environment.store((Writer) null, null);
   }

}
