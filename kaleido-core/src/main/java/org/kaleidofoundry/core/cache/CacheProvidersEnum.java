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
package org.kaleidofoundry.core.cache;

/**
 * cache providers codes enumeration
 * 
 * @author Jerome RADUGET
 */
public enum CacheProvidersEnum {

   /* If you change an enum name, please apply changes to CacheConstants too... */
   ehCache,

   jbossCache3x,

   infinispan,

   coherence3x,

   gigaspace7x,

   websphere,

   local;
}