package com.kubetown.sockettest;

import com.kubetown.sockettest.ioclient.client.Ack;
import com.kubetown.sockettest.ioclient.client.Socket;

import io.socket.emitter.Emitter;


/**
 * Created by Arango on 6/9/2016.
 */
public class API {
    private static Socket mSocket;

    public void attachSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }
    public void detatchSocket(Socket mSocket) {
    }
    private void callSocket(String event, APIListener listener, APIResult res) {
        listener.setEventName(event);
        listener.setSocket(mSocket);
        mSocket.emit(event, res, listener);
    }
    public void addPersistentListener(String event, APIListener listener) {
        listener.setEventName(event);
        listener.setSocket(mSocket);
        listener.persist();
        mSocket.on(event, listener);
    }

    /** BEGIN WRAPPERS **/
    public void userLogin(User user, APIListener listener) {
        APIResult res = new APIResult();
        res.user = user;
        callSocket("user_login", listener, res);
    }
    public void userList(APIListener listener) {
        callSocket("user_list", listener, new APIResult());
    }
}
