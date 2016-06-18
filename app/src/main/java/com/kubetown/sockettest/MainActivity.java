package com.kubetown.sockettest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.kubetown.sockettest.ioclient.client.IO;
import com.kubetown.sockettest.ioclient.client.Manager;
import com.kubetown.sockettest.ioclient.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;


public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://kubetown.com:4748");
        } catch (URISyntaxException e) {}
    }

    private API api;

    private TextView button;
    private TextView button2;
    private String userID = "";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = new API();
        api.attachSocket(mSocket);
        api.addPersistentListener("validate", new APIListener(this, new APICallback() {
            @Override
            public void onResult(APIResult res) {
                Toast.makeText(MainActivity.this, "Validated!", Toast.LENGTH_LONG).show();
            }
        }));
        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];
                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                        // modify request headers
                        headers.put("X-User-ID", Arrays.asList(userID));
                        headers.put("X-User-Token", Arrays.asList(token));
                    }
                });

            }
        });


        mSocket.connect();

        button = (TextView)findViewById(R.id.button);
        button2 = (TextView)findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User u = new User();
                u.username = "pl";
                u.password = "arango";
                api.userLogin(u, new APIListener(MainActivity.this, new APICallback() {
                    @Override
                    public void onResult(APIResult res) {
                        userID = res.user.id.toString();
                        token = res.user.token;
                        Toast.makeText(MainActivity.this, "Logged in!", Toast.LENGTH_LONG).show();

                    }
                }));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.userList(new APIListener(MainActivity.this, new APICallback() {
                    @Override
                    public void onResult(APIResult res) {
                        if (res.error != null) {
                            Toast.makeText(MainActivity.this, "Error: " + res.error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Gson gson = new Gson();
                            Toast.makeText(MainActivity.this, "Users: " + gson.toJson(res.users), Toast.LENGTH_LONG).show();
                        }
                    }
                }));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        api.detatchSocket(mSocket);
    }
}
