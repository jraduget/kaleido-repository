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
package org.kaleidofoundry.core.env.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jr250241
 */
@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentVersions implements Serializable {

   private static final long serialVersionUID = 1L;

   private final String application;
   private final String runner;
   private final String kaleido;

   protected EnvironmentVersions() {
	this.application = null;
	this.runner = null;
	this.kaleido = null;
   }

   public EnvironmentVersions(String application, String runner, String kaleido) {
	super();
	this.application = application;
	this.runner = runner;
	this.kaleido = kaleido;
   }

   public String getApplication() {
	return application;
   }

   public String getRunner() {
	return runner;
   }

   public String getKaleido() {
	return kaleido;
   }

}
