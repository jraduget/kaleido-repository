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
 * Resource store base {@link RuntimeContext} builder & properties. <br/>
 * <p>
 * <b>ResourceStore commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Perimeter</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>uriScheme</td>
 * <td>all</td>
 * <td>resource store uri scheme <code>http|https|ftp|file|classpath|webapp|...</code></td>
 * </tr>
 * <tr>
 * <td>readonly</td>
 * <td>all</td>
 * <td>resource store read-only usage <code>true|false</code></td>
 * </tr>
 * <tr>
 * <td>connectionRetryCount</td>
 * <td>all</td>
 * <td>retry count settings for establish the connection</td>
 * </tr>
 * <tr>
 * <td>readRetryCount</td>
 * <td>all</td>
 * <td>retry count settings for reading a resource</td>
 * </tr>
 * <tr>
 * <td>classloader</td>
 * <td>classpath</td>
 * <td>the class name, to get the class loader to use</td>
 * </tr>
 * <tr>
 * <td>customResourceStoreEntity</td>
 * <td>jpa</td>
 * <td>class name of a custom resource store entity (if you want to persist your own bean)</td>
 * </tr>
 * <tr>
 * <td>bufferSize</td>
 * <td>jpa</td>
 * <td>buffer size for reading input stream data</td>
 * </tr>
 * <tr>
 * <th>Property name</th>
 * <th>Perimeter</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>user</td>
 * <td>http|https|ftp</td>
 * <td>the connection user when resource store needs authentication</td>
 * </tr>
 * <tr>
 * <td>password</td>
 * <td>http|https|ftp</td>
 * <td>the connection password when resource store needs authentication</td>
 * </tr>
 * <tr>
 * <td>useCaches</td>
 * <td>http|https|ftp</td>
 * <td>Enable or not the cache use - see {@link URLConnection#setUseCaches(boolean)}</td>
 * </tr>
 * <tr>
 * <td>connectTimeout</td>
 * <td>http|https|ftp</td>
 * <td>connection timeout settings - see {@link URLConnection#setConnectTimeout(int) }</td>
 * </tr>
 * <tr>
 * <td>readTimeout</td>
 * <td>http|https|ftp</td>
 * <td>read timeout settings - - see {@link URLConnection#setReadTimeout(int)}</td>
 * </tr>
 * <tr>
 * <td>method</td>
 * <td>http|https</td>
 * <td>method to get content (POST / GET)</td>
 * </tr>
 * <tr>
 * <td>contentType</td>
 * <td>http|https</td>
 * <td>the http response mime type</td>
 * </tr>
 * <tr>
 * <th>Property name</th>
 * <th>Perimeter</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>proxySet</td>
 * <td>http|https|ftp</td>
 * <td>does proxy is enabled - usage <code>true|false</code> value</td>
 * </tr>
 * <tr>
 * <td>proxyHost</td>
 * <td>http|https|ftp</td>
 * <td>proxy host - ignored is proxySet is set to <code>false</code></td>
 * </tr>
 * <tr>
 * <td>nonProxyHosts</td>
 * <td>http|https|ftp</td>
 * <td>non proxy hosts list, separators is comma - ignored is proxySet is set to <code>false</code></td>
 * </tr>
 * <tr>
 * <td>proxyPort</td>
 * <td>http|https|ftp</td>
 * <td>proxy port</td>
 * </tr>
 * <tr>
 * <td>proxyUser</td>
 * <td>http|https|ftp</td>
 * <td>proxy user</td>
 * </tr>
 * <tr>
 * <td>proxyPassword</td>
 * <td>http|https|ftp</td>
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
   public static final String UriScheme = "uriScheme";

   /** resource store read-only usage <code>true|false</code> */
   public static final String Readonly = "readonly";
   /** retry count settings for establish the connection */
   public static final String ConnectionRetryCount = "connectionRetryCount";
   /** retry count settings for reading a resource */
   public static final String ReadRetryCount = "readRetryCount";
   /** property name for setting the class name, to get the class loader to use */
   public static final String Classloader = "classloader";

   // * jpa settings property name ****************
   /** class name of a custom resource store entity */
   public static final String CustomResourceStoreEntity = "customResourceStoreEntity";
   /** buffer size for reading input stream data */
   public static final String BufferSize = "bufferSize";

   // * connection settings for ftp, http ... if needed ******************

   /** the connection user when resource store needs authentication */
   public static final String User = "user";
   /** the connection password when resource store needs authentication */
   public static final String Password = "password";
   /** Enable or not the cache use - see {@link URLConnection#setUseCaches(boolean)} */
   public static final String UseCaches = "useCaches";
   /** connection timeout settings - see {@link URLConnection#setConnectTimeout(int) } */
   public static final String ConnectTimeout = "connectTimeout";
   /** read timeout settings - see {@link URLConnection#setReadTimeout(int)} */
   public static final String ReadTimeout = "readTimeout";

   /** Http method to get file content : GET, POST */
   public static final String Method = "method";
   /** Http response mime type */
   public static final String ContentType = "contentType";

   // * proxy settings if needed ******************
   /** does proxy is enabled - usage <code>true|false</code> value */
   public static final String ProxySet = "proxySet";
   /** proxy host - ignored is proxySet is set to <code>false</code> */
   public static final String ProxyHost = "proxyHost";
   /** non proxy hosts list, separators is comma - ignored is proxySet is set to <code>false</code> */
   public static final String NonProxyHosts = "nonProxyHosts";
   /** proxy port */
   public static final String ProxyPort = "proxyPort";
   /** proxy user */
   public static final String ProxyUser = "proxyUser";
   /** proxy password */
   public static final String ProxyPassword = "proxyPassword";

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
	getContextParameters().put(UriScheme, uriScheme);
	return this;
   }

   /**
    * @param readonly
    * @return current builder instance
    * @see ResourceContextBuilder#readonly
    */
   public ResourceContextBuilder withReadonly(final boolean readonly) {
	getContextParameters().put(Readonly, String.valueOf(readonly));
	return this;
   }

   /**
    * @param username
    * @return current builder instance
    * @see ResourceContextBuilder#user
    */
   public ResourceContextBuilder withUser(final String username) {
	getContextParameters().put(User, username);
	return this;
   }

   /**
    * @param password
    * @return current builder instance
    * @see ResourceContextBuilder#password
    */
   public ResourceContextBuilder withPassword(final String password) {
	getContextParameters().put(Password, password);
	return this;
   }

   /**
    * @param connectTimeout
    * @return current builder instance
    * @see ResourceContextBuilder#connectTimeout
    */
   public ResourceContextBuilder withConnectTimeout(final int connectTimeout) {
	getContextParameters().put(ConnectTimeout, String.valueOf(connectTimeout));
	return this;
   }

   /**
    * @param readTimeout
    * @return current builder instance
    * @see ResourceContextBuilder#readTimeout
    */
   public ResourceContextBuilder withReadTimeout(final int readTimeout) {
	getContextParameters().put(ReadTimeout, String.valueOf(readTimeout));
	return this;
   }

   /**
    * @param proxySet
    * @return current builder instance
    * @see ResourceContextBuilder#proxySet
    */
   public ResourceContextBuilder withProxySet(final boolean proxySet) {
	getContextParameters().put(ProxySet, String.valueOf(proxySet));
	return this;
   }

   /**
    * @param proxyHost
    * @return current builder instance
    */
   public ResourceContextBuilder withProxyHost(final String proxyHost) {
	getContextParameters().put(ProxyHost, proxyHost);
	return this;
   }

   /**
    * @param nonProxyHosts
    * @return current builder instance
    * @see ResourceContextBuilder#nonProxyHosts
    */
   public ResourceContextBuilder withNonProxyHosts(final String nonProxyHosts) {
	getContextParameters().put(NonProxyHosts, nonProxyHosts);
	return this;
   }

   /**
    * @param proxyPort
    * @return current builder instance
    * @see ResourceContextBuilder#proxyPort
    */
   public ResourceContextBuilder withProxyPort(final int proxyPort) {
	getContextParameters().put(ProxyPort, String.valueOf(proxyPort));
	return this;
   }

   /**
    * @param proxyUser
    * @return current builder instance
    * @see ResourceContextBuilder#proxyUser
    */
   public ResourceContextBuilder withProxyUser(final String proxyUser) {
	getContextParameters().put(ProxyUser, proxyUser);
	return this;
   }

   /**
    * @param proxyPassword
    * @return current builder instance
    * @see ResourceContextBuilder#proxyPassword
    */
   public ResourceContextBuilder withProxyPassword(final String proxyPassword) {
	getContextParameters().put(ProxyPassword, proxyPassword);
	return this;
   }

   /**
    * @param classloader
    * @return current builder instance
    * @see ResourceContextBuilder#classloader
    */
   public ResourceContextBuilder withClassloader(final String classloader) {
	getContextParameters().put(Classloader, classloader);
	return this;
   }

   /**
    * @param method
    * @return current builder instance
    * @see ResourceContextBuilder#method
    */
   public ResourceContextBuilder withMethod(final String method) {
	getContextParameters().put(Method, method);
	return this;
   }

   /**
    * @param contentType
    * @return current builder instance
    * @see ResourceContextBuilder#classloader
    */
   public ResourceContextBuilder withContentType(final String contentType) {
	getContextParameters().put(ContentType, contentType);
	return this;
   }

   /**
    * @param customResourceStoreEntity
    * @return current builder instance
    * @see ResourceContextBuilder#customResourceStoreEntity
    */
   public ResourceContextBuilder withCustomResourceStoreEntity(final String customResourceStoreEntity) {
	getContextParameters().put(CustomResourceStoreEntity, customResourceStoreEntity);
	return this;
   }

   /**
    * @param bufferSize
    * @return current builder instance
    * @see ResourceContextBuilder#bufferSize
    */
   public ResourceContextBuilder withBufferSize(final String bufferSize) {
	getContextParameters().put(BufferSize, bufferSize);
	return this;
   }

}
