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
package org.kaleidofoundry.core.plugin;

/**
 * @author Jerome RADUGET
 */
public interface PluginConstants {

   /** Plugin are considered standard, if interfaces / classes annotated are package into */
   public static final String PACKAGE_STANDARD = "org.kaleidofoundry";

   // Annotation processing info ***************************************************************************************

   /** Plugin annotation processor package name, used for meta file plugin generation */
   public static final String META_PLUGIN_PATH = "";

   /** Plugin annotation processor output plugin file, used for plugin meta-file generation */
   public static final String META_PLUGIN_FILE = "META-INF/org.kaleidofoundry.core.plugin";

   /** Plugin annotation processor output plugin implementation file, used for plugin meta-file generation */
   public static final String META_PLUGIN_IMPLEMENTATION_FILE = "META-INF/org.kaleidofoundry.core.plugin.implementation";

   // Commons kaleido plugin name **************************************************************************************

   /** I18n unique key identifier */
   public static final String I18N = "i18n";

   /** ResourceStore unique key identifier */
   public static final String RESOURCE_STORE = "resourcestore";

   /** DataSource unique key identifier */
   public static final String DATASOURCE = "datasource";

   /** Cache unique key identifier */
   public static final String CACHE = "cache";

   /** Mail unique key identifier */
   public static final String MAIL = "mail";

   /** Messaging unique key identifier */
   public static final String MESSAGING = "messaging";

   /** Rule unique key identifier */
   public static final String RULE = "rule";
}
