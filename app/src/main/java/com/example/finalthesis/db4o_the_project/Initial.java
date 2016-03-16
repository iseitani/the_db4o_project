package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import java.util.ArrayList;
import java.util.List;

public class Initial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /*MHN TA DIAGRAPSEIS
    private String host=null;
    private int port=0;
    private String username=null;
    private String password=null;
    */
    private Menu menu;
    private String kClass = null;
    private List<ReflectClass> knownClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.proceedToQuery);
        fab.setClickable(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(Initial.this, ConstraintsActivity.class);
                x.putExtra("className", kClass);
                startActivity(x);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        knownClasses = new ArrayList<>();
        new LoadClassATTTask().execute();
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.initial, menu);
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

    private void Class2Text() {
        List<String> temp = new ArrayList<String>();
        int io = 0;
        for (int ko = 0; ko < knownClasses.size(); ko++) {
            menu.add(knownClasses.get(ko).getName());
        }
        onNavigationItemSelected(menu.getItem(0));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        kClass = item.getTitle().toString();
        setTitle(kClass);
        FloatingActionButton proceedToQuery = (FloatingActionButton) findViewById(R.id.proceedToQuery);
        proceedToQuery.setClickable(true);
        new LoadObjectsATTTask().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class LoadObjectsATTTask extends AsyncTask<Integer, String, String> {
        List<String> reflectATTListSTRING;
        ProgressDialog mProgressDialog;

        protected String doInBackground(Integer... params) {
            reflectATTListSTRING = new ArrayList<String>();
            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            //ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            ReflectClass rf1 = db.ext().reflector().forName(kClass);
            ReflectClass rfi = rf1.getDelegate();
            ReflectField[] fields = rfi.getDeclaredFields();
            for (ReflectField rff : fields) {
                if (rff.getFieldType().isCollection()) {
                    reflectATTListSTRING.add(rff.getName() + "-->" + " isCollection");
                } else {
                    reflectATTListSTRING.add(rff.getName() + "-->" + rff.getFieldType().getName());
                }
            }
            db.close();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Initial.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Fields ");
            mProgressDialog.setMessage("Searching for the required Fields");
            mProgressDialog.show();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(String t) {
            // TODO: Edo malon prepei na emfanizeis ta ReflectFields
            mProgressDialog.dismiss();
        }
    }

    private class LoadClassATTTask extends AsyncTask<Integer, String, String> {
        List<String> reflectATTListSTRING;
        ProgressDialog mProgressDialog;
        ReflectClass[] sdmkd = null;

        protected String doInBackground(Integer... params) {
            reflectATTListSTRING = new ArrayList<String>();
            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            //ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            sdmkd = db.ext().reflector().knownClasses();
            db.close();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Initial.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Clases ");
            mProgressDialog.setMessage("Searching for Classes");
            mProgressDialog.show();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(String t) {
            List<String> itemList = new ArrayList<String>();
            for (int i = 0; i < sdmkd.length / 2; i++) {
                if (!sdmkd[i].toString().contains("com.") && !sdmkd[i].toString().contains("java.")) {
                    knownClasses.add(sdmkd[i]);
                }
            }
            Class2Text();
            mProgressDialog.dismiss();
        }
    }
}
