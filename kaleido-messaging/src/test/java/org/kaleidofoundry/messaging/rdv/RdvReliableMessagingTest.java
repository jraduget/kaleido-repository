package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.MessagingConstantsTests.RDV_RELIABLE_CONFIG_PATH;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.kaleidofoundry.core.config.NamedConfiguration;
import org.kaleidofoundry.core.launcher.KaleidoJunit4ClassRunner;

/**
 * Tibco RDV reliable test for producer and consumer 
 * 
 * @author jraduget
 *
 */
@RunWith(KaleidoJunit4ClassRunner.class)
@NamedConfiguration(name = "rdvReliableConfiguration", uri = RDV_RELIABLE_CONFIG_PATH)
@Ignore
public class RdvReliableMessagingTest extends RdvCertifiedMessagingTest {

}
