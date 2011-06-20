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

import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * <p>
 * <h3>Simple i18n usage</h3> Build {@link I18nMessages} context and instance manually by coding, using context builder
 * </p>
 * <br/>
 */
public class I18nSample04 {

   private final I18nMessages myBundle;

   public I18nSample04() {

	RuntimeContext<I18nMessages> context = new I18nContextBuilder("myI18nBundle", I18nMessages.class).withBaseName("i18n/messages").withLocaleLanguage("fr")
		.build();

	myBundle = I18nMessagesFactory.provides(context);
   }

   /**
    * Stdout :
    * 
    * <pre>
    * Bonjour tout le monde!
    * Bonjour M. Smith
    * Bonjour M. Smith, votre dernière connexion a été le 21/10/2010
    * Bonjour M. Smith, votre dernière connexion a été le 21/10/2010 et vous avez gagné 1234,56euros
    * </pre>
    * 
    * @throws ParseException
    */
   public void echo() throws ParseException {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	System.out.printf("%s\n", myBundle.getMessage("label.hello"));
	System.out.printf("%s\n", myBundle.getMessage("label.hello.who", "Smith"));
	System.out.printf("%s\n", myBundle.getMessage("label.hello.when", "Smith", df.parse("2010/10/21")));
	System.out.printf("%s\n", myBundle.getMessage("label.hello.how", "Smith", df.parse("2010/10/21"), 1234.56));
   }

   /**
    * used only for junit assertions
    * 
    * @return current messages instance
    */
   I18nMessages getMessages() {
	return myBundle;
   }
}
