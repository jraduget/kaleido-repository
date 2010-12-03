/*
 *  Copyright 2008-2010 the original author or authors.
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
package org.kaleidofoundry.core.naming;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.kaleidofoundry.core.context.Context;

/**
 * <p>
 * <h3>Simple naming service usage</h3> Inject {@link NamingService} context and instance using {@link Context} annotation without
 * parameters, but using if needed external configuration
 * </p>
 * <br/>
 * 
 * @author Jerome RADUGET
 */
public class LocalDatasourceSample01 {

   @Context("myLocalCtx")
   private NamingService namingService;

   /**
    * @param message
    * @return input message
    * @throws SQLException
    * @throws NamingServiceException
    */
   public Connection getConnection(final String message) throws SQLException {

	// get local datasource
	DataSource myDatasource = namingService.locate("jdbc/kaleido", DataSource.class);

	// return a connection from the pool
	return myDatasource.getConnection();
   }

}
