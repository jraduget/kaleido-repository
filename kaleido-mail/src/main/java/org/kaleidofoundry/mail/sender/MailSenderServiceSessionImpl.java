package org.kaleidofoundry.mail.sender;

import static org.kaleidofoundry.mail.MailConstants.I18nRessource;

import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.mail.MailAttachment;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.session.MailSessionContext;
import org.kaleidofoundry.mail.session.MailSessionException;
import org.kaleidofoundry.mail.session.MailSessionFactory;
import org.kaleidofoundry.mail.session.MailSessionService;

/**
 * Implémentation MailSenderService
 * 
 * @author Jerome RADUGET
 */
class MailSenderServiceSessionImpl implements MailSenderService {

   private final MailSessionContext context;
   private final MailSessionService sessionService;

   MailSenderServiceSessionImpl(final MailSessionContext context) throws MailSessionException {
	this.context = context;
	this.sessionService = MailSessionFactory.createMailSessionService(getSessionContext());
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#getSessionContext()
    */
   public MailSessionContext getSessionContext() {
	return context;
   }

   /**
    * @return Accésseur au service qui donne un session
    */
   protected MailSessionService getMailSessionService() {
	if (sessionService == null) throw new IllegalStateException("sessionService is null");
	return sessionService;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#send(org.kaleidofoundry.mail.MailMessage)
    */
   public void send(final MailMessage message) throws MailSessionException, AddressException, MessagingException {

	final Session session = getMailSessionService().createSession(); // Session
	final MimeMessage mimeMessage = new MimeMessage(session); // Message
	MimeBodyPart messageBodyPart = new MimeBodyPart(); // Body
	final Multipart multipart = new MimeMultipart(); // Part du body

	Iterator<String> iterator = null;
	boolean hasIncorrectAddress = false;
	boolean hasAdress = false;

	// Si From / Emetteur, non renseigné, erreur
	if (message.getFromAdress() == null) {
	   final I18nMessages messages = I18nMessagesFactory.getMessages(I18nRessource);
	   throw new AddressException(messages.getMessage("mail.service.fromadress.none"));
	}

	// From / Emetteur
	try {
	   mimeMessage.setFrom(new InternetAddress(message.getFromAdress()));
	} catch (final AddressException ade) {
	   hasIncorrectAddress = true;
	   logger.error(ade.getMessage());
	}
	mimeMessage.setSubject(message.getSubject(), message.getCharSet()); // Sujet

	// Type et sous-type du corps du message
	messageBodyPart.setContent(message.getContent(), message.getContentType() + "; charset=" + message.getCharSet());

	// Priorité
	mimeMessage.addHeaderLine("X-Priority: " + String.valueOf(message.getPriority()));

	// Ajout de la première partie du message dans un objet Multipart
	multipart.addBodyPart(messageBodyPart);

	// To Adresses
	if (message.getToAdress() != null) {
	   for (iterator = message.getToAdress().iterator(); iterator.hasNext();) {
		try {
		   mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(iterator.next()));
		   hasAdress = true;
		} catch (final Throwable ae) {
		   hasIncorrectAddress = true;
		   logger.error(ae.getMessage());
		}
	   }
	}
	// CC Adresses
	if (message.getCcAdress() != null) {
	   for (iterator = message.getCcAdress().iterator(); iterator.hasNext();) {
		try {
		   mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(iterator.next()));
		   hasAdress = true;
		} catch (final Throwable ae) {
		   hasIncorrectAddress = true;
		   logger.error(ae.getMessage());
		}
	   }
	}
	// BCC Adresses
	if (message.getCciAdress() != null) {
	   for (iterator = message.getCciAdress().iterator(); iterator.hasNext();) {
		try {
		   mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(iterator.next()));
		   hasAdress = true;
		} catch (final Throwable ae) {
		   hasIncorrectAddress = true;
		   logger.error(ae.getMessage());
		}
	   }
	}

	// Fichiers joints
	if (message.getAttachments() != null) {

	   for (final String objName : message.getAttachments()) {

		final MailAttachment mailAttach = message.getAttachmentFilename(objName);
		final String filePath = mailAttach.getContentPath();
		final URL fileURL = mailAttach.getContentURL();

		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setFileName(objName);

		if (!StringHelper.isEmpty(filePath)) {
		   final DataSource source = new FileDataSource(filePath);
		   messageBodyPart.setDataHandler(new DataHandler(source));
		   multipart.addBodyPart(messageBodyPart);
		} else if (fileURL != null) {
		   messageBodyPart.setDataHandler(new DataHandler(fileURL));
		   multipart.addBodyPart(messageBodyPart);
		}
		/*
		 * else if (fileIn != null) {
		 * try {
		 * byte[] data = IOHelper.toByteArray(fileIn);
		 * ByteArrayInputStream inData = new ByteArrayInputStream(data);
		 * messageBodyPart.setContent(inData, mailAttach.getContentType());
		 * multipart.addBodyPart(messageBodyPart);
		 * } catch (IOException ioe) {
		 * throw new MessagingException(Messages.getMessage("mail.service.attach.content.io"), ioe);
		 * }
		 * }
		 */else {
		   final I18nMessages messages = I18nMessagesFactory.getMessages(I18nRessource);
		   throw new MessagingException(messages.getMessage("mail.service.attach.content.invalid"));
		}

	   }
	}

	// Ajout du contenu à l'enveloppe
	mimeMessage.setContent(multipart);

	// Envoie du message
	if (hasAdress == true) {
	   Transport.send(mimeMessage);
	} else {
	   final I18nMessages messages = I18nMessagesFactory.getMessages(I18nRessource);
	   throw new MessagingException(messages.getMessage("mail.service.adress.none"));
	}

	// Après envoie, si erreur d'adresse présente, on propage quand même l'exception
	if (hasIncorrectAddress) {
	   final I18nMessages messages = I18nMessagesFactory.getMessages(I18nRessource);
	   throw new AddressException(messages.getMessage("mail.service.adress.invalid"));
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#createMessage()
    */
   public MailMessage createMessage() {
	return MailSenderFactory.createMessage();
   }

}
