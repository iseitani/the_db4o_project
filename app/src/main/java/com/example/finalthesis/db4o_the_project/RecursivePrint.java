package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.models.ConstraintsJsonData;
import com.example.finalthesis.db4o_the_project.models.MyConstraint;
import com.example.finalthesis.db4o_the_project.views.DividerItemDecoration;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursivePrint extends AppCompatActivity {

    private RecyclerView recursiveRecyclerView;
    private List<MyConstraint> myConstraints;
    private static ObjectMapper mapper = new ObjectMapper();
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

        myConstraints = new ArrayList<>();
        recursiveRecyclerView = (RecyclerView) findViewById(R.id.recursiveprintRecyclerView);
        recursiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recursiveRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        //For XML
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        String jsonData = getIntent().getExtras().getString("ConstraintsJsonData");
        if (jsonData != null) {
            try {
                ConstraintsJsonData constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
                myConstraints = constraintsJsonData.getConstraints();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
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

    public Constraint MyQ(List<Object> s, Query q, int operator) {
        if (s.size() == 1) {
            switch(operator){
                case 0:
                    return q.constrain(s.get(0)).greater();
                case 1:
                    return q.constrain(s.get(0)).smaller();
                case 2:
                    return q.constrain(s.get(0)).like();
                case 3:
                    return q.constrain(s.get(0));
            }
        }

        Query sub = q.descend(s.get(0).toString());
        s.remove(0);
        return MyQ(s, sub, operator);
    }

    class GetReflectFields extends AsyncTask<String, Void, Void> {

        ProgressDialog mProgressDialog;
        List<ReflectField> reflectFields;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reflectFields = new ArrayList<>();
            mProgressDialog = new ProgressDialog(RecursivePrint.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Results");
            mProgressDialog.setMessage("Your Results will be available as soon as possible");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            //ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            ReflectField[] allReflectFields = db.ext().reflector().forName(params[0]).getDelegate().getDeclaredFields();
            for (ReflectField reflectField : allReflectFields) {
                if (!reflectField.getFieldType().getName().contains(".Object")) {
                    reflectFields.add(reflectField);
                }
            }
            ReflectClass[] reflectClasses = db.ext().reflector().knownClasses();
            for (ReflectClass reflectClass : reflectClasses) {
                if (!reflectClass.toString().contains("com.") && !reflectClass.toString().contains("java.")) {
                 //   userClasses.add(reflectClass.getName());
                }
            }
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void tmpt) {
            super.onPostExecute(tmpt);
            //showFields(fFields);
            mProgressDialog.dismiss();
        }
    }

}
