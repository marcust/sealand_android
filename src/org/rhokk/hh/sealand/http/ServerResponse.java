package org.rhokk.hh.sealand.http;

import org.json.JSONObject;

public class ServerResponse {

    final JSONObject _object;
    final Result _result;
    
    private ServerResponse ( JSONObject object, Result result ) {
        super();
        _object = object;
        _result = result;
    }
    
    public static ServerResponse create( JSONObject object, Result result ) {
        return new ServerResponse( object, result );
    }

    public JSONObject getObject() {
        return _object;
    }

    public boolean isSuccess() {
        return _result == Result.OK;
    }
    
    
}
