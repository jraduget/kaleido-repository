/*
 * Copyright 2008-2014 the original author or authors
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
package org.kaleidofoundry.core.env;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.kaleidofoundry.core.env.model.EnvironmentInfo;
import org.kaleidofoundry.core.env.model.EnvironmentStatus;

/**
 * Environment Initializer Controller :<br/>
 * 
 * @author jraduget
 */
@Stateless(mappedName = "ejb/environments/manager")
// @Singleton
@Path("/environments/")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class EnvironmentController {

   private final EnvironmentInitializer envInitializer;

   /** injected and used to handle security context */
   @Context
   SecurityContext securityContext;

   /** injected and used to handle URIs */
   @Context
   UriInfo uriInfo;

   public EnvironmentController() {
	envInitializer = EnvironmentInitializer.instance;
   }

   /**
    * @return application environment
    */
   @GET
   @Path("info")
   public EnvironmentInfo getInfo() {
	return envInitializer.getInfo();
   }

   /**
    * @return application status
    */
   @GET
   @Path("status")
   public EnvironmentStatus getStatus() {
	return envInitializer.getStatus();
   }

   /**
    * Load I18n / FileStore / CacheManager / Configuration <br/>
    * it can be overloaded
    */
   @PUT
   @Path("start")
   public Response start() {
	try {
	   envInitializer.start();
	   return Response.ok(envInitializer.getStatus()).build();
	} catch (IllegalStateException ise) {
	   envInitializer.getLogger().error(ise.getMessage());
	   return Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	} catch (RuntimeException re) {
	   envInitializer.getLogger().error("start error", re);
	   return Response.serverError().build();
	}
   }

   /**
    * destroy and free
    */
   @PUT
   @Path("stop")
   public Response stop() {
	try {
	   envInitializer.stop();
	   return Response.ok(envInitializer.getStatus()).build();
	} catch (IllegalStateException ise) {
	   envInitializer.getLogger().error(ise.getMessage());
	   return Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN).build();
	} catch (RuntimeException re) {
	   envInitializer.getLogger().error("start error", re);
	   return Response.serverError().build();
	}
   }
}
