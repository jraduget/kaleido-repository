package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvCertifiedConfigPath;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvListenerConfigKey;
import static org.kaleidofoundry.messaging.MessagingTestsConstants.RdvPublisherConfigKey;

/**
 * @author Jerome RADUGET
 */
public class TestRdvConsumerCertifiedMessaging extends AbstractRdvConsumerMessaging {

   public TestRdvConsumerCertifiedMessaging() {
	super(RdvCertifiedConfigPath, RdvListenerConfigKey, RdvPublisherConfigKey);
   }
}
