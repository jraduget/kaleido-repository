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
package org.kaleidofoundry.core.store;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URI;

import org.kaleidofoundry.core.cache.Cache;
import org.kaleidofoundry.core.i18n.I18nMessages;

/**
 * Annotation used to help the uri path resolution of a resource . Use it on a field, an argument, ... <br/>
 * <br/>
 * The {@link #uri()} field value, specify a "template" uri of the external resource, like :
 * <ul>
 * <li>"${host}/runtime.properties"
 * <li>"http://${host}/runtime.properties"
 * <li>"ftp://${ftp.host}:${ftp.port}/myProp.xml"
 * <li>"classpath:/${file.properties}"
 * </ul>
 * <p>
 * You can use this template to specify the cache configuration file of a {@link Cache}, of a {@link ResourceStore}, of {@link I18nMessages}
 * , ... :
 * 
 * <pre>
 * // cache annotation usage
 * &#064;Context(name = &quot;cacheContext&quot;)
 * &#064;ResourcePath(uri = &quot;${host}/cache.properties&quot;)
 * private RuntimeContext&lt;Cache&gt; myCache;
 * 
 * // i18n annotation usage
 * &#064;Context(name = &quot;module.messages&quot;)
 * &#064;ResourcePath(uri = &quot;http:/${host}/i18n/module/messages&quot;)
 * private RuntimeContext&lt;I18nMessages&gt; messages;
 * 
 * 
 * </pre>
 * 
 * </p>
 * 
 * @author Jerome RADUGET
 */
@Documented
@Target( { FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ContextResource {

   /**
    * @return {@link URI} template of an the resource (configuration file, ...), it will be used by the underlying context injection<br/>
    */
   String uri();

   /**
    * @return Configuration identifiers. It is used to restrict the resolution of the merge properties in the uri.<br/>
    *         it is optional, and if empty string : then merge properties name will be search in all registered configuration<br/>
    */
   String[] configurationIds() default "";

}
