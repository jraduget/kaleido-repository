package org.kaleidofoundry.mail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kaleidofoundry.core.util.ThrowableHelper;

/**
 * Report of a mailing send
 * <ul>
 * <li>invalid addresses</li>
 * </ul>
 * 
 * @author jraduget
 */
public class MailingDispatcherReport implements Serializable {

	private static final long serialVersionUID = -5533039526120133759L;

	private Map<MailMessage, List<String>> invalidAddresses;
	private Map<MailMessage, MailException> mailExceptions;

	public MailingDispatcherReport() {
		invalidAddresses = new HashMap<MailMessage, List<String>>();
		mailExceptions = new HashMap<MailMessage, MailException>();
	}

	public Map<MailMessage, List<String>> getInvalidAddresses() {
		return invalidAddresses;
	}

	public Map<MailMessage, MailException> getMailExceptions() {
		return mailExceptions;
	}

	public void put(MailMessage message, List<String> invalidAddresses) {
		this.invalidAddresses.put(message, invalidAddresses);
	}

	public void put(MailMessage message, MailException me) {
		this.mailExceptions.put(message, me);
		if (me instanceof InvalidMailAddressException && !"mail.service.message.fromaddress.none".equals(me.getCode())
				&& !"mail.service.message.address.none".equals(me.getCode())) {
			put(message, ((InvalidMailAddressException) me).getInvalidAddresses());
		}
	}

	public void put(MailMessage message, List<String> invalidAddresses, MailException me) {
		put(message, invalidAddresses);
		put(message, me);
	}

	public boolean isEmpty() {
		return invalidAddresses.isEmpty() && mailExceptions.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("MailingDispatcherReport [");
		str.append("invalidAddresses="+ invalidAddresses.values());
		str.append(", mailExceptions=");
		for (Entry<MailMessage, MailException> mre: mailExceptions.entrySet()) {
			str.append(ThrowableHelper.getStackTrace(mre.getValue())).append("\n");
		}
		str.append("]");
		
		return str.toString();
	}

}
