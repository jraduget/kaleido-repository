package org.kaleidofoundry.mail.session;

import java.util.ArrayList;
import java.util.List;


/**
 * Enumération des Implementation de MailSession Disponible
 * 
 * @author Jerome RADUGET
 */
public class MailSessionEnum {

   private static List<MailSessionEnum> dsEnum = new ArrayList<MailSessionEnum>();

   /** Implementation Envoi Via accès local au service mail */
   public static MailSessionEnum LOCALE = new MailSessionEnum(MailSessionConstants.LocalMailSessionType, LocalMailSessionService.class);

   /** Implementation Envoi Via accès Jndi au service mail */
   public static MailSessionEnum JNDI = new MailSessionEnum(MailSessionConstants.JndiMailSessionType, JndiMailSessionService.class);

   MailSessionEnum(final String key, final Class<? extends MailSessionService> impl) {
	this.key = key;
	this.implementationClass = impl;
	dsEnum.add(this);
   }

   private final String key;
   private final Class<? extends MailSessionService> implementationClass;

   public String getKey() {
	return key;
   }

   public Class<? extends MailSessionService> getImplementationClass() {
	return implementationClass;
   }

   @Override
   public String toString() {
	return key;
   }

   public static List<MailSessionEnum> getEnums() {
	return dsEnum;
   }
}