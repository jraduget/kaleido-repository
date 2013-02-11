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

import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_BUFFER_SIZE;
import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_CHARSET;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.BufferSize;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Charset;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolver;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.util.StringHelper;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

/**
 * Google application file store service
 * <br/>
 * <b>References</b>
 * <ul>
 * <li>https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/FileService</li>
 * <li>https://developers.google.com/storage/docs/hellogooglestorage/li>
 * <li>https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/AppEngineFile/li>
 * <li>https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/AppEngineFile.FileSystem/li>
 * </ul>
 * @author Jerome RADUGET
 */
public class GaeFileStore extends AbstractFileStore {

   protected final FileService fileService = FileServiceFactory.getFileService();

   /**
    * @param context
    */
   public GaeFileStore(final RuntimeContext<FileStore> context) {
	super(context);
   }

   /**
    * @param baseUri
    * @param context
    */
   public GaeFileStore(final String baseUri, final RuntimeContext<FileStore> context) {
	super(baseUri, context);
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#getStoreType()
    */
   @Override
   public FileStoreType[] getStoreType() {
	return new FileStoreType[] { FileStoreTypeEnum.gs };
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doGet(java.net.URI)
    */
   @Override
   protected ResourceHandler doGet(final URI resourceUri) throws ResourceNotFoundException, ResourceException {

	FileReadChannel readChannel = null;
	ResourceHandler resource;
	MimeTypeResolver mimeTypeResolver = MimeTypeResolverFactory.getService();

	try {
	   final String filepath = "/gs" + resourceUri.getPath();
	   final AppEngineFile readableFile = new AppEngineFile(filepath);
	   final String mimeType = mimeTypeResolver.getMimeType(FileHelper.getFileNameExtension(resourceUri.getPath()));
	   final String charset = context.getString(Charset, DEFAULT_CHARSET.getCode());
	   final int bufferSize = context.getInteger(BufferSize, DEFAULT_BUFFER_SIZE);

	   readChannel = fileService.openReadChannel(readableFile, false);	   
	   
	   LOGGER.debug("gs filepath={}", filepath);	   
	   
	   if (mimeTypeResolver.isText(mimeType)) {		
		LOGGER.debug("gs content type isText={}", true);		
		//resource = createResourceHandler(resourceUri.toString(), readChannel, Channels.newReader(readChannel, charset), charset);
		resource = createResourceHandler(resourceUri.toString(), readChannel, new BufferedReader(Channels.newReader(readChannel, charset), bufferSize), charset);
	   } else {
		LOGGER.debug("gs content type isText={}", false);
		//resource = createResourceHandler(resourceUri.toString(), readChannel, Channels.newInputStream(readChannel));
		resource = createResourceHandler(resourceUri.toString(), readChannel, new BufferedInputStream(Channels.newInputStream(readChannel), bufferSize));
	   }
	   // Set some meta datas
	   if (resource instanceof ResourceHandlerBean) {
		// ((ResourceHandlerBean) resource).setLastModified(file.lastModified());
		((ResourceHandlerBean) resource).setMimeType(mimeType);
	   }

	   return resource;
	} catch (final FileNotFoundException fnfe) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} catch (final IOException ioe) {
	   throw new ResourceException(resourceUri.toString(), ioe);
	} 
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   @NotYetImplemented
   protected void doRemove(final URI resourceUri) throws ResourceNotFoundException, ResourceException {
	final String filepath = "/gs" + resourceUri.getPath();
	final AppEngineFile readableFile = new AppEngineFile(filepath);
	try {
	   fileService.delete(readableFile);
	} catch (FileNotFoundException fnfe) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} catch (IOException ioe) {
	   throw new ResourceException(ioe, resourceUri.toString());
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {

	String bucketName = getBucketName();
	String charset = context.getString(Charset, DEFAULT_CHARSET.getCode());
	boolean isTextContent = false;
	MimeTypeResolver mimeTypeResolver = MimeTypeResolverFactory.getService();

	GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder().setBucket(bucketName).setKey(resource.getResourceUri());

	if (!StringHelper.isEmpty(resource.getMimeType())) {
	   optionsBuilder.setMimeType(resource.getMimeType());
	   isTextContent = mimeTypeResolver.isText(resource.getMimeType());
	}

	// optionsBuilder.setAcl("public_read");

	if (isTextContent) {
	   optionsBuilder.setContentEncoding(charset);
	}

	final AppEngineFile writableFile;
	FileWriteChannel writeChannel = null;

	try {
	   writableFile = fileService.createNewGSFile(optionsBuilder.build());
	   writeChannel = fileService.openWriteChannel(writableFile, true);

	   if (isTextContent) {
		final char[] cbuff = new char[context.getInteger(BufferSize, DEFAULT_BUFFER_SIZE)];
		int lengthToWrite = resource.getReader(charset).read(cbuff);

		PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, charset));
		while (lengthToWrite != -1) {
		   out.write(cbuff, 0, lengthToWrite);
		   lengthToWrite = resource.getReader(charset).read(cbuff);
		}
		out.close();

	   } else {
		final byte[] buff = new byte[context.getInteger(BufferSize, DEFAULT_BUFFER_SIZE)];
		int lengthToWrite = resource.getInputStream().read(buff);
		while (lengthToWrite != -1) {
		   writeChannel.write(ByteBuffer.wrap(buff, 0, lengthToWrite));
		   lengthToWrite = resource.getInputStream().read(buff);
		}
	   }

	} catch (final IOException ioe) {
	   throw new ResourceException(ioe, resourceUri.toString());
	} finally {
	   if (writeChannel != null) {
		try {
		   writeChannel.closeFinally();
		} catch (final IOException ioe) {
		   throw new ResourceException(ioe, resourceUri.toString());
		}
	   }
	}

   }
   
   public ResourceHandler createResourceHandler(final String resourceUri, final FileReadChannel fileReadChannel, final Reader reader, final String charset) {
	ResourceHandler resource = new GaeResourceHandlerBean(this, resourceUri, fileReadChannel, reader, charset);
	openedResources.put(resourceUri, resource);
	return resource;
   }

   public ResourceHandler createResourceHandler(final String resourceUri, final FileReadChannel fileReadChannel, final InputStream input) {
	ResourceHandler resource = new GaeResourceHandlerBean(this, resourceUri, fileReadChannel, input);
	openedResources.put(resourceUri, resource);
	return resource;
   }


   /**
    * @return gs bucket name of the resource store (it is extracted from the resource store base dir
    */
   protected String getBucketName() {
	String bucketName = getBaseUri();

	for (FileStoreType type : getStoreType()) {
	   if (bucketName.startsWith(type.name())) {
		bucketName = bucketName.replace(type.name(), "");
		break;
	   }
	}
	bucketName = bucketName.replaceFirst(":/", "");
	bucketName = bucketName.replaceFirst("://", "");
	return bucketName;
   }
}
