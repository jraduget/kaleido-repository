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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple i18n usage</h3> Inject {@link I18nMessages} context and instance using {@link Context} annotation without parameters, but
 * using external configuration
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
 * i18n.myBundleCtx.locale.lang=fr
 * i18n.myBundleCtx.locale.country=FR
 * </pre>
 * 
 * Message file : "classpath:/i18n/messages_fr.properties" contains :
 * 
 * <pre>
 * label.hello=Bonjour tout le monde!
 * label.hello.who=Bonjour M. {0}
 * label.hello.when=Bonjour M. {0}, votre derni�re connexion a �t� le {1,date,dd/MM/yyyy}
 * label.hello.how=Bonjour M. {0}, votre derni�re connexion a �t� le {1,date,dd/MM/yyyy} et vous avez gagn� {2,number,#.##euros}
 * </pre>
 * 
 * @author Jerome RADUGET
 */
public class I18nSample01 {

   @Context("myBundleCtx")
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
