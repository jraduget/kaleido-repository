package org.kaleidofoundry.mail.sender;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumération des Implementation de MailSession Disponible
 * 
 * @author Jerome RADUGET
 */
public class MailSenderEnum {

   private static List<MailSenderEnum> dsEnum = new ArrayList<MailSenderEnum>();

   /** Implementation Envoi Via accès JMS */
   public static MailSenderEnum JMS = new MailSenderEnum(MailSenderConstants.JmsMailServiceType, MailSenderServiceJmsImpl.class, false);

   /** Implementation Envoi Via accès EJB */
   public static MailSenderEnum EJB = new MailSenderEnum(MailSenderConstants.EjbMailServiceType, MailSenderServiceEjbImpl.class, false);

   /** Implementation Envoi Via accès mail Session */
   public static MailSenderEnum SESSION = new MailSenderEnum(MailSenderConstants.SessionMailServiceType, MailSenderServiceSessionImpl.class, true);

   MailSenderEnum(final String key, final Class<? extends MailSenderService> impl, final boolean needMailSession) {
	this.key = key;
	this.implementationClass = impl;
	this.needMailSession = needMailSession;
	dsEnum.add(this);
   }

   private final String key;
   private final Class<? extends MailSenderService> implementationClass;
   private final boolean needMailSession;

   public String getKey() {
	return key;
   }

   public Class<? extends MailSenderService> getImplementationClass() {
	return implementationClass;
   }

   public boolean isNeedMailSession() {
	return needMailSession;
   }

   @Override
   public String toString() {
	return key;
   }

   public static List<MailSenderEnum> getEnums() {
	return dsEnum;
   }
}