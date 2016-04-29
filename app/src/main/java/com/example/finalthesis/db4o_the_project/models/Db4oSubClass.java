package com.example.finalthesis.db4o_the_project.models;


import android.content.Context;
import android.content.SharedPreferences;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.R;

import java.util.ArrayList;
import java.util.List;

public class Db4oSubClass {
    private ObjectContainer db;

    public ObjectContainer getDb() {
        return db;
    }

    public void CloseDB() {
        getDb().close();
    }

    //Classes
    public ReflectClass[] reflectClasses() {
        return db.ext().reflector().knownClasses();
    }

    public ReflectClass reflectClass(String className) {
        return db.ext().reflector().forName(className);
    }

    public List<String> reflectClassesAsSTR() {
        List<String> reflectATTList = new ArrayList<>();
        for (ReflectClass RC : reflectClassesASREFC()) {
            reflectATTList.add(RC.getName());
        }
        return reflectATTList;
    }

    public List<ReflectClass> reflectClassesASREFC() {
        List<ReflectClass> knownClasses = new ArrayList<>();
        ReflectClass[] sdmkd = reflectClasses();
        for (int i = 0; i < sdmkd.length / 2; i++) {
            if (!sdmkd[i].toString().contains("com.") && !sdmkd[i].toString().contains("java.")) {
                knownClasses.add(sdmkd[i]);
            }
        }
        return knownClasses;
    }

    //Fields
    public ReflectField[] reflectFields(String classname) {
        return db.ext().reflector().forName(classname).getDelegate().getDeclaredFields();
    }

    public List<ReflectField> reflectFieldsNameASRF(String className) {
        List<ReflectField> reflectFields = new ArrayList<>();
        ReflectField[] allReflectFields = reflectFields(className);
        for (ReflectField reflectField : allReflectFields) {
            if (!reflectField.getFieldType().getName().contains(".Object")) {
                reflectFields.add(reflectField);
            }
        }
        return reflectFields;
    }

    public List<String> reflectFieldsNameASSTR(String className) {
        List<String> reflectFields = new ArrayList<>();

        for (ReflectField reflectField : reflectFieldsNameASRF(className)) {
            if (!reflectField.getFieldType().getName().contains(".Object")) {
                reflectFields.add(reflectField.getName());
            }
        }
        return reflectFields;
    }

    public List<String> reflectFieldsNameANDTypeListSTRING(String className) {
        List<String> reflectATTList = new ArrayList<>();
        // ReflectClass rf1 = reflectClass(className);
        // ReflectClass rfi = rf1.getDelegate();
        ReflectField[] fields = reflectFields(className);//rfi.getDeclaredFields();
        for (ReflectField rff : fields) {
            if (rff.getFieldType().isCollection()) {
                reflectATTList.add(rff.getName() + "-->" + " isCollection");
            } else {
                reflectATTList.add(rff.getName() + "-->" + rff.getFieldType().getName());
            }
        }
        //CloseDB();
        return reflectATTList;
    }


    public Db4oSubClass(Context ctx) {
        //db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
        // SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences()
        // getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String ip = sharedPref.getString(ctx.getString(R.string.ServerN), null);
        int port = sharedPref.getInt(ctx.getString(R.string.PortN), 0);
        String username = sharedPref.getString(ctx.getString(R.string.UserN), null);
        String password = sharedPref.getString(ctx.getString(R.string.PasswordN), null);

       /*
        String ip="192.168.1.3";
        int port=4000;
        String username="iseitani";
        String password="iseitani";
        */
        db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), ip, port, username, password);
    }
}
