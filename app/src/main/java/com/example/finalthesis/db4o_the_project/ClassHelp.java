package com.example.finalthesis.db4o_the_project;

import com.db4o.ObjectContainer;
import com.db4o.reflect.ReflectClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ILIAS on 11/3/2016.
 */




public class ClassHelp {
    private ObjectContainer db = null;
    private List<ReflectClass> lik;
    public ClassHelp(ObjectContainer db) {
        this.db = db;
        lik=new ArrayList<ReflectClass>();
    }

    public List<String> MyClass() {
        return findClass();
    }

    private List<String> findClass() {
        ReflectClass[] sdmkd= db.ext().reflector().knownClasses();
        List<String> itemList = new ArrayList<String>();
        for (int i = 0; i < sdmkd.length / 2; i++) {
            if (!sdmkd[i].toString().contains("com.") && !sdmkd[i].toString().contains("java.")) {
                itemList.add(sdmkd[i].getName());
                lik.add(sdmkd[i]);
            }
        }
        return itemList;
    }
    private ReflectClass retClass(int num){
        return lik.get(num).getDelegate();
    }
}
