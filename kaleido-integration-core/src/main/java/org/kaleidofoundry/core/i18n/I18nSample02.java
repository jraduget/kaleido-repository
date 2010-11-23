/*
 * Copyright 2008-2010 the original author or authors
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
package org.kaleidofoundry.core.i18n;

import static org.kaleidofoundry.core.i18n.I18nContextBuilder.BaseName;
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.LocaleLanguage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple i18n usage</h3> Inject {@link I18nMessages} context and instance using {@link Context} annotation with parameters, and without
 * external configuration
 * </p>
 * <br/>
 * Message file : "classpath:/i18n/messages_en.properties" contains :
 * 
 * <pre>
 * label.hello=Hello world!
 * label.hello.who=Hello Mr {0}
 * label.hello.when=Hello Mr {0}, your last connection was the {1,date,yyyy-MM-dd}
 * label.hello.how=Hello Mr {0}, your last connection was the {1,date,yyyy-MM-dd} and you have win {2,number,#.##$}
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class I18nSample02 {

   @Context(value = "myBundleCtx2", parameters = { @Parameter(name = BaseName, value = "i18n/messages"), @Parameter(name = LocaleLanguage, value = "en") })
   private I18nMessages messages;

   public void echo() throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	System.out.printf("%s\n", messages.getMessage("label.hello"));
	System.out.printf("%s\n", messages.getMessage("label.hello.who"), "Smith");
	System.out.printf("%s\n", messages.getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	System.out.printf("%s\n", messages.getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 1234.56));
   }

   /**
    * used only for junit assertions
    * 
    * @return current messages instance
    */
   I18nMessages getMessages() {
	return messages;
   }

}
