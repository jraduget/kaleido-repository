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

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation without
 * parameters, but using external configuration
 * </p>
 * <br/>
 * <b>
 * For configuration detail please see {@link RemoteDatasourceSample01} </b>
 * 
 * @author Jerome RADUGET
 */
public class RemoteJmsSample01 {

   @Context("myRemoteCtx")
   private NamingService namingService;

   /**
    * @return
    * @throws NamingServiceException
    */
   public QueueConnectionFactory getQueueConnectionFactory() {

	// get remote jms queue connection factory
	QueueConnectionFactory myQueueFactory = namingService.locate("jms/kaleidoQueueFactory", QueueConnectionFactory.class);

	return myQueueFactory;
   }
   
   /**
    * @param message
    * @throws NamingServiceException
    */
   public Queue getQueue() {

	// get remote jms queue
	Queue myQueue = namingService.locate("jms/kaleidoQueue", Queue.class);

	return myQueue;
   }

   /**
    * @return
    * @throws NamingServiceException
    */
   public TopicConnectionFactory getTopicConnectionFactory() {

	// get remote jms topic connection factory
	TopicConnectionFactory myTopicFactory = namingService.locate("jms/kaleidoQueueFactory", TopicConnectionFactory.class);

	return myTopicFactory;
   }
   
   /**
    * @return
    * @throws NamingServiceException
    */
   public Topic getTopic() {

	// get remote jms topic
	Topic myTopic = namingService.locate("jms/kaleidoTopic", Topic.class);

	return myTopic;
   }

}
