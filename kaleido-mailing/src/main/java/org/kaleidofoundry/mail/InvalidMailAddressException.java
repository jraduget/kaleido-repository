package org.kaleidofoundry.mail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Invalid mail address exception
 * 
 * @author jraduget
 */
public class InvalidMailAddressException extends MailException {

   private static final long serialVersionUID = -7901278074653762018L;

   private final List<String> invalidAddresses;

   public static InvalidMailAddressException emptyFromMailAddressException() {
	return new InvalidMailAddressException("mail.service.message.fromaddress.none");
   }

   public static InvalidMailAddressException emptyToMailAddressException() {
	return new InvalidMailAddressException("mail.service.message.address.none");
   }

   public static InvalidMailAddressException invalidMailAddressException(String... addresses) {
	return new InvalidMailAddressException("mail.service.message.address.invalid", addresses);
   }

   public InvalidMailAddressException(String code, String... parameters) {
	this(code, (Throwable) null, parameters);
   }

   @SuppressWarnings("unchecked")
   public InvalidMailAddressException(String code, Throwable cause, String... parameters) {
	super(code, cause, parameters);
	this.invalidAddresses = parameters != null ? Arrays.asList(parameters) : Collections.EMPTY_LIST;
   }

   public List<String> getInvalidAddresses() {
	return invalidAddresses;
   }

}
