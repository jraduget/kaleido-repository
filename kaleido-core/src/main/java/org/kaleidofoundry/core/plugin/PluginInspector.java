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

import static org.kaleidofoundry.core.i18n.InternalBundleHelper.PluginMessageBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.kaleidofoundry.core.plugin.model.Plugin;
import org.kaleidofoundry.core.plugin.processor.PluginAnnotationProcessor;
import org.kaleidofoundry.core.system.JavaSystemHelper;
import org.kaleidofoundry.core.util.ReflectionHelper;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
public class PluginInspector {

   private static final Logger LOGGER = LoggerFactory.getLogger(PluginInspector.class);

   private final Set<Plugin<?>> pluginsSet;
   private final Set<Plugin<?>> pluginImplsSet;
   private final Set<String> echoMessages;

   private final String pluginMetaInfPath;
   private final String pluginImplementationMetaInfPath;

   /**
    * load inspector with default plugin meta inf file :
    * <ul>
    * <li>META-INF/org.kaleidofoundry.core.plugin</li>
    * <li>META-INF/org.kaleidofoundry.core.plugin.implementation</li>
    * </ul>
    */
   public PluginInspector() {
	pluginsSet = new LinkedHashSet<Plugin<?>>();
	pluginImplsSet = new LinkedHashSet<Plugin<?>>();
	echoMessages = new LinkedHashSet<String>();
	pluginMetaInfPath = getDefaultPluginMetaInfPath();
	pluginImplementationMetaInfPath = getDefaultPluginImplementationMetaInfPath();

   }

   /**
    * load inspector with specified plugin file
    * 
    * @param pluginMetaInfPath
    * @param pluginImplementationMetaInfPath
    */
   public PluginInspector(final String pluginMetaInfPath, final String pluginImplementationMetaInfPath) {
	pluginsSet = new TreeSet<Plugin<?>>();
	pluginImplsSet = new TreeSet<Plugin<?>>();
	echoMessages = new LinkedHashSet<String>();
	this.pluginMetaInfPath = pluginMetaInfPath;
	this.pluginImplementationMetaInfPath = pluginImplementationMetaInfPath;
   }

   /**
    * @return default full local path (from classpath) for plugin declared file
    */
   public static String getDefaultPluginMetaInfPath() {
	final StringBuilder path = new StringBuilder();

	path.append(PluginConstants.META_PLUGIN_PATH);
	if (path.length() > 0) {
	   path.append("/");
	}
	path.append(PluginConstants.META_PLUGIN_FILE);
	return path.toString();
   }

   /**
    * @return default full local path (from classpath) for plugin implementation declared file
    */
   public static String getDefaultPluginImplementationMetaInfPath() {
	final StringBuilder path = new StringBuilder();

	path.append(PluginConstants.META_PLUGIN_PATH);
	if (path.length() > 0) {
	   path.append("/");
	}
	path.append(PluginConstants.META_PLUGIN_IMPLEMENTATION_FILE);
	return path.toString();
   }

   /**
    * @return plugin MetaInf file path which have been loaded {@link #PluginInspector()}
    */
   public String getPluginMetaInfPath() {
	return pluginMetaInfPath;
   }

   /**
    * @return plugin implementation MetaInf file path which have been loaded {@link #PluginInspector()}
    */
   public String getPluginImplementationMetaInfPath() {
	return pluginImplementationMetaInfPath;
   }

   /**
    * @return set of plugin loaded (be sure to call method {@link #loadPluginMetaData()} first.
    */
   public Set<Plugin<?>> getPluginsSet() {
	return pluginsSet;
   }

   /**
    * @return set of plugin implementation loaded (be sure to call method {@link #loadPluginImplementationMetaData()} first.
    */
   public Set<Plugin<?>> getPluginImplsSet() {
	return pluginImplsSet;
   }

   /**
    * @return ordered set of messages, feed at load time {@link #loadPluginMetaData()} & {@link #loadPluginImplementationMetaData()} and at
    *         coherence check {@link #checkDeclarations()}
    */
   public Set<String> getEchoMessages() {
	return echoMessages;
   }

   /**
    * Load and registered plugin meta data,<br/>
    * The process start by reading text file, containing interface name list of annotated plugin interface<br/>
    * this file is generate at compile time, by plugin annotation processor {@link PluginAnnotationProcessor} <br/>
    * None coherence check is done at this step, use <code>checkDeclaration</code> to do so <br/>
    * 
    * @return unmodifiable set of plugin interface found by annotation processing
    * @throws PluginRegistryException target cause can be:
    *            <ul>
    *            <li>IOException if problem reading declare plugin interface file</li>
    *            <li>ClassNotFoundException if annotation plugin processor, provide/visit interface name that is not present in classpath</li>
    *            </ul>
    */
   public Set<Plugin<?>> loadPluginMetaData() throws PluginRegistryException {

	try {
	   final Enumeration<URL> urls = JavaSystemHelper.getResources(pluginMetaInfPath);

	   pluginsSet.clear();

	   if (urls != null) {
		while (urls.hasMoreElements()) {
		   final URL url = urls.nextElement();
		   echoMessages.add(PluginMessageBundle.getMessage("plugin.info.visitor.resource.found", "interfaces", url.getPath()));

		   InputStream resourceInput = null;
		   Reader reader = null;
		   BufferedReader buffReader = null;
		   String line;

		   try {
			resourceInput = url.openStream();
			reader = new InputStreamReader(resourceInput);
			buffReader = new BufferedReader(reader);
			line = buffReader.readLine();
			while (line != null) {
			   try {
				if (!StringHelper.isEmpty(line)) {
				   echoMessages.add(PluginMessageBundle.getMessage("plugin.info.visitor.resource.processing", "interface", line));
				   pluginsSet.add(inspectPlugin(Class.forName(line.trim())));
				}
				line = buffReader.readLine();
			   } catch (final ClassNotFoundException cnfe) {
				throw new PluginRegistryException("plugin.error.load.classnotfound", cnfe, pluginMetaInfPath, line);
			   }
			}

		   } catch (final IOException ioe) {
			throw new PluginRegistryException("plugin.error.load.ioe", ioe, url.getFile() + "\n" + url.toString(), ioe.getMessage());
		   } finally {
			if (buffReader != null) {
			   buffReader.close();
			}
			if (reader != null) {
			   reader.close();
			}
			if (resourceInput != null) {
			   resourceInput.close();
			}
		   }
		}
	   }
	   return Collections.unmodifiableSet(pluginsSet);
	} catch (final IOException ioe) {
	   throw new PluginRegistryException("plugin.error.load.ioe", ioe, pluginMetaInfPath, ioe.getMessage());
	}
   }

   /**
    * load / registered plugin implementation meta data,<br/>
    * process start by reading text file, containing implementation class name list of annotated plugin impl<br/>
    * this file is generate at compile time, by plugin annotation processor {@link PluginAnnotationProcessor} <br/>
    * None coherence check is done at this step, use <code>checkDeclaration</code> to do so <br/>
    * 
    * @return unmodifiable set of plugin implementation found by annotation processing
    * @throws PluginRegistryException target cause can be:
    *            <ul>
    *            <li>IOException if problem reading declare plugin class file</li>
    *            <li>ClassNotFoundException if annotation plugin processor, provide/visit class name that is not present in classpath</li>
    *            </ul>
    */
   public Set<Plugin<?>> loadPluginImplementationMetaData() throws PluginRegistryException {

	try {
	   final Enumeration<URL> urls = JavaSystemHelper.getResources(pluginImplementationMetaInfPath);

	   pluginImplsSet.clear();

	   if (urls != null) {
		while (urls.hasMoreElements()) {
		   final URL url = urls.nextElement();
		   echoMessages.add(PluginMessageBundle.getMessage("plugin.info.visitor.resource.found", "classes", url.getPath()));

		   InputStream resourceInput = null;
		   Reader reader = null;
		   BufferedReader buffReader = null;
		   String line;

		   try {
			resourceInput = url.openStream();
			reader = new InputStreamReader(resourceInput);
			buffReader = new BufferedReader(reader);
			line = buffReader.readLine();
			while (line != null) {
			   try {
				pluginImplsSet.add(inspectPluginImpl(Class.forName(line.trim())));
				echoMessages.add(PluginMessageBundle.getMessage("plugin.info.visitor.resource.processing", "class", line));
				line = buffReader.readLine();
			   } catch (final ClassNotFoundException cnfe) {
				throw new PluginRegistryException("plugin.error.load.classnotfound", cnfe, pluginImplementationMetaInfPath, line);
			   } catch (final LinkageError ncfe) {
				if (LOGGER.isDebugEnabled()) {
				   echoMessages.add(PluginMessageBundle.getMessage("plugin.info.visitor.resource.linkageError", "class", line, ncfe.getMessage()));
				}
				// next plugin
				line = buffReader.readLine();
			   }
			}

		   } catch (final IOException ioe) {
			throw new PluginRegistryException("plugin.error.load.ioe", ioe, url.getFile(), ioe.getMessage());
		   } finally {
			if (buffReader != null) {
			   buffReader.close();
			}
			if (reader != null) {
			   reader.close();
			}
			if (resourceInput != null) {
			   resourceInput.close();
			}
		   }
		}
	   }
	   return Collections.unmodifiableSet(pluginImplsSet);
	} catch (final IOException ioe) {
	   throw new PluginRegistryException("plugin.error.load.ioe", ioe, pluginImplementationMetaInfPath, ioe.getMessage());
	}
   }

   /**
    * introspect class input parameter {@link Declare} annotation , and return bean meta data info
    * 
    * @param classToinspect
    * @return null if classToinspect not {@link Declare} annotated, or bean meta otherwise
    * @param <T>
    */
   public <T> Plugin<T> inspectPlugin(final Class<T> classToinspect) {

	final Declare declarePlugin = classToinspect.getAnnotation(Declare.class);
	if (declarePlugin != null) {
	   return PluginFactory.create(declarePlugin, classToinspect);
	} else {
	   return null;
	}
   }

   /**
    * introspect class input parameter {@link Plugin} annotation , and return bean meta data info
    * 
    * @param classToinspect
    * @return null if classToinspect not {@link Plugin} annotated, or bean meta otherwise
    * @param <T>
    */
   public <T> Plugin<T> inspectPluginImpl(final Class<T> classToinspect) {

	final Declare declarePluginImpl = classToinspect.getAnnotation(Declare.class);
	if (declarePluginImpl != null) {
	   return PluginFactory.create(declarePluginImpl, classToinspect);
	} else {
	   return null;
	}
   }

   /**
    * coherence check of loaded plugin and pluginImplementation
    * <ul>
    * <li>1. check that plugin annotated interface is right an interface and right {@link Declare} annotated
    * <li>2. check that plugin implementation is right an concrete class and right {@link Declare} annotated
    * <li>3. check the uniqueness name for {@link Declare}("name") plugin interface usage</li>
    * <li>4. check the uniqueness name for {@link Declare}("name") plugin implementation class usage</li>
    * <li>5. implementation class annotated by {@link Declare} have to implements one interface {@link Declare} annotated</li>
    * </ul>
    * 
    * @return set of errors (i18n exception instances, code + message)
    */
   public Set<PluginRegistryException> checkDeclarations() {

	final Set<PluginRegistryException> pluginRegistryExceptions = new LinkedHashSet<PluginRegistryException>();

	// 1 ***** plugin interface simple check ****************************************************
	for (final Plugin<?> plugin : pluginsSet) {
	   if (!plugin.isInterface()) {
		pluginRegistryExceptions.add(new PluginRegistryException("plugin.error.load.declare.notaninterface", pluginMetaInfPath, plugin.getAnnotatedClass()
			.getName()));
	   }
	}

	// 2 ***** plugin implementation simple check ****************************************************
	for (final Plugin<?> plugin : pluginImplsSet) {
	   if (!plugin.isClass()) {
		pluginRegistryExceptions.add(new PluginRegistryException("plugin.error.load.declare.notaconcreteclass", pluginImplementationMetaInfPath, plugin
			.getAnnotatedClass().getName()));
	   }
	}

	// 3 ***** check unique code @Declare(value="") is used for interface ******************************************
	final Map<String, List<Plugin<?>>> pluginCountByCode = new HashMap<String, List<Plugin<?>>>();

	// organize plugins in map<name, list<plugin>>
	for (final Plugin<?> plugin : pluginsSet) {
	   List<Plugin<?>> lplugins = pluginCountByCode.get(plugin.getName());
	   if (lplugins == null) {
		lplugins = new ArrayList<Plugin<?>>();
		pluginCountByCode.put(plugin.getName(), lplugins);
	   }
	   lplugins.add(plugin);
	}

	// if one values of the map<name, list<plugin>> has more than one element, conflict name
	final List<Plugin<?>> nonUniqueNamePlugin = new ArrayList<Plugin<?>>();
	for (final Entry<String, List<Plugin<?>>> entry : pluginCountByCode.entrySet()) {
	   if (entry.getValue().size() > 1) {
		nonUniqueNamePlugin.addAll(entry.getValue());
	   }
	}

	if (!nonUniqueNamePlugin.isEmpty()) {
	   for (final Plugin<?> p : nonUniqueNamePlugin) {
		pluginRegistryExceptions.add(new PluginRegistryException("plugin.error.load.declare.nonunique.name", p.getName(), p.getAnnotatedClass().getName()));
	   }
	}

	// 4 ***** check unique code @Declare(value="") is used for implementation class *******************************
	final Map<String, List<Plugin<?>>> pluginImplCountByCode = new HashMap<String, List<Plugin<?>>>();

	// organize plugins in map<name, list<plugin>>
	for (final Plugin<?> plugin : pluginImplsSet) {
	   List<Plugin<?>> lplugins = pluginImplCountByCode.get(plugin.getName());
	   if (lplugins == null) {
		lplugins = new ArrayList<Plugin<?>>();
		pluginImplCountByCode.put(plugin.getName(), lplugins);
	   }
	   lplugins.add(plugin);
	}

	// if one values of the map<name, list<plugin>> has more than one element, conflict name
	final List<Plugin<?>> nonUniqueNamePluginImpl = new ArrayList<Plugin<?>>();
	for (final Entry<String, List<Plugin<?>>> entry : pluginImplCountByCode.entrySet()) {
	   if (entry.getValue().size() > 1) {
		nonUniqueNamePluginImpl.addAll(entry.getValue());
	   }
	}

	if (!nonUniqueNamePluginImpl.isEmpty()) {
	   for (final Plugin<?> p : nonUniqueNamePluginImpl) {
		pluginRegistryExceptions.add(new PluginRegistryException("plugin.error.load.declare.nonunique.name", p.getName(), p.getAnnotatedClass().getName()));
	   }

	}

	// 5. ***** class annotated must implements one interface @Declare annotated
	// *****************************************
	for (final Plugin<?> plugin : pluginImplsSet) {

	   int pluginDeclareCount = 0;
	   final Set<Class<?>> pluginInterface = ReflectionHelper.getAllInterfaces(plugin.getAnnotatedClass());
	   for (final Class<?> c : pluginInterface) {
		if (c.getAnnotation(Declare.class) != null) {
		   pluginDeclareCount++;
		}
	   }

	   if (pluginDeclareCount == 0) {
		pluginRegistryExceptions.add(new PluginRegistryException("plugin.error.load.declare.nointerface", plugin.getAnnotatedClass().getName()));
	   }
	}

	return pluginRegistryExceptions;
   }

}
