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
package org.kaleidofoundry.core.env.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.kaleidofoundry.core.plugin.model.Plugin;

/**
 * @author jraduget
 */
@XmlRootElement(name = "environment")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentInfo implements Serializable {

   private static final long serialVersionUID = 1L;

   protected EnvironmentInfo() {
	this.versions = null;
	this.manifestInfos = null;
	this.runnerManifestInfos = null;
	this.plugins = null;
	this.pluginImplementations = null;
	this.systemInfos = null;
	this.osInfos = null;
   }

   public EnvironmentInfo(EnvironmentVersions versions, List<EnvironmentEntry> manifestInfos, List<EnvironmentEntry> runnerManifestInfos,
	   List<EnvironmentEntry> systemInfos, List<EnvironmentEntry> osInfos, List<Plugin<?>> plugins, List<Plugin<?>> pluginImpls) {
	super();
	this.versions = versions;
	this.manifestInfos = manifestInfos;
	this.runnerManifestInfos = runnerManifestInfos;
	this.plugins = plugins;
	this.pluginImplementations = pluginImpls;
	this.systemInfos = systemInfos;
	this.osInfos = osInfos;
   }

   private final EnvironmentVersions versions;

   @XmlElementWrapper(name = "applicationManifest")
   @XmlElement(name = "attribute")
   private final List<EnvironmentEntry> manifestInfos;

   @XmlElementWrapper(name = "runnerManifest")
   @XmlElement(name = "attribute")
   private final List<EnvironmentEntry> runnerManifestInfos;

   @XmlElementWrapper(name = "system")
   @XmlElement(name = "property")
   private final List<EnvironmentEntry> systemInfos;

   @XmlElementWrapper(name = "os")
   @XmlElement(name = "path")
   private final List<EnvironmentEntry> osInfos;

   @XmlElementWrapper(name = "plugin")
   @XmlElement(name = "plugin")
   private final List<Plugin<?>> plugins;

   @XmlElementWrapper(name = "pluginClasses")
   @XmlElement(name = "plugin")
   private final List<Plugin<?>> pluginImplementations;

   public EnvironmentVersions getVersions() {
	return versions;
   }

   public List<EnvironmentEntry> getManifestInfos() {
	return manifestInfos;
   }

   public List<EnvironmentEntry> getRunnerManifestInfos() {
	return runnerManifestInfos;
   }

   public List<EnvironmentEntry> getSystemInfos() {
	return systemInfos;
   }

   public List<EnvironmentEntry> getOsInfos() {
	return osInfos;
   }

   public List<Plugin<?>> getPlugins() {
	return plugins;
   }

   public List<Plugin<?>> getPluginImplementations() {
	return pluginImplementations;
   }

}
