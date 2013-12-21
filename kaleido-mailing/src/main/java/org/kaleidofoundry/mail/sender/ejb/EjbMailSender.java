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
package org.kaleidofoundry.mail.sender.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;

/**
 * Remote interface
 */
public interface EjbMailSender extends EJBObject {

   /**
    * Envoi d'un Message
    * 
    * @param message
    * @throws MailException
    * @throws AddressException
    * @throws MessagingException
    * @throws RemoteException
    */
   void send(MailMessage message) throws MailException, AddressException, MessagingException, RemoteException;

}