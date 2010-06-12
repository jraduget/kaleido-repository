package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_CMname;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Daemon;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Network;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Service;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Timeout;
import static org.kaleidofoundry.messaging.rdv.RdvTransportConstants.PATH_KEY_Type;

import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractTransportMessaging;
import org.kaleidofoundry.messaging.TransportMessaging;
import org.kaleidofoundry.messaging.TransportMessagingContext;
import org.kaleidofoundry.messaging.TransportMessagingException;
import org.kaleidofoundry.messaging.TransportRegistry;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmQueueTransport;
import com.tibco.tibrv.TibrvCmTransport;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvTransport;

/**
 * TibcoRdv MessagingTransport
 * 
 * @author Jerome RADUGET
 */
public class RdvTransportMessaging extends AbstractTransportMessaging implements TransportMessaging {

   private TibrvRvdTransport transport;
   private TibrvCmTransport cmTransport;
   private TibrvCmQueueTransport cmQueueTransport;

   /**
    * Create and register transport
    * 
    * @param context
    * @throws TransportMessagingException
    */
   public RdvTransportMessaging(final TransportMessagingContext context) throws TransportMessagingException {
	super(context);

	checkTransportContext();

	createTransport();
   }

   /**
    * Check cohérence
    * 
    * @throws TransportMessagingException
    */
   protected void checkTransportContext() throws TransportMessagingException {
	if (StringHelper.isEmpty(getType())) { throw new TransportMessagingException("messaging.transport.rdv.type"); }

	if (StringHelper.isEmpty(getService())) { throw new TransportMessagingException("messaging.transport.rdv.service"); }

	if (StringHelper.isEmpty(getDaemon())) { throw new TransportMessagingException("messaging.transport.rdv.daemon"); }

   }

   /**
    * Instanciate du transport
    * 
    * @throws TransportMessagingException
    */
   protected void createTransport() throws TransportMessagingException {
	try {
	   // Native implémentation
	   Tibrv.open(Tibrv.IMPL_NATIVE);
	} catch (final TibrvException rvde) {
	   throw new TransportMessagingException("messaging.transport.rdv.open", rvde);
	}

	try {
	   // Create RVD transport
	   transport = new TibrvRvdTransport(getService(), getNetwork(), getDaemon());

	} catch (final TibrvException rvde) {
	   throw new TransportMessagingException("messaging.transport.rdv.create", rvde);
	}

	try {
	   // Create RVD certified transport if needed
	   if (RdvTransportTypeEnum.CERTIFIED.equals(getType())) {
		cmTransport = new TibrvCmTransport(transport, getCmname(), true);

		if (!StringHelper.isEmpty(getTimeout())) {
		   try {
			cmTransport.setDefaultTimeLimit(Double.parseDouble(getTimeout()));
		   } catch (final NumberFormatException nfe) {
			throw new TransportMessagingException("messaging.transport.rdv.timeout",
				new String[] { getTimeout() });
		   }
		}
	   }
	} catch (final TibrvException rvde) {
	   throw new TransportMessagingException("messaging.transport.rdv.create", rvde);
	}

	try {
	   // Create RVD Distributed queue transport if needed
	   if (RdvTransportTypeEnum.DQUEUE.equals(getType())) {
		cmQueueTransport = new TibrvCmQueueTransport(transport, getCmname());

		if (!StringHelper.isEmpty(getTimeout())) {
		   try {
			cmQueueTransport.setDefaultTimeLimit(Double.parseDouble(getTimeout()));
		   } catch (final NumberFormatException nfe) {
			throw new TransportMessagingException("messaging.transport.rdv.timeout",
				new String[] { getTimeout() });
		   }
		}

	   }
	} catch (final TibrvException rvde) {
	   throw new TransportMessagingException("messaging.transport.rdv.create", rvde);
	}
   }

   // getters & setter
   // **********************************************************************
   public void close() throws TransportMessagingException {

	if (transport != null) {
	   transport.destroy();
	}

	try {
	   Tibrv.close();
	} catch (final TibrvException trve) {
	   throw new TransportMessagingException("messaging.transport.rdv.close", trve);
	}
   }

   /**
    * @return RDV service config value
    */
   public String getService() {
	return getContext().getProperty(PATH_KEY_Service);
   }

   /**
    * @return RDV Network config value
    */
   public String getNetwork() {
	return getContext().getProperty(PATH_KEY_Network);
   }

   /**
    * @return RDV Daemon config value
    */
   public String getDaemon() {
	return getContext().getProperty(PATH_KEY_Daemon);
   }

   /**
    * @return RDV Timeout value for certified transport
    */
   public String getTimeout() {
	return getContext().getProperty(PATH_KEY_Timeout);
   }

   /**
    * @return RDV type transport
    * @see RdvTransportTypeEnum
    */
   public String getType() {
	return getContext().getProperty(PATH_KEY_Type);
   }

   /**
    * @return RDV type transport
    * @see RdvTransportTypeEnum
    */
   public String getCmname() {
	return getContext().getProperty(PATH_KEY_CMname);
   }

   /**
    * @return Transport RDV
    */
   public TibrvTransport getRdvTransport() {
	return transport;
   }

   /**
    * @return CmTransport RDV if certified
    * @IllegalStateException If transport is not certified
    */
   public TibrvCmTransport getRdvCmTransport() {
	if (RdvTransportTypeEnum.CERTIFIED.equals(getType())) {
	   return cmTransport;
	} else {
	   throw new IllegalStateException("use only for certified transport");
	}
   }

   /**
    * @return CmQueueTransport RDV if distributed queue
    * @IllegalStateException If transport is not distributed queue
    */
   public TibrvCmQueueTransport getCmQueueTransport() {
	if (RdvTransportTypeEnum.DQUEUE.equals(getType())) {
	   return cmQueueTransport;
	} else {
	   throw new IllegalStateException("use only for distributed queue transport");
	}
   }

   // static

   /**
    * @param context
    * @return Get Transport for registry or instanciate it, if needed
    */
   public static RdvTransportMessaging registeredTransport(final TransportMessagingContext context)
	   throws TransportMessagingException {
	RdvTransportMessaging transport = null;

	// transport instance (from registry if already instanciate)
	if (TransportRegistry.isRegistered(context.getName())) {
	   final TransportMessaging transportRegistered = TransportRegistry.getTransportMessaging(context.getName());
	   if (transportRegistered instanceof RdvTransportMessaging) {
		transport = (RdvTransportMessaging) transportRegistered;
	   } else {
		throw new TransportMessagingException("messaging.error.register.illegal", new String[] { context.getName(),
			transportRegistered.getClass().getName(), RdvTransportMessaging.class.getName() });
	   }
	} else {
	   transport = new RdvTransportMessaging(context);
	   TransportRegistry.register(context.getName(), transport);
	}
	return transport;
   }
}