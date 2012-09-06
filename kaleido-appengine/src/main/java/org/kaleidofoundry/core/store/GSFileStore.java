/*
 *  Copyright 2008-2011 the original author or authors.
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

import static org.kaleidofoundry.core.store.FileStoreConstants.DEFAULT_CHARSET;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.BufferSize;
import static org.kaleidofoundry.core.store.FileStoreContextBuilder.Charset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.io.FileHelper;
import org.kaleidofoundry.core.io.MimeTypeResolverFactory;
import org.kaleidofoundry.core.lang.annotation.NotYetImplemented;
import org.kaleidofoundry.core.util.StringHelper;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

/**
 * https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/FileService
 * https://developers.google.com/storage/docs/hellogooglestorage
 * https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/AppEngineFile
 * https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/files/AppEngineFile.FileSystem
 * 
 * @author Jerome RADUGET
 */
public class GSFileStore extends AbstractFileStore {

   protected final FileService fileService = FileServiceFactory.getFileService();

   protected final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

   // protected final BlobstoreService service = BlobstoreServiceFactory.getBlobstoreService();
   // protected final ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();
   // protected final ThreadLocal<HttpServletResponse> httpResponse = new ThreadLocal<HttpServletResponse>();

   /**
    * @param context
    */
   public GSFileStore(final RuntimeContext<FileStore> context) {
	super(context);
   }

   /**
    * @param baseUri
    * @param context
    */
   public GSFileStore(final String baseUri, final RuntimeContext<FileStore> context) {
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

	try {
	   final String filepath = getBucketName() + resourceUri.getPath();
	   final AppEngineFile readableFile = new AppEngineFile(filepath);

	   readChannel = fileService.openReadChannel(readableFile, false);
	   resource = createResourceHandler(resourceUri.toString(), Channels.newInputStream(readChannel));

	   // Set some meta datas
	   if (resource instanceof ResourceHandlerBean) {
		// ((ResourceHandlerBean) resource).setLastModified(file.lastModified());
		((ResourceHandlerBean) resource).setMimeType(MimeTypeResolverFactory.getService().getMimeType(
			FileHelper.getFileNameExtension(resourceUri.getPath())));
	   }

	   return resource;
	} catch (final FileNotFoundException fnfe) {
	   throw new ResourceNotFoundException(resourceUri.toString());
	} catch (final IOException ioe) {
	   throw new ResourceException(resourceUri.toString(), ioe);
	} finally {
	   if (readChannel != null) {
		try {
		   readChannel.close();
		} catch (final IOException ioe) {
		   throw new ResourceException(ioe, resourceUri.toString());
		}
	   }
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doRemove(java.net.URI)
    */
   @Override
   @NotYetImplemented
   protected void doRemove(final URI resourceUri) throws ResourceNotFoundException, ResourceException {
	final String filepath = getBucketName() + resourceUri.getPath();
	final AppEngineFile readableFile = new AppEngineFile(filepath);
	blobstoreService.delete(fileService.getBlobKey(readableFile));
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.core.store.AbstractFileStore#doStore(java.net.URI, org.kaleidofoundry.core.store.ResourceHandler)
    */
   @Override
   protected void doStore(final URI resourceUri, final ResourceHandler resource) throws ResourceException {

	String bucketName = getBucketName();
	String charset = context.getString(Charset, DEFAULT_CHARSET.getCode());

	GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder().setBucket(bucketName).setKey(resource.getResourceUri());

	if (!StringHelper.isEmpty(resource.getMimeType())) {
	   optionsBuilder.setMimeType(resource.getMimeType());
	}

	// optionsBuilder.setAcl("public_read");
	// optionsBuilder.addUserMetadata("myfield1", "my field value");

	if (MimeTypeResolverFactory.getService().isMimeTypeAscii(resource.getMimeType())) {
	   optionsBuilder.setContentEncoding(charset);
	}

	final AppEngineFile writableFile;
	FileWriteChannel writeChannel = null;

	try {
	   writableFile = fileService.createNewGSFile(optionsBuilder.build());
	   writeChannel = fileService.openWriteChannel(writableFile, true);

	   final byte[] buff = new byte[context.getInteger(BufferSize, 128)];
	   int lengthToWrite = resource.getInputStream().read(buff);
	   while (lengthToWrite != -1) {
		writeChannel.write(ByteBuffer.wrap(buff, 0, lengthToWrite));
		lengthToWrite = resource.getInputStream().read(buff);
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
