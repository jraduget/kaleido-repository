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
import static org.kaleidofoundry.core.i18n.I18nContextBuilder.CacheManagerRef;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kaleidofoundry.core.context.Context;
import org.kaleidofoundry.core.context.Parameter;

/**
 * <p>
 * <h3>Simple i18n usage</h3> Inject {@link I18nMessages} context and instance using {@link Context} annotation mixing the
 * use of parameters and external configuration (Parameters have priority to the external configuration)
 * </p>
 * <br/>
 * <b>Precondition :</b> The following java env. variable have been set
 * 
 * <pre>
 * -Dkaleido.configurations=classpath:/i18n/myContext.properties
 * </pre>
 * 
 * Resource file : "classpath:/i18n/myContext.properties" contains :
 * 
 * <pre>
 * i18n.myBundleCtx.baseName=i18n/messages
 * i18n.myBundleCtx.locale.lang=en
 * i18n.myBundleCtx.locale.country=GB
 * #i18n.myBundleCtx.cacheManagerRef=myCacheManager
 * 
 * cacheManager.myCacheManager.providerCode=ehCache1x
 * cacheManager.myCacheManager.resourceUri=classpath:/i18n/ehcache.xml
 * </pre>
 * 
 * Message file : "classpath:/i18n/messages_fr.properties" contains :
 * 
 * <pre>
 * label.hello=Hello world!
 * label.hello.who=Hello Mr {0}
 * label.hello.when=Hello Mr {0}, your last connection was the {1,date,yyyy-MM-dd}
 * label.hello.how=Hello Mr {0}, your last connection was the {1,date,yyyy-MM-dd} and you have win {2,number,#.##£}
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class I18nSample03 {

   @Context(value="myBundleCtx", parameters = {
	   @Parameter(name = BaseName, value = "i18n/messages"),
	   @Parameter(name = CacheManagerRef, value = "myCacheManager")
   })
   private I18nMessages messages;

   /**
    * Stdout :
    * 
    * <pre>
    * Hello world!
    * Hello Mr Smith
    * Hello Mr Smith, your last connection was the 2010-10-21
    * Hello Mr Smith, your last connection was the 2010-10-21 and you have win 1234.56£
    * </pre>
    * 
    * @throws ParseException
    */
   public void echo() throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	System.out.printf("%s\n", messages.getMessage("label.hello"));
	System.out.printf("%s\n", messages.getMessage("label.hello.who", "Smith"));
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
