package org.rhokk.hh.sealand.http;


public class ServerResponse {

    private final int  _responseCode;
    
    private ServerResponse ( final int responseCode ) {
        super();
        _responseCode = responseCode;
    }
    
    public static ServerResponse create(int statusCode) {
    	return new ServerResponse( statusCode  );
	}

	public int getResponseCode() {
		return _responseCode;
	}

	public static ServerResponse createError() {
    	return new ServerResponse( -1 );
	}

	public boolean isSuccess() {
		return _responseCode >= 200 && _responseCode < 400;
	}

}
