/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.messaging.jms.ibm;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.messaging.Consumer;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.jms.JmsConsumer;

/**
 * @author jraduget
 */
@Declare(MessagingConstants.AMQ_CONSUMER_PLUGIN)
public class WebsphereMQConsumer extends JmsConsumer implements Consumer {

   /**
    * @param context
    */
   public WebsphereMQConsumer(RuntimeContext<Consumer> context) {
	super(context);
   }

}
