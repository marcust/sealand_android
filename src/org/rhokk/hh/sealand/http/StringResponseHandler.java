/**
 * 
 */
package org.rhokk.hh.sealand.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.rhokk.hh.sealand.util.IOUtils;

import android.util.Log;

public class StringResponseHandler implements ResponseHandler<ServerResponse> {
    
    private final String TAG = StringResponseHandler.class.getSimpleName();
    
    @Override
    public ServerResponse handleResponse(HttpResponse response) {
        try {
            final InputStream stream = response.getEntity().getContent();

            final String responseValue = IOUtils.toString(stream);

            Log.i( TAG, "Response is: " + responseValue );

            return ServerResponse.create( response.getStatusLine().getStatusCode()  );

        } catch (IllegalStateException e) {
            Log.i( TAG, e.getMessage() );
            throw e;
        } catch (IOException e) {
            Log.i( TAG, e.getMessage() );
            e.printStackTrace();
        }

        return ServerResponse.createError();
    }

}