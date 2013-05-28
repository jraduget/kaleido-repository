package org.kaleidofoundry.core.store;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jerome RADUGET
 */
public class UploadRequestDataSource implements DataSource {

    private InputStream dataStream;
    private String contentType;

    public UploadRequestDataSource(final String pType, final InputStream pDataStream) {
        dataStream = null;
        contentType = null;
        contentType = pType;
        dataStream = pDataStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (dataStream == null) {
            throw new IOException("input stream contain no data");
        } else {
            return dataStream;
        }
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("no output stream");

    }

}
