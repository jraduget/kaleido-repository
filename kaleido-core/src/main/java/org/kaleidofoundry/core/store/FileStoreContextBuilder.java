/*
 *  Copyright 2008-2014 the original author or authors.
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

import java.io.Serializable;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentMap;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.AbstractRuntimeContextBuilder;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * File store base {@link RuntimeContext} builder & properties. <br/>
 * <p>
 * <b>FileStore commons context properties</b> : <br/>
 * <table border="1">
 * <tr>
 * <th>Property name</th>
 * <th>Perimeter</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>baseUri</td>
 * <td>all</td>
 * <td>file store uri root path, looks like (path is optional) :
 * <ul>
 * <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
 * <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
 * <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
 * <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
 * <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
 * <li><code>memory:/</code> <b>or</b> <code>memory:/path</code></li>
 * <li><code>gs:/</code> <b>or</b> <code>gs:/bucketName/path</code></li>
 * <li><code>...</code></li>
 * </ul>
 * <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code></td>
 * </tr>
 * <tr>
 * <td>readonly</td>
 * <td>all</td>
 * <td>file store read-only usage <code>true|false</code></td>
 * </tr>
 * <tr>
 * <td>maxRetryOnFailure</td>
 * <td>all</td>
 * <td>it defines the maximum attempt count, when failure occurred for get / store / remove / move a resource (it is disabled if not
 * defined)</td>
 * </tr>
 * <tr>
 * <td>sleepTimeBeforeRetryOnFailure</td>
 * <td>all</td>
 * <td>it defines the time to sleep in ms before a new attempt, when failure occurred for get / store / remove / move a resource (it is
 * disabled if not defined)</td>
 * </tr>
 * <tr>
 * <td>bufferSize</td>
 * <td>all</td>
 * <td>buffer size for writing (store) in output stream data</td>
 * </tr>
 * <tr>
 * <td>charset</td>
 * <td>all</td>
 * <td>charset to use with a text that we want to read or store</td>
 * </tr>
 * <tr>
 * <td>caching</td>
 * <td>all</td>
 * <td>true|false it controls the caching of the store resources</td>
 * </tr>
 * <tr>
 * <td>cacheManagerRef</td>
 * <td>all</td>
 * <td>the name of the custom cacheManager to use if you want to cache resources</td>
 * </tr>
 * <tr>
 * <td>classloader</td>
 * <td>classpath</td>
 * <td>the class name, to get the class loader to use</td>
 * </tr>
 * <tr>
 * <td>customResourceHandlerEntity</td>
 * <td>jpa</td>
 * <td>class name of a custom file handler entity (if you want to persist your own bean) - default one is ResourceHandlerEntity</td>
 * </tr>
 * <tr>
 * <th>Property name</th>
 * <th>Perimeter</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>user</td>
 * <td>http|https|ftp</td>
 * <td>the connection user when file store needs authentication</td>
 * </tr>
 * <tr>
 * <td>password</td>
 * <td>http|https|ftp</td>
 * <td>the connection password when file store needs authentication</td>
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
 * @author jraduget
 */
public class FileStoreContextBuilder extends AbstractRuntimeContextBuilder<FileStore> {

   // * commons settings property name ****************
   /**
    * file store root path uri, which looks like (path is optional) :
    * <ul>
    * <li><code>http://host/</code> <b>or</b> <code>http://host/path</code></li>
    * <li><code>ftp://host/</code> <b>or</b> <code>ftp://host/path</code></li>
    * <li><code>classpath:/</code> <b>or</b> <code>classpath:/path</code></li>
    * <li><code>file:/</code> <b>or</b> <code>file:/path</code></li>
    * <li><code>webapp:/</code> <b>or</b> <code>webapp:/path</code></li>
    * <li><code>gs:/</code> <b>or</b> <code>gs:/bucketName/path</code></li>
    * <li><code>...</code></li>
    * </ul>
    * <b>uri schemes handled</b>: <code>http|https|ftp|file|classpath|webapp|...</code>
    */
   public static final String BaseUri = "baseUri";

   /** file store read-only usage <code>true|false</code> */
   public static final String Readonly = "readonly";
   /**
    * it defines the maximum attempt count, when failure occurred for get / store / remove / move a resource (it is disabled if not defined)
    */
   public static final String MaxRetryOnFailure = "maxRetryOnFailure";
   /**
    * it defines the time to sleep in ms before a new attempt, when failure occurred for get / store / remove / move a resource (it is
    * disabled if not defined)
    */
   public static final String SleepTimeBeforeRetryOnFailure = "sleepTimeBeforeRetryOnFailure";
   /** buffer size for reading input stream data */
   public static final String BufferSize = "bufferSize";
   /** the default charset to use for reading a resource as text */
   public static final String Charset = "charset";
   /** property name for setting the class name, to get the class loader to use */
   public static final String Classloader = "classloader";
   /** enable the caching of resources */
   public static final String Caching = "caching";
   /** if caching is enable - this property can be used to set the cache manager to use */
   public static final String CacheManagerRef = "cacheManagerRef";


   // * jpa settings property name ****************
   /** class name of a custom file handler entity used in jpa store */
   public static final String CustomResourceHandlerEntity = "customResourceHandlerEntity";

   // * connection settings for ftp, http ... if needed ******************
   /** the connection user when file store needs authentication */
   public static final String User = "user";
   /** the connection password when file store needs authentication */
   public static final String Password = "password";
   /** Enable or not the cache use - see {@link URLConnection#setUseCaches(boolean)} */
   public static final String UseCaches = "useCaches";
   /** connection timeout settings - see {@link URLConnection#setConnectTimeout(int) } */
   public static final String ConnectTimeout = "connectTimeout";
   /** read timeout settings - see {@link URLConnection#setReadTimeout(int)} */
   public static final String ReadTimeout = "readTimeout";

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
    * 
    */
   public FileStoreContextBuilder() {
	super();
   }

   /**
    * @param pluginInterface
    * @param configurations
    */
   public FileStoreContextBuilder(final Class<FileStore> pluginInterface, final Configuration... configurations) {
	super(pluginInterface, configurations);
   }

   /**
    * @param pluginInterface
    * @param staticParameters
    */
   public FileStoreContextBuilder(final Class<FileStore> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters) {
	super(pluginInterface, staticParameters);
   }

   /**
    * @param pluginInterface
    */
   public FileStoreContextBuilder(final Class<FileStore> pluginInterface) {
	super(pluginInterface);
   }

   /**
    * @param staticParameters
    * @param configurations
    */
   public FileStoreContextBuilder(final ConcurrentMap<String, Serializable> staticParameters, final Configuration... configurations) {
	super(staticParameters, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param configurations
    */
   public FileStoreContextBuilder(final String name, final Class<FileStore> pluginInterface, final Configuration... configurations) {
	super(name, pluginInterface, configurations);
   }

   /**
    * @param name
    * @param pluginInterface
    * @param staticParameters
    * @param configurations
    */
   public FileStoreContextBuilder(final String name, final Class<FileStore> pluginInterface, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, pluginInterface, staticParameters, configurations);
   }

   /**
    * @param name
    * @param configurations
    */
   public FileStoreContextBuilder(final String name, final Configuration... configurations) {
	super(name, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param configurations
    */
   public FileStoreContextBuilder(final String name, final String prefixProperty, final Configuration... configurations) {
	super(name, prefixProperty, configurations);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param staticParameters
    * @param configurations
    */
   public FileStoreContextBuilder(final String name, final String prefixProperty, final ConcurrentMap<String, Serializable> staticParameters,
	   final Configuration... configurations) {
	super(name, prefixProperty, staticParameters, configurations);
   }

   /**
    * @param name
    * @param prefix
    */
   public FileStoreContextBuilder(final String name, final String prefix) {
	super(name, prefix);
   }

   /**
    * @param name
    */
   public FileStoreContextBuilder(final String name) {
	super(name);
   }

   // ** builder methods **************************

   /**
    * @param parameter parameter name
    * @param value parameter value
    * @return current builder instance
    */
   public FileStoreContextBuilder withParameter(final String parameter, final Serializable value) {
	getContextParameters().put(parameter, value);
	return this;
   }

   /**
    * @param baseUri
    * @return current builder instance
    * @see FileStoreContextBuilder#BaseUri
    */
   public FileStoreContextBuilder withBaseUri(final String baseUri) {
	getContextParameters().put(BaseUri, baseUri);
	return this;
   }

   /**
    * @param readonly
    * @return current builder instance
    * @see FileStoreContextBuilder#Readonly
    */
   public FileStoreContextBuilder withReadonly(final boolean readonly) {
	getContextParameters().put(Readonly, String.valueOf(readonly));
	return this;
   }

   /**
    * @param maxRetryOnFailure
    * @return current builder instance
    * @see FileStoreContextBuilder#MaxRetryOnFailure
    */
   public FileStoreContextBuilder withMaxRetryOnFailure(final int maxRetryOnFailure) {
	getContextParameters().put(MaxRetryOnFailure, String.valueOf(maxRetryOnFailure));
	return this;
   }

   /**
    * @param sleepTimeBeforeRetryOnFailure
    * @return current builder instance
    * @see FileStoreContextBuilder#SleepTimeBeforeRetryOnFailure
    */
   public FileStoreContextBuilder withSleepTimeBeforeRetryOnFailure(final int sleepTimeBeforeRetryOnFailure) {
	getContextParameters().put(SleepTimeBeforeRetryOnFailure, String.valueOf(sleepTimeBeforeRetryOnFailure));
	return this;
   }

   /**
    * @param username
    * @return current builder instance
    * @see FileStoreContextBuilder#User
    */
   public FileStoreContextBuilder withUser(final String username) {
	getContextParameters().put(User, username);
	return this;
   }

   /**
    * @param password
    * @return current builder instance
    * @see FileStoreContextBuilder#Password
    */
   public FileStoreContextBuilder withPassword(final String password) {
	getContextParameters().put(Password, password);
	return this;
   }

   /**
    * @param connectTimeout
    * @return current builder instance
    * @see FileStoreContextBuilder#ConnectTimeout
    */
   public FileStoreContextBuilder withConnectTimeout(final int connectTimeout) {
	getContextParameters().put(ConnectTimeout, String.valueOf(connectTimeout));
	return this;
   }

   /**
    * @param readTimeout
    * @return current builder instance
    * @see FileStoreContextBuilder#ReadTimeout
    */
   public FileStoreContextBuilder withReadTimeout(final int readTimeout) {
	getContextParameters().put(ReadTimeout, String.valueOf(readTimeout));
	return this;
   }

   /**
    * @param proxySet
    * @return current builder instance
    * @see FileStoreContextBuilder#ProxySet
    */
   public FileStoreContextBuilder withProxySet(final boolean proxySet) {
	getContextParameters().put(ProxySet, String.valueOf(proxySet));
	return this;
   }

   /**
    * @param proxyHost
    * @return current builder instance
    */
   public FileStoreContextBuilder withProxyHost(final String proxyHost) {
	getContextParameters().put(ProxyHost, proxyHost);
	return this;
   }

   /**
    * @param nonProxyHosts
    * @return current builder instance
    * @see FileStoreContextBuilder#NonProxyHosts
    */
   public FileStoreContextBuilder withNonProxyHosts(final String nonProxyHosts) {
	getContextParameters().put(NonProxyHosts, nonProxyHosts);
	return this;
   }

   /**
    * @param proxyPort
    * @return current builder instance
    * @see FileStoreContextBuilder#ProxyPort
    */
   public FileStoreContextBuilder withProxyPort(final int proxyPort) {
	getContextParameters().put(ProxyPort, String.valueOf(proxyPort));
	return this;
   }

   /**
    * @param proxyUser
    * @return current builder instance
    * @see FileStoreContextBuilder#ProxyUser
    */
   public FileStoreContextBuilder withProxyUser(final String proxyUser) {
	getContextParameters().put(ProxyUser, proxyUser);
	return this;
   }

   /**
    * @param proxyPassword
    * @return current builder instance
    * @see FileStoreContextBuilder#ProxyPassword
    */
   public FileStoreContextBuilder withProxyPassword(final String proxyPassword) {
	getContextParameters().put(ProxyPassword, proxyPassword);
	return this;
   }

   /**
    * @param classloader
    * @return current builder instance
    * @see FileStoreContextBuilder#Classloader
    */
   public FileStoreContextBuilder withClassloader(final String classloader) {
	getContextParameters().put(Classloader, classloader);
	return this;
   }

   /**
    * @param caching
    * @return set caching context parameter
    */
   public FileStoreContextBuilder withCaching(final String caching) {
	getContextParameters().put(Caching, caching);
	return this;
   }

   /**
    * @param cacheManagerRef
    * @return set cacheManagerRef context parameter
    */
   public FileStoreContextBuilder withCacheManagerRef(final String cacheManagerRef) {
	getContextParameters().put(CacheManagerRef, cacheManagerRef);
	return this;
   }

   /**
    * @param customResourceHandlerEntity
    * @return current builder instance
    * @see FileStoreContextBuilder#CustomResourceHandlerEntity
    */
   public FileStoreContextBuilder withCustomResourceHandlerEntity(final String customResourceHandlerEntity) {
	getContextParameters().put(CustomResourceHandlerEntity, customResourceHandlerEntity);
	return this;
   }

   /**
    * @param bufferSize
    * @return current builder instance
    * @see FileStoreContextBuilder#BufferSize
    */
   public FileStoreContextBuilder withBufferSize(final String bufferSize) {
	getContextParameters().put(BufferSize, bufferSize);
	return this;
   }

   /**
    * @param charset
    * @return current builder instance
    * @see FileStoreContextBuilder#Charset
    */
   public FileStoreContextBuilder withCharset(final String charset) {
	getContextParameters().put(Charset, charset);
	return this;
   }

}
