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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Jerome RADUGET
 */
@XmlType
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentInfo implements Serializable {

   private static final long serialVersionUID = 1L;

   public EnvironmentInfo() {
   }

   public EnvironmentInfo(String manifest, String systemInfo, String osInfo, String plugins) {
	super();
	this.manifest = manifest;
	this.systemInfo = systemInfo;
	this.osInfo = osInfo;
	this.plugins = plugins;
   }

   private String manifest;
   private String systemInfo;
   private String osInfo;
   private String plugins;

   public String getManifest() {
	return manifest;
   }

   public void setVersion(String manifest) {
	this.manifest = manifest;
   }

   public String getSystemInfo() {
	return systemInfo;
   }

   public void setSystemInfo(String systemInfo) {
	this.systemInfo = systemInfo;
   }

   public String getOsInfo() {
	return osInfo;
   }

   public void setOsInfo(String osInfo) {
	this.osInfo = osInfo;
   }

   public String getPlugins() {
	return plugins;
   }

   public void setPlugins(String plugins) {
	this.plugins = plugins;
   }

}
