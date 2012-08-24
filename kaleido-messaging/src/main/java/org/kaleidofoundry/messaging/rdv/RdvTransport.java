/*  
 * Copyright 2008-2012 the original author or authors 
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
package org.kaleidofoundry.messaging.rdv;

import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_CERTIFIED_CMNAME;
import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_DAEMON_PARAMETER;
import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_NETWORK_PARAMETER;
import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_SERVICE_PARAMETER;
import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_CERTIFIED_TIMEOUT;
import static org.kaleidofoundry.messaging.rdv.RdvMessaginsConstants.RDV_TRANSPORT_TYPE;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.util.StringHelper;
import org.kaleidofoundry.messaging.AbstractTransport;
import org.kaleidofoundry.messaging.MessagingConstants;
import org.kaleidofoundry.messaging.Transport;
import org.kaleidofoundry.messaging.TransportException;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmQueueTransport;
import com.tibco.tibrv.TibrvCmTransport;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvTransport;

/**
 * Tibco RDV MessagingTransport
 * 
 * @author Jerome RADUGET
 */
@Declare(MessagingConstants.RDV_TRANSPORT_PLUGIN)
public class RdvTransport extends AbstractTransport implements Transport {

   private TibrvRvdTransport transport;
   private TibrvCmTransport cmTransport;
   private TibrvCmQueueTransport cmQueueTransport;

   /**
    * Create transport
    * 
    * @param context
    * @throws TransportException
    */
   public RdvTransport(final RuntimeContext<Transport> context) throws TransportException {
	super(context);

	checkTransportContext();

	createTransport();
   }

   /**
    * Consistency check
    * 
    * @throws ContextException
    */
   protected void checkTransportContext() throws TransportException {
	if (StringHelper.isEmpty(getType())) { throw new EmptyContextParameterException(RDV_TRANSPORT_TYPE, context); }
	if (StringHelper.isEmpty(getService())) { throw new EmptyContextParameterException(RDV_SERVICE_PARAMETER, context); }
	if (StringHelper.isEmpty(getDaemon())) { throw new EmptyContextParameterException(RDV_DAEMON_PARAMETER, context); }
   }

   /**
    * transport creation
    * 
    * @throws TransportException
    */
   protected void createTransport() throws TransportException {
	try {	   
	   // Native implem
	   Tibrv.open(Tibrv.IMPL_NATIVE);	   
	} catch (final TibrvException rvde) {
	   throw new TransportException("messaging.transport.rdv.open", rvde);
	}

	try {
	   // Create RVD transport
	   transport = new TibrvRvdTransport(getService(), getNetwork(), getDaemon());	   
	} catch (final TibrvException rvde) {
	   throw new TransportException("messaging.transport.rdv.create", rvde);
	}

	try {
	   // Create RVD certified transport if needed
	   if (RdvTransportTypeEnum.CERTIFIED.equals(getType())) {
		cmTransport = new TibrvCmTransport(transport, getCmname(), true);

		if (!StringHelper.isEmpty(getTimeout())) {
		   try {
			cmTransport.setDefaultTimeLimit(Double.parseDouble(getTimeout()));
		   } catch (final NumberFormatException nfe) {
			throw new TransportException("messaging.transport.rdv.timeout", new String[] { getTimeout() });
		   }
		}
	   }
	} catch (final TibrvException rvde) {
	   throw new TransportException("messaging.transport.rdv.create", rvde);
	}

	try {
	   // Create RVD Distributed queue transport if needed
	   if (RdvTransportTypeEnum.DQUEUE.equals(getType())) {
		cmQueueTransport = new TibrvCmQueueTransport(transport, getCmname());

		if (!StringHelper.isEmpty(getTimeout())) {
		   try {
			cmQueueTransport.setDefaultTimeLimit(Double.parseDouble(getTimeout()));
		   } catch (final NumberFormatException nfe) {
			throw new TransportException("messaging.transport.rdv.timeout", new String[] { getTimeout() });
		   }
		}

	   }
	} catch (final TibrvException rvde) {
	   throw new TransportException("messaging.transport.rdv.create", rvde);
	}
   }

   public void close() throws TransportException {

	if (transport != null) {
	   transport.destroy();
	}

	try {
	   Tibrv.close();
	} catch (final TibrvException trve) {
	   throw new TransportException("messaging.transport.rdv.close", trve);
	}
   }

   /**
    * @return RDV service config value
    */
   public String getService() {
	return context.getString(RDV_SERVICE_PARAMETER);
   }

   /**
    * @return RDV Network config value
    */
   public String getNetwork() {
	return context.getString(RDV_NETWORK_PARAMETER);
   }

   /**
    * @return RDV Daemon config value
    */
   public String getDaemon() {
	return context.getString(RDV_DAEMON_PARAMETER);
   }

   /**
    * @return RDV Timeout value for certified transport
    */
   public String getTimeout() {
	return context.getString(RDV_CERTIFIED_TIMEOUT);
   }

   /**
    * @return RDV type transport
    * @see RdvTransportTypeEnum
    */
   public String getType() {
	return context.getString(RDV_TRANSPORT_TYPE);
   }

   /**
    * @return RDV type transport
    * @see RdvTransportTypeEnum
    */
   public String getCmname() {
	return context.getString(RDV_CERTIFIED_CMNAME);
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


}