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
package org.kaleidofoundry.mail.sender.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * Home interface for the bean Mailer
 */
public interface EjbMailSenderHome extends EJBHome {

   /**
    * Création en utilisant le fournisseur par défaut
    * 
    * @throws CreateException if the bean creation failed.
    * @throws RemoteException if the call failed.
    * @return the "mailer" bean created
    *         Le section sessionMail utilisée sera "default"
    *         (voir mailSession.properties dans META-INF/
    */
   EjbMailSender create() throws CreateException, RemoteException;

   /**
    * Création en utilisant le fournisseur dont le nom est en argument
    * 
    * @param mailsessionName Nom de la section sessionMail à utiliser
    *           (voir mailSession.properties dans META-INF/
    * @throws CreateException if the bean creation failed.
    * @throws RemoteException if the call failed.
    * @return the "mailer" bean created
    */
   EjbMailSender create(String mailsessionName) throws CreateException, RemoteException;
}
