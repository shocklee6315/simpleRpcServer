package com.shock.remote;

import com.shock.remote.protocol.RemoteMessage;

/**
 * Created by shocklee on 16/6/28.
 */
public interface RequestProcessor {

    public RemoteMessage processRequest(RemoteMessage request) throws Exception;

}
