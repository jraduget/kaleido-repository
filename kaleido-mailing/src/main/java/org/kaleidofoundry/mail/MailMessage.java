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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.kaleidofoundry.core.store.ResourceException;
import org.kaleidofoundry.core.store.ResourceHandler;

/**
 * <p>
 * A mail message representation with a subject, addresses, body, priority, attachments...
 * </p>
 * 
 * @author jraduget
 */
public interface MailMessage extends Serializable, Cloneable {

   /**
    * Add an attachment
    * 
    * @param attach
    * @return current instance
    */
   MailMessage attach(MailAttachment attach);

   /**
    * Add an attachment
    * 
    * @param name
    * @param attach
    * @return current instance
    * @throws ResourceException
    */
   MailMessage attach(String name, ResourceHandler attach) throws ResourceException;

   /**
    * Add an attachment to the message
    * 
    * @param attachName attachment name
    * @param fileURI attachment file {@link URI}
    * @throws FileNotFoundException
    * @throws IOException unknown mime type
    * @return current instance
    */
   MailMessage attach(String attachName, String fileURI) throws FileNotFoundException, IOException;

   /**
    * Add an attachment to the message
    * 
    * @param attachName
    * @param mimeType
    * @param attachIn
    * @return current instance
    */
   MailMessage attach(String attachName, String mimeType, InputStream attachIn);

   /**
    * @return Clone the current mail message into a new one
    */
   MailMessage clone();

   /**
    * @return get an attachment by its name
    * @param attachName
    */
   MailAttachment getAttachment(String attachName);

   /**
    * @return all mail attachment names
    */
   Set<String> getAttachmentNames();

   /**
    * @return CC addresses
    */
   List<String> getCcAddresses();

   /**
    * @return CCI addresses
    */
   List<String> getCciAddresses();

   /**
    * @return CharSet used to encode the body of the message
    */
   String getBodyCharSet();

   /**
    * @return mailboty
    */
   String getBody();

   /**
    * @return ContentType used in the body of the message
    */
   String getBodyContentType();

   /**
    * @return sender address
    */
   String getFromAddress();

   /**
    * @return Message priority <li>0 - Normal</li> <li>1 - High</li> <li>4 - Low</li>
    */
   int getPriority();

   /**
    * @return mail subject
    */
   String getSubject();

   /**
    * @return TO addresses
    */
   List<String> getToAddresses();

   /**
    * Define the body content
    * 
    * @param content
    * @return current instance
    */
   MailMessage withBody(String content);

   /**
    * Set the mail body as html content
    * 
    * @return current instance
    */
   MailMessage withBodyAsHtml();

   /**
    * Set the mail body as plain text
    * 
    * @return current instance
    */
   MailMessage withBodyAsText();

   /**
    * CharSet used to encode the text of the message
    * 
    * @param charset
    */
   MailMessage withBodyCharSet(String charset);

   /**
    * Define the from address
    * 
    * @param address
    * @return current instance
    */
   MailMessage withFromAddress(String address);

   /**
    * Define the message priority
    * 
    * @param priority
    * @return current instance
    */
   MailMessage withPriority(int priority);

   /**
    * Define the mail subject
    * 
    * @param subject
    * @return current instance
    */
   MailMessage withSubject(String subject);

}
