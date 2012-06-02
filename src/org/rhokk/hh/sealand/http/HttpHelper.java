package org.rhokk.hh.sealand.http;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;
import org.rhokk.hh.sealand.BaseActivity;
import org.rhokk.hh.sealand.util.StringUtils;

import android.util.Log;


public class HttpHelper {

    public static ServerResponse getServerResponse( final JSONObject payload, final String url, String userAgentString ) throws ClientProtocolException, IOException {
        final HttpPost post = new HttpPost( url );

        Log.d( BaseActivity.LOG_ID, "Calling  " + url + ":\n" + payload.toString() );

        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "api_key=f34df0fc602d1ef9d7a1ba8fa8a051fd17d6d2a1");
        post.setEntity( new StringEntity(payload.toString(), "UTF-8" ) );
        
        
        final HttpClient client = new DefaultHttpClient();
        
        if ( StringUtils.isNotBlank( userAgentString ) ) {
            HttpProtocolParams.setUserAgent( client.getParams(), userAgentString );
        }
        
        try {
            final ServerResponse response = client.execute( post, new StringResponseHandler() );

            return response;

        } finally {
            client.getConnectionManager().shutdown();
        }
    }


}
