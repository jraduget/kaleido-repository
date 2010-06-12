package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvListenerConfigKey;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvPublisherConfigKey;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvReliabledConfigPath;

/**
 * @author Jerome RADUGET
 */
public class TestRdvConsumerReliableMessaging extends AbstractRdvConsumerMessaging {

   public TestRdvConsumerReliableMessaging() {
	super(RdvReliabledConfigPath, RdvListenerConfigKey, RdvPublisherConfigKey);
   }
}
