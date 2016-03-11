package com.example.finalthesis.db4o_the_project;

import android.util.Log;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;

public class Db4oHelper {

    private static ObjectContainer oc = null;
    private String host= null;
    private int port=0;
    private String username=null;
    private String password=null;

    public Db4oHelper(String host,int port,String username, String password) {
        this.host=host;
        this.port=port;
        this.username=username;
        this.password=password;
    }

    /**
     * Create, open and close the database
     */
    public ObjectContainer db() {
        try {
            if (oc == null || oc.ext().isClosed()) {
                oc = Db4oClientServer.openClient(Db4oClientServer
                        .newClientConfiguration(), host, port, username, password);
            }
            return oc;
        } catch (Exception ie) {
            Log.e(Db4oHelper.class.getName(), ie.toString());
            return null;
        }
    }
    /**
     * Closes the database
     */
    public void close() {
        if (oc != null) {
            oc.close();
        }
    }
}
