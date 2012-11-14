/*
 *  Copyright 2008-2012 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kaleidofoundry.core.store;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

@Stateless(mappedName = "ejb/filestore/manager")
@Path("/filestores/")
public class FileStoreManagerBean {

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   @GET
   @Path("{store}/get/{resource}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public byte[] getResourceContent(final @PathParam("store") String store, final @PathParam("resource") String resource) throws FileStoreNotFoundException,
	   ResourceNotFoundException, ResourceException {
	return findFileStore(store).get(resource).getBytes();
   }

   @GET
   @Path("{store}/info/{resource}")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public ResourceHandler getResourceInfo(final @PathParam("store") String store, final @PathParam("resource") String resource)
	   throws FileStoreNotFoundException, ResourceNotFoundException, ResourceException {
	return findFileStore(store).get(resource);
   }

   private FileStore findFileStore(String storeName) throws FileStoreNotFoundException {
	if (!FileStoreFactory.getRegistry().contains(storeName)) {
	   throw new FileStoreNotFoundException(storeName);
	} else {
	   return FileStoreFactory.getRegistry().get(storeName);
	}
   }
}
