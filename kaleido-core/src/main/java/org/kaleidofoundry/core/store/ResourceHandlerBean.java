/*  
 * Copyright 2008-2010 the original author or authors 
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
package org.kaleidofoundry.core.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.io.IOHelper;
import org.kaleidofoundry.core.lang.annotation.Immutable;
import org.kaleidofoundry.core.lang.annotation.NotNull;
import org.kaleidofoundry.core.lang.annotation.NotThreadSafe;

/**
 * Default {@link ResourceHandler} implementation
 * 
 * @author Jerome RADUGET
 */
@Immutable
@NotThreadSafe
public class ResourceHandlerBean implements ResourceHandler {

   private final InputStream input;

   private boolean closed;

   /**
    * @param input
    */
   public ResourceHandlerBean(@NotNull final InputStream input) {
	this.input = input;
	this.closed = false;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStream()
    */
   @Override
   @NotNull
   public InputStream getInputStream() {
	return input;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getBytes()
    */
   @Override
   @NotNull
   public byte[] getBytes() throws ResourceException {
	try {
	   return IOHelper.toByteArray(getInputStream());
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe);
	} finally {
	   // free resource handler
	   release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader()
    */
   @Override
   @NotNull
   public InputStreamReader getInputStreamReader() throws ResourceException {
	return getInputStreamReader("UTF8");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getInputStreamReader(java.lang.String)
    */
   @Override
   @NotNull
   public InputStreamReader getInputStreamReader(final String charset) throws ResourceException {
	try {
	   return new InputStreamReader(getInputStream(), charset);
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe);
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getText()
    */
   @Override
   @NotNull
   public String getText() throws ResourceException {
	return getText("UTF8");
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#getText(java.lang.String)
    */
   @Override
   @NotNull
   public String getText(final String charset) throws ResourceException {
	BufferedReader reader = null;
	String inputLine;

	try {
	   final StringBuilder stb = new StringBuilder();
	   reader = new BufferedReader(getInputStreamReader(charset));
	   while ((inputLine = reader.readLine()) != null) {
		stb.append(inputLine).append("\n");
	   }
	   return stb.toString();
	} catch (final IOException ioe) {
	   throw new ResourceException(ioe);
	} finally {
	   // free buffered reader
	   try {
		if (reader != null) {
		   reader.close();
		}
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.ResourceStoreMessageBundle.getMessage("store.resource.inputstream.error", ioe.getMessage(),
			ioe), ioe);
	   }

	   // free resource handler
	   release();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.ResourceHandler#release()
    */
   @Override
   public void release() {
	if (input != null && !closed) {
	   try {
		input.close();
		closed = true;
	   } catch (final IOException ioe) {
		throw new IllegalStateException(InternalBundleHelper.ResourceStoreMessageBundle.getMessage("store.resource.inputstream.error", ioe.getMessage(),
			ioe), ioe);
	   }
	}
   }

}
