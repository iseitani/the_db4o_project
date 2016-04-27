package com.example.finalthesis.db4o_the_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.adapters.ReflectClassesResultsRecyclerViewAdapter;
import com.example.finalthesis.db4o_the_project.adapters.ReflectFieldsValuesRecyclerViewAdapter;
import com.example.finalthesis.db4o_the_project.models.ConstraintsJsonData;
import com.example.finalthesis.db4o_the_project.models.MyConstraint;
import com.example.finalthesis.db4o_the_project.views.DividerItemDecoration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecursivePrint extends AppCompatActivity {

    private RecyclerView recursiveRecyclerView;
    private ReflectFieldsValuesRecyclerViewAdapter reflectFieldsValuesRecyclerViewAdapter;

    private ConstraintsJsonData constraintsJsonData;
    private static ObjectMapper mapper = new ObjectMapper();

    private List<String> userClasses;
    private int reflectClassIndex;
    private String classPath;
    private String attributePath;

    /*
      for selected fields preview, at the end of the project I will upload the class
      private List<MyFields> myFields;
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recursive_print);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String jsonData = getIntent().getStringExtra("ConstraintsJsonData");
        if (jsonData != null) {
            try {
                constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //LIGES GRAMMES PIO KATW DEN EIDES TO VARIABLE???? ESY LES META MHN DHMIOYRGOUME AXRHSTA VARIABLES
        classPath = getIntent().getStringExtra("classPath");
        if (classPath == null) {
            classPath = constraintsJsonData.getConstraints().get(0).getPath().get(0);
        }

        attributePath = getIntent().getStringExtra("attributePath");

        reflectClassIndex = getIntent().getIntExtra("reflectClassIndex", -1);
        // currentPath=getIntent().getStringExtra("currentPath");

        recursiveRecyclerView = (RecyclerView) findViewById(R.id.recursiveprintRecyclerView);
        recursiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recursiveRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        // Auto merikes fores xriazete ama kathisterisei to query
        // giati ama den exei adapter to RecyclerView den kanei kan ton kopo na zografistei
        List<String> emptyList = new ArrayList<>();
        reflectFieldsValuesRecyclerViewAdapter = new ReflectFieldsValuesRecyclerViewAdapter(emptyList, null, null);
        recursiveRecyclerView.setAdapter(reflectFieldsValuesRecyclerViewAdapter);

        //For XML
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new RunQuery().execute(constraintsJsonData.getConstraints().get(0).getPath().get(0));
    }

    //Grammh 92 einai to error 8a prepei na phgainei mpros pisw
   /*
    @Override

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
               String jsonData = data.getExtras().getString("ConstraintsJsonData");
                //remove the previous class
                try {
                    String tempcurrentPath = data.getStringExtra("currentPath");
                    StringBuffer text = new StringBuffer(tempcurrentPath);
                    text.replace(text.lastIndexOf(":"), text.length() - 1, "");
                } catch (Exception ex) {
                }

                if (jsonData != null) {
                    try {
                        ConstraintsJsonData constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
                        myConstraints = constraintsJsonData.getConstraints();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
               }
                reflectClassIndex = getIntent().getIntExtra("reflectClassIndex", -1);
            }
        }
    }
    */

    @Override
    public void onBackPressed() {
        //Oi grammes 105-106 mporoun na grafoun edw h ekei pou einai
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recursive_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Object correctGeorgesBugs(Object value, Object type) {
        Object temp = null;

        switch (type.toString()) {
            case ReflectMTypes.FLOAT:
                temp = Float.parseFloat(value.toString());
                break;
            case ReflectMTypes.INT:
                temp = Integer.parseInt(value.toString());
                break;
            default:
                temp = value;
        }
        /*
        if (type.toString().equalsIgnoreCase(ReflectMTypes.INT)) {
            temp = Integer.parseInt(value.toString());
        } else if (type.toString().equalsIgnoreCase(ReflectMTypes.FLOAT)) {
            temp = Float.parseFloat(value.toString());
        } else {
            temp = value;
        }
        */
        return temp;
    }

    public Constraint MyQ(List<Object> s, Query q, int operator) {
        if (s.size() == 2) {
            switch (operator) {
                case Constants.GREATER_OPERATOR:
                    return q.constrain(correctGeorgesBugs(s.get(0), s.get(1))).greater();
                case Constants.SMALLER_OPERATOR:
                    return q.constrain(correctGeorgesBugs(s.get(0), s.get(1))).smaller();
                case Constants.LIKE_OPERATOR:
                    return q.constrain(s.get(0)).like();
                case Constants.EQUALS_OPERATOR:
                    return q.constrain(correctGeorgesBugs(s.get(0), s.get(1)));
              /* Edo einai mia endiaferousa prosthiki gia tous operators ">=" kai "<="
              ostoso mporei na dimiourgithoun provlimata me ta and kai or (tha to suzitisoume)
              */
                case Constants.GREATER_EQUALS_OPERATOR:
                    return MyQ(s, q, Constants.GREATER_OPERATOR).or(MyQ(s, q, Constants.EQUALS_OPERATOR));
                case Constants.SMALLER_EQUALS_OPERATOR:
                    return MyQ(s, q, Constants.SMALLER_OPERATOR).or(MyQ(s, q, Constants.EQUALS_OPERATOR));
            }
        }
        Query sub = q.descend(s.get(0).toString());
        s.remove(0);
        return MyQ(s, sub, operator);
    }

    class RunQuery extends AsyncTask<String, Void, Void> {

        ProgressDialog mProgressDialog;
        List<ReflectField> reflectFields;
        List<String> valuesToPrint;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reflectFields = new ArrayList<>();
            userClasses = new ArrayList<>();
            valuesToPrint = new ArrayList<>();
            mProgressDialog = new ProgressDialog(RecursivePrint.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Results");
            mProgressDialog.setMessage("Your Results will be available as soon as possible");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            //Start

            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            //ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.2.2", 4000, "olympic", "olympic");

            // Edo apofasizoume poia ReflectField xreiazomaste gia na emfanisooume to object
            List<String> classPathList = new ArrayList<>(Arrays.asList(classPath.split(":")));
            ReflectField[] allReflectFields;
            if (!classPathList.isEmpty()) {
                allReflectFields = db.ext().reflector().forName(classPathList.get(classPathList.size() - 1)).getDelegate().getDeclaredFields();
            } else {
                allReflectFields = db.ext().reflector().forName(params[0]).getDelegate().getDeclaredFields();
            }
            for (ReflectField reflectField : allReflectFields) {
                if (!reflectField.getFieldType().getName().contains(".Object")) {
                    reflectFields.add(reflectField);
                }
            }

            ReflectClass[] reflectClasses = db.ext().reflector().knownClasses();
            for (ReflectClass reflectClass : reflectClasses) {
                if (!reflectClass.toString().contains("com.") && !reflectClass.toString().contains("java.")) {
                    userClasses.add(reflectClass.getName());
                }
            }
            //end

            // Building query
            Query query = db.query();
            query.constrain(db.ext().reflector().forName(params[0]));
            int queryOperator = constraintsJsonData.getOperator();
            Constraint lasConstraint = null;
            for (MyConstraint myConstraint : constraintsJsonData.getConstraints()) {
                List<Object> s = new ArrayList<>();
                s.addAll(myConstraint.getPath());
                s.add(myConstraint.getValue());
                s.add(myConstraint.getReflectFieldType());
                s.remove(0);
                if (lasConstraint != null) {
                    switch (queryOperator) {
                        case Constants.AND_OPERATOR:
                            lasConstraint = lasConstraint.and(MyQ(s, query, myConstraint.getOperator()));
                            break;
                        case Constants.OR_OPERATOR:
                            lasConstraint = lasConstraint.or(MyQ(s, query, myConstraint.getOperator()));
                            break;
                    }
                } else {
                    lasConstraint = MyQ(s, query, myConstraint.getOperator());
                }
            }
            // Execute query
            ObjectSet objectSet = query.execute();
            if (reflectClassIndex != -1) {
                if (attributePath != null) {
                    List<ReflectClass> classPathReflectClasses = new ArrayList<>();
                    for (String className : new ArrayList<>(Arrays.asList(classPath.split(":")))) {
                        classPathReflectClasses.add(db.ext().reflector().forName(className));
                    }
                    Object o = objectSet.get(reflectClassIndex);
                    printObject(findOutWhichObjectToPrint(objectSet.get(reflectClassIndex), new ArrayList<>(Arrays.asList(attributePath.split(":"))), classPathReflectClasses));
                } else {
                    printObject(objectSet.get(reflectClassIndex));
                }
            } else {
                printAllObjects(objectSet);
            }
            db.close();
            return null;
        }

        private void printAllObjects(ObjectSet objectSet) {
            //NOT THE WHOLE OBJECT(?)
            //nop
            for (Object o : objectSet) {
                valuesToPrint.add(o.toString());
            }
        }

        private void printObject(Object o) {
            for (ReflectField reflectField : reflectFields) {
                Object value = reflectField.get(o);
                if (value != null) {
                    valuesToPrint.add(reflectField.getName() + " : " + reflectField.get(o).toString());
                } else {
                    valuesToPrint.add(reflectField.getName() + " : null");
                }
            }
        }

        private Object findOutWhichObjectToPrint(Object o, List<String> attributePath, List<ReflectClass> classPathReflectClasses) {
            ReflectField reflectField = classPathReflectClasses.get(0).getDeclaredField(attributePath.get(0));
            if (attributePath.size() == 1) {
                return reflectField.get(o);
            }
            attributePath.remove(0);
            classPathReflectClasses.remove(0);
            return findOutWhichObjectToPrint(reflectField.get(o), attributePath, classPathReflectClasses);
        }

        @Override
        protected void onPostExecute(Void tmpt) {
            super.onPostExecute(tmpt);
            //showFields(fFields);
            if (reflectClassIndex != -1) {
                reflectFieldsValuesRecyclerViewAdapter = new ReflectFieldsValuesRecyclerViewAdapter(valuesToPrint, reflectFields, new OnReflectFieldItemClickedListener() {
                    @Override
                    public void onListItemClicked(ReflectField reflectField) {
                        // Edo tha vlepoume an einai anafora se allo antikeimeno
                        String fieldType = reflectField.getFieldType().getName();
                        if (userClasses.contains(fieldType)) {
                            //Einai Anafora
                            List<String> tempL = new ArrayList<>(Arrays.asList(classPath.split(":")));
                            if (tempL.contains(fieldType)) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                                alertDialogBuilder.setTitle("You already have viewed " + fieldType);
                                alertDialogBuilder.setMessage("You cannot view " + fieldType + " again");
                                alertDialogBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                try {
                                    Intent intent = new Intent(RecursivePrint.this, RecursivePrint.class);
                                    intent.putExtra("ConstraintsJsonData", mapper.writeValueAsString(constraintsJsonData));
                                    intent.putExtra("reflectClassIndex", reflectClassIndex);
                                    classPath = classPath.concat(":" + fieldType);
                                    intent.putExtra("classPath", classPath);
                                    if (attributePath == null) {
                                        intent.putExtra("attributePath", reflectField.getName());
                                    } else {
                                        intent.putExtra("attributePath", attributePath + ":" + reflectField.getName());
                                    }
                                    Log.i("MyRecurcivePrint", "classPath: " + classPath);
                                    Log.i("MyRecurcivePrint", "attributePath: " + attributePath);
                                    startActivity(intent);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                recursiveRecyclerView.setAdapter(reflectFieldsValuesRecyclerViewAdapter);
                reflectFieldsValuesRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                ReflectClassesResultsRecyclerViewAdapter reflectClassesResultsRecyclerViewAdapter = new ReflectClassesResultsRecyclerViewAdapter(valuesToPrint, new OnReflectClassItemClickedListener() {
                    @Override
                    public void onListItemClicked(int reflectClassIndex) {

                        Intent intent = new Intent(RecursivePrint.this, RecursivePrint.class);
                        try {
                            intent.putExtra("ConstraintsJsonData", mapper.writeValueAsString(constraintsJsonData));
                            intent.putExtra("reflectClassIndex", reflectClassIndex);
                            Log.i("MyConstraintsActivity", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(constraintsJsonData));
                        } catch (JsonProcessingException e) {//why not Exception ,it can be anything
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
                recursiveRecyclerView.setAdapter(reflectClassesResultsRecyclerViewAdapter);
                reflectClassesResultsRecyclerViewAdapter.notifyDataSetChanged();
            }

            mProgressDialog.dismiss();
        }
    }

    public interface OnReflectFieldItemClickedListener {
        void onListItemClicked(ReflectField reflectField);
    }

    public interface OnReflectClassItemClickedListener {
        void onListItemClicked(int reflectClassIndex);
    }
}
