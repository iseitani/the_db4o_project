package com.example.finalthesis.db4o_the_project.models;


import com.db4o.ObjectContainer;

public class Db4oSubClass {
    private ObjectContainer db;
    public ObjectContainer getDb(){
        return db;
    }
    public Db4oSubClass(){
        //db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
        ///String ip=;
       // int port=;
       // String username=;
       // String password=;
       // db= Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), ip, port, username, password);
    }
}
