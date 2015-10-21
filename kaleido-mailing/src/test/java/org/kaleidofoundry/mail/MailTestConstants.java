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

/**
 * @author jraduget
 */
public interface MailTestConstants {

   String FROM_ADRESS = "kaleido-user@googlegroups.com";
   String TO_ADRESS = "kaleido-dev@googlegroups.com";   
   String CC_ADRESS = "kaleido-dev@googlegroups.com";

   String MAIL_SUBJECT = "Kaleido integration test subject";
   String MAIL_BODY_HTML = "<b>Kaleido integration test body</b><br/>Hello world!";
   
   String INVALID_MAIL_ADDRESS_01 = "wrongmail.com";

}
