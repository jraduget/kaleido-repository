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
package org.kaleidofoundry.core.naming;

import java.sql.Connection;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.store.ResourceException;

/**
 * @author Jerome RADUGET
 */
public class RemoteJunitLauncher extends Assert {

   @Before
   public void setup() throws ResourceException {
	// load and register given configuration
	// another way to to this, set following java env variable : -Dkaleido.configurations=myConfig=classpath:/naming/myContext.properties
	ConfigurationFactory.provides("myConfig", "classpath:/naming/myContext.properties");
   }

   @After
   public void cleanupClass() throws ResourceException {
	I18nMessagesFactory.clearCache();
	ConfigurationFactory.destroy("myConfig");
   }

   @Test
   public void testDatasource01() throws SQLException {
	RemoteDatasourceSample01 remoteDatasourceSample = new RemoteDatasourceSample01();
	Connection connection = remoteDatasourceSample.getConnection();
	assertNotNull(connection);
	assertNotSame(connection, remoteDatasourceSample.getConnection());
   }

   @Test
   public void testJms01() throws JMSException {
	RemoteJmsSample01 remoteQueueSample = new RemoteJmsSample01();

	// get a jms queue connection factory
	QueueConnectionFactory queueConFactory = remoteQueueSample.getQueueConnectionFactory();
	assertNotNull(queueConFactory);
	assertSame(queueConFactory, queueConFactory);

	// get a jms queue
	Queue queue = remoteQueueSample.getQueue();
	assertNotNull(queue);
	assertSame(queue, queue);
	assertEquals("kaleidoQueue", queue.getQueueName());

	// get a topic queue
	Topic topic = remoteQueueSample.getTopic();
	assertNotNull(topic);
	assertSame(topic, topic);
	assertEquals("kaleido.topic.destination", topic.getTopicName());
   }
   
   @Test
   public void testEjb01() {
	RemoteEjbSample01 remoteEjbSample = new RemoteEjbSample01();
	assertEquals("hello world", remoteEjbSample.echo("hello world"));
	assertEquals("hello world2", remoteEjbSample.echo("hello world2"));
	assertEquals("hello world3", remoteEjbSample.echo("hello world3"));
	assertFalse("foo?".equals(remoteEjbSample.echo("foo")));
   }   
}
