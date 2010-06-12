package org.kaleidofoundry.mail;

import java.io.InputStream;
import java.net.URL;

/**
 * Impléméntation MailAttachment
 * 
 * @author Jerome RADUGET
 */
public class MailAttachmentBean implements MailAttachment {

   private static final long serialVersionUID = 6032673070918414344L;

   private String name;
   private String contentType;
   private InputStream content;
   private String contentPath;
   private URL contentURL;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContent()
    */
   public InputStream getContent() {
	return content;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentType()
    */
   public String getContentType() {
	return contentType;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getName()
    */
   public String getName() {
	return name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContent(java.io.InputStream)
    */
   public void setContent(final InputStream in) {
	this.content = in;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentType(java.lang.String)
    */
   public void setContentType(final String contentType) {
	this.contentType = contentType;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setName(java.lang.String)
    */
   public void setName(final String name) {
	this.name = name;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentPath()
    */
   public String getContentPath() {
	return contentPath;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentPath(java.lang.String)
    */
   public void setContentPath(final String contentPath) {
	this.contentPath = contentPath;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#getContentURL()
    */
   public URL getContentURL() {
	return contentURL;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.MailAttachment#setContentURL(java.net.URL)
    */
   public void setContentURL(final URL contentURL) {
	this.contentURL = contentURL;
   }

}
