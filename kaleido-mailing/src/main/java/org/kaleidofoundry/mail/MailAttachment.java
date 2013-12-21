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
package org.kaleidofoundry.mail;

import java.io.Serializable;
import java.net.URL;

/**
 * Pi�ce jointe d'un mail.
 * On peut sp�cifier un chemin syst�me
 * ou
 * une URL java pour localiser la pi�ce jointe
 * si il est sp�cifi�, le chemin syst�me est utilis� par d�faut
 * 
 * @author jraduget
 */
public interface MailAttachment extends Serializable {

   /** @return attachment name */
   String getName();

   /** @return attachment content type */
   String getContentType();

   /** @return attachment path */
   String getContentPath();

   /** @return attachment url */
   URL getContentURL();

 
}
