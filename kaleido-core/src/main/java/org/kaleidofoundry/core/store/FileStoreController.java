/*
 *  Copyright 2008-2014 the original author or authors.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.kaleidofoundry.core.store.model.FileStoreEntry;
import org.kaleidofoundry.core.store.model.ResourceHandlerEntity;
import org.kaleidofoundry.core.util.locale.LocaleFactory;

@Stateless(mappedName = "ejb/stores/manager")
@Path("/stores/")
public class FileStoreController {

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   /**
    * @return get the list of registered file store
    */
   @GET
   @XmlElementWrapper(name = "stores")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public List<FileStoreEntry> getStores() {
	List<FileStoreEntry> stores = new ArrayList<FileStoreEntry>();
	for (Entry<String, FileStore> storeEntry : FileStoreFactory.getRegistry().entrySet()) {
	   stores.add(new FileStoreEntry(storeEntry.getKey(), storeEntry.getValue().getBaseUri()));
	}
	return stores;
   }

   /**
    * fetch the content of a resource
    * 
    * @param store
    * @param resource
    * @return resource content, with the right charset, mime type ....
    * @throws FileStoreNotFoundException
    * @throws ResourceNotFoundException
    * @throws ResourceException
    */
   @GET
   @Path("{store}/content/{resource}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response getResourceContent(final @PathParam("store") String store, final @PathParam("resource") String resource) throws FileStoreNotFoundException,
	   ResourceNotFoundException, ResourceException {

	ResourceHandler rs = findFileStore(store).get(resource);
	return Response.ok(rs.getBytes())
		.type(rs.getMimeType())
		.location(URI.create(rs.getUri()))
		.lastModified(new Date(rs.getLastModified()))
		.header(HttpHeaders.CONTENT_TYPE, rs.getMimeType() + "; " + rs.getCharset())
		// .cacheControl(CacheControl.valueOf(value))
		// .expires(date)
		.status(Status.OK).build();
   }

   /**
    * fetch the meta information of a resource
    * 
    * @param store
    * @param resource
    * @return the resource meta info
    * @throws FileStoreNotFoundException
    * @throws ResourceNotFoundException
    * @throws ResourceException
    */
   @GET
   @Path("{store}/meta/{resource}")
   @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public ResourceHandlerEntity getResourceMetaInfo(final @PathParam("store") String store, final @PathParam("resource") String resource)
	   throws FileStoreNotFoundException, ResourceNotFoundException, ResourceException {
	return toResourceHandlerEntity(findFileStore(store).get(resource));
   }

   /**
    * remove a resource
    * 
    * @param store
    * @param resource
    * @throws FileStoreNotFoundException
    * @throws ResourceNotFoundException
    * @throws ResourceException
    */
   @DELETE
   @Path("{store}/{resource}")
   public void removeResource(final @PathParam("store") String store, final @PathParam("resource") String resource) throws FileStoreNotFoundException,
	   ResourceNotFoundException, ResourceException {
	findFileStore(store).remove(resource);
   }

   /**
    * find the store in the registry
    * 
    * @param storeName
    * @return
    * @throws FileStoreNotFoundException
    */
   static FileStore findFileStore(String storeName) throws FileStoreNotFoundException {
	if (!FileStoreFactory.getRegistry().keySet().contains(storeName)) {
	   throw new FileStoreNotFoundException(storeName);
	} else {
	   return FileStoreFactory.getRegistry().get(storeName);
	}
   }

   /**
    * converter
    * 
    * @param res
    * @return
    */
   static ResourceHandlerEntity toResourceHandlerEntity(ResourceHandler res) {
	Calendar calendar = new GregorianCalendar(LocaleFactory.getDefaultFactory().getCurrentLocale());
	calendar.setTimeInMillis(res.getLastModified());

	ResourceHandlerEntity facade = new ResourceHandlerEntity();
	facade.setUri(res.getUri());
	facade.setPath(res.getPath());
	facade.setMimeType(res.getMimeType());
	facade.setCharset(res.getCharset());
	facade.setSize(res.getLength());
	facade.setUpdatedDate(calendar.getTime());
	facade.setCreationDate(calendar.getTime());
	return facade;
   }
}
