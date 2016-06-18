package com.kubetown.sockettest;

import android.app.Activity;

import com.kubetown.sockettest.ioclient.client.Ack;
import com.kubetown.sockettest.ioclient.client.Socket;

import io.socket.emitter.Emitter;

/**
 * Created by Arango on 6/15/2016.
 */
public class APIListener implements Ack, Emitter.Listener {
    private static String eventName;
    private static Socket mSocket;
    private static APICallback mCallback;
    private static Activity activity;
    private static boolean isPersistent = false;

    public APIListener(Activity activity, APICallback mCallback) {
        this.mCallback = mCallback;
        this.activity = activity;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setSocket (Socket mSocket) {
        this.mSocket = mSocket;
    }
    public void persist() {
        isPersistent = true;
    }

    @Override
    public void call(final Object... args) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onResult((APIResult) args[0]);
            }
        });
    }


}
