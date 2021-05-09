/*  
 * Copyright 2008-2021 the original author or authors 
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
package org.kaleidofoundry.mail;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.config.ConfigurationFactory;

/**
 * @author jraduget
 */
public class MailTestConstants {

    public final static String FROM_ADRESS = "kaleido-user@googlegroups.com";
    public final static String[] TO_ADRESS = new String[] { "kaleido-dev@googlegroups.com", "kaleido-user@googlegroups.com",
		"kaleido-int@googlegroups.com" };
    public final static String[] CC_ADRESS = new String[] { "kaleido-int@googlegroups.com", "kaleido-user@googlegroups.com" };
    public final static String[] BCC_ADRESS = new String[] { "kaleido-int@googlegroups.com" };

    public final static String MAIL_SUBJECT = "Kaleido integration test subject";
    public final static String MAIL_BODY_HTML = "<b>Kaleido integration test body</b><br/>Hello world!";
    public final static String MAIL_ENCODING = "UTF-8";
    public final static boolean MAIL_HTML = true;
    public final static int MAIL_PRIORITY = 3;

    public final static String INVALID_MAIL_ADDRESS_01 = "wrongmail.com";

    public static MailMessage createDefaultMailMessage() {

	  Configuration javaSysConfig = ConfigurationFactory.provides("javaSys", "memory:/dump.javasystem");

	  String from = javaSysConfig.getString("mailFrom", FROM_ADRESS);
	  String[] to = javaSysConfig.getStrings("mailTo", TO_ADRESS);
	  String[] cc = javaSysConfig.getStrings("mailCc", CC_ADRESS);
	  String[] cci = javaSysConfig.getStrings("mailCci", BCC_ADRESS);	  
	  String subject = javaSysConfig.getString("mailSubject", MAIL_SUBJECT);
	  String body = javaSysConfig.getString("mailBody", MAIL_BODY_HTML);
	  int priority = javaSysConfig.getInteger("mailPriority", MAIL_PRIORITY);
	  boolean html = javaSysConfig.getBoolean("mailAsHtml", MAIL_HTML);
	  String htmlBodyCharset = javaSysConfig.getString("mailEncoding", MAIL_ENCODING);

	  return new MailMessageBean().withFromAddress(from).withToAddresses(to).withCcAddresses(cc).withBccAddresses(cci)
		    .withSubject(subject).withBodyAs(html).withBody(body).withPriority(priority).withBodyCharSet(htmlBodyCharset);
    }
    
}
