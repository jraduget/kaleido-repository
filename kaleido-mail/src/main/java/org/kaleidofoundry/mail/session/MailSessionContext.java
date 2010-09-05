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
package org.kaleidofoundry.mail.session;

import static org.kaleidofoundry.mail.session.MailSessionConstants.LocalRootProperty;
import static org.kaleidofoundry.mail.session.MailSessionConstants.TypeProperty;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Ancêtre MailSessionContext utilisé pour charger une Session Mail
 * 
 * @author Jerome RADUGET
 */
public class MailSessionContext extends RuntimeContext<MailSessionService> {

   private static final long serialVersionUID = -8772474592608596423L;

   /**
	 * 
	 */
   public MailSessionContext() {
	super((String) null, LocalRootProperty);
   }

   /**
    * @param config
    */
   public MailSessionContext(final Configuration... config) {
	super(null, LocalRootProperty, config);
   }

   /**
    * @param name
    * @param prefixProperty
    * @param defaults
    */
   public MailSessionContext(final String name, final String prefixProperty, final Configuration... defaults) {
	super(name, prefixProperty, defaults);
   }

   /**
    * @param name
    * @param defaults
    */
   public MailSessionContext(final String name, final Configuration... defaults) {
	super(name, LocalRootProperty, defaults);
   }

   /**
    * @param name
    */
   public MailSessionContext(final String name) {
	super(name, LocalRootProperty);
   }

   /**
    * @return Type d'implementation (clé)
    */
   public String getType() {
	return getProperty(TypeProperty);
   }

}
