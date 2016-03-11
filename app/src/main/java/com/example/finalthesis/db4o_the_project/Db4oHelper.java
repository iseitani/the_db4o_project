package com.example.finalthesis.db4o_the_project;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;

public class Db4oHelper {

    private static ObjectContainer oc = null;

    public Db4oHelper() {
    }

    /**
     * Create, open and close the database
     */
    public ObjectContainer db() {
        try {
            if (oc == null || oc.ext().isClosed()) {
                oc = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
                //oc = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "ip", 4000, "olympic", "olympic");
            }
            return oc;
        } catch (Exception ie) {
            Log.e(Db4oHelper.class.getName(), ie.toString());
            return null;
        }
    }

    /**
     * Returns the path for the database location
     */
    private String db4oDBFullPath(Context ctx) {
        return ctx.getDir("data", 0) + "/" + "myDatabase.db4o";
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
