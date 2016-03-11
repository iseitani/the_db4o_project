package com.example.finalthesis.db4o_the_project;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ILIAS on 11/3/2016.
 */
public class FieldsHelp extends Db4oHelper {
    private  ReflectClass sdmkd = null;
    private ObjectContainer db=null;
    public FieldsHelp(ObjectContainer db,ReflectClass sdmkd){
         this.db=db;
         this.sdmkd=sdmkd;
    }

    public List<String> reflectFields(){
        return reflectFiel();
    }


    private List<String> reflectFiel(){
        List<String> reflectATTListSTRING;
        reflectATTListSTRING=new ArrayList<String>() ;
        ReflectField[] fields = sdmkd.getDeclaredFields();
        for (ReflectField rff : fields) {
            if (rff.getFieldType().isCollection()) {
                Query query = db.query();
                query.constrain(sdmkd);
                query.descend(rff.getName());
                ObjectSet result = query.execute();
                reflectATTListSTRING.add(rff.getName() + "-->" + result.get(0)+"  : Collection ") ;
            } else {
                if(rff.getFieldType().isPrimitive() ){
                    reflectATTListSTRING.add(rff.getName() + "-->" + rff.getFieldType().getName());
                    System.out.println(rff.getName() + "-->" + rff.getFieldType().getName());
                }
                else{
                    reflectATTListSTRING.add(rff.getName() + "-->" + rff.getFieldType().getName()+"  : Object");
                }
            }
        }
        return reflectATTListSTRING;
    }
}
