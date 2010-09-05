/*
 *  Copyright 2008-2010 the original author or authors.
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
package org.kaleidofoundry.core.store;

import java.net.URLConnection;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Resource store base {@link RuntimeContext} builder & properties
 * <p>
 * <b>ResourceStore commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Property description</th>
 * </tr>
 * <tr>
 * <td>uriScheme</td>
 * <td>resource store uri scheme <code>http|https|ftp|file|classpath|webapp|...</code></td>
 * </tr>
 * <tr>
 * <td>readonly</td>
 * <td>resource store read-only usage <code>true|false</code></td>
 * </tr>
 * <tr>
 * <td>user</td>
 * <td>the connection user when resource store needs authentication</td>
 * </tr>
 * <tr>
 * <td>password</td>
 * <td>the connection password when resource store needs authentication</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * <b>Http / Ftp / ... - UrlConnection ResourceStore context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Property description</th>
 * </tr>
 * <tr>
 * <td>useCaches</td>
 * <td>Enable or not the cache use - see {@link URLConnection#setUseCaches(boolean)}</td>
 * </tr>
 * <tr>
 * <td>connectTimeout</td>
 * <td>connection timeout settings - see {@link URLConnection#setConnectTimeout(int) }</td>
 * </tr>
 * <tr>
 * <td>readTimeout</td>
 * <td>read timeout settings - - see {@link URLConnection#setReadTimeout(int)}</td>
 * </tr>
 * <tr>
 * <td>connectionRetryCount</td>
 * <td>retry count settings for establish the connection</td>
 * </tr>
 * <tr>
 * <td>readRetryCount</td>
 * <td>retry count settings for reading a resource</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * <b>ResourceStore common proxy context properties (for Http / Ftp / ... - UrlConnection)</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Property description</th>
 * </tr>
 * <tr>
 * <td>proxySet</td>
 * <td>does proxy is enabled - usage <code>true|false</code> value</td>
 * </tr>
 * <tr>
 * <td>proxyHost</td>
 * <td>proxy host - ignored is proxySet is set to <code>false</code></td>
 * </tr>
 * <tr>
 * <td>nonProxyHosts</td>
 * <td>non proxy hosts list, separators is comma - ignored is proxySet is set to <code>false</code></td>
 * </tr>
 * <tr>
 * <td>proxyPort</td>
 * <td>proxy port</td>
 * </tr>
 * <tr>
 * <td>proxyUser</td>
 * <td>proxy user</td>
 * </tr>
 * <tr>
 * <td>proxyPassword</td>
 * <td>proxy password</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author Jerome RADUGET
 */
public class ResourceContextBuilder extends AbstractRuntimeContextBuilder<ResourceStore> {

   // * commons settings property name ****************
   /** resource store uri scheme <code>http|https|ftp|file|classpath|webapp|...</code> */
   public static final String uriScheme = "uriScheme";

   /** resource store read-only usage <code>true|false</code> */
   public static final String readonly = "readonly";
   /** the connection user when resource store needs authentication */
   public static final String user = "user";
   /** the connection password when resource store needs authentication */
   public static final String password = "password";

   // * connection settings for ftp, http ... if needed ******************

   /** Enable or not the cache use - see {@link URLConnection#setUseCaches(boolean)} */
   public static final String useCaches = "useCaches";
   /** connection timeout settings - see {@link URLConnection#setConnectTimeout(int) } */
   public static final String connectTimeout = "connectTimeout";
   /** read timeout settings - - see {@link URLConnection#setReadTimeout(int)} */
   public static final String readTimeout = "readTimeout";

   /** retry count settings for establish the connection */
   public static final String connectionRetryCount = "connectionRetryCount";
   /** retry count settings for reading a resource */
   public static final String readRetryCount = "readRetryCount";

   // * proxy settings if needed ******************

   /** does proxy is enabled - usage <code>true|false</code> value */
   public static final String proxySet = "proxySet";
   /** proxy host - ignored is proxySet is set to <code>false</code> */
   public static final String proxyHost = "proxyHost";
   /** non proxy hosts list, separators is comma - ignored is proxySet is set to <code>false</code> */
   public static final String nonProxyHosts = "nonProxyHosts";
   /** proxy port */
   public static final String proxyPort = "proxyPort";
   /** proxy user */
   public static final String proxyUser = "proxyUser";
   /** proxy password */
   public static final String proxyPassword = "proxyPassword";

   /**
    * @param pluginInterface
    * @param configurations
    */
   public ResourceContextBuilder(final Class<ResourceStore> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    */
   public ResourceContextBuilder(final Class<ResourceStore> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param configurations
    */
   public ResourceContextBuilder(final Configuration... configurations) {
	super(configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public ResourceContextBuilder(final String name, final Class<ResourceStore> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public ResourceContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public ResourceContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public ResourceContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public ResourceContextBuilder(final String name) {
	super(name);
   }

   // ** builder methods **************************

   /**
    * @param uriScheme
    * @return current builder instance
    * @see ResourceContextBuilder#readonly
    */
   public ResourceContextBuilder withUriScheme(final String uriScheme) {
	getContextParameters().put(ResourceContextBuilder.uriScheme, uriScheme);
	return this;
   }

   /**
    * @param readonly
    * @return current builder instance
    * @see ResourceContextBuilder#readonly
    */
   public ResourceContextBuilder withReadonly(final boolean readonly) {
	getContextParameters().put(ResourceContextBuilder.readonly, String.valueOf(readonly));
	return this;
   }

   /**
    * @param username
    * @return current builder instance
    * @see ResourceContextBuilder#user
    */
   public ResourceContextBuilder withUser(final String username) {
	getContextParameters().put(ResourceContextBuilder.user, username);
	return this;
   }

   /**
    * @param password
    * @return current builder instance
    * @see ResourceContextBuilder#password
    */
   public ResourceContextBuilder withPassword(final String password) {
	getContextParameters().put(ResourceContextBuilder.password, password);
	return this;
   }

   /**
    * @param connectTimeout
    * @return current builder instance
    * @see ResourceContextBuilder#connectTimeout
    */
   public ResourceContextBuilder withConnectTimeout(final int connectTimeout) {
	getContextParameters().put(ResourceContextBuilder.connectTimeout, String.valueOf(connectTimeout));
	return this;
   }

   /**
    * @param readTimeout
    * @return current builder instance
    * @see ResourceContextBuilder#readTimeout
    */
   public ResourceContextBuilder withReadTimeout(final int readTimeout) {
	getContextParameters().put(ResourceContextBuilder.readTimeout, String.valueOf(readTimeout));
	return this;
   }

   /**
    * @param proxySet
    * @return current builder instance
    * @see ResourceContextBuilder#proxySet
    */
   public ResourceContextBuilder withProxySet(final boolean proxySet) {
	getContextParameters().put(ResourceContextBuilder.proxySet, String.valueOf(proxySet));
	return this;
   }

   /**
    * @param proxyHost
    * @return current builder instance
    */
   public ResourceContextBuilder withProxyHost(final String proxyHost) {
	getContextParameters().put(ResourceContextBuilder.proxyHost, proxyHost);
	return this;
   }

   /**
    * @param nonProxyHosts
    * @return current builder instance
    * @see ResourceContextBuilder#nonProxyHosts
    */
   public ResourceContextBuilder withNonProxyHosts(final String nonProxyHosts) {
	getContextParameters().put(ResourceContextBuilder.nonProxyHosts, nonProxyHosts);
	return this;
   }

   /**
    * @param proxyPort
    * @return current builder instance
    * @see ResourceContextBuilder#proxyPort
    */
   public ResourceContextBuilder withProxyPort(final int proxyPort) {
	getContextParameters().put(ResourceContextBuilder.proxyPort, String.valueOf(proxyPort));
	return this;
   }

   /**
    * @param proxyUser
    * @return current builder instance
    * @see ResourceContextBuilder#proxyUser
    */
   public ResourceContextBuilder withProxyUser(final String proxyUser) {
	getContextParameters().put(ResourceContextBuilder.proxyUser, proxyUser);
	return this;
   }

   /**
    * @param proxyPassword
    * @return current builder instance
    * @see ResourceContextBuilder#proxyPassword
    */
   public ResourceContextBuilder withProxyPassword(final String proxyPassword) {
	getContextParameters().put(ResourceContextBuilder.proxyPassword, proxyPassword);
	return this;
   }
}
