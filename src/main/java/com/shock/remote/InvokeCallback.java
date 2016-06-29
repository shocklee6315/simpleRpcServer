package com.shock.remote;

import com.shock.remote.protocol.RemoteMessage;

/**
 * Created by shocklee on 16/6/29.
 */
public interface InvokeCallback {

    public void onMessageComplete(RemoteMessage response);

}
