package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.finalthesis.db4o_the_project.models.Db4oSubClass;

import java.util.ArrayList;
import java.util.List;

public class Initial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView ATTListView;
    private Menu menu;
    private String kClass = null;
    private List<String> knownClasses;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.proceedToQuery);
        fab.setClickable(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(Initial.this, ConstraintsActivity.class);
                x.putExtra("className", kClass);
                startActivity(x);
            }
        });//13

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ATTListView = (ListView) findViewById(R.id.fieldsKclasses);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        knownClasses = new ArrayList<>();
        new LoadClassATTTask().execute();//23
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
        if (id == R.id.logout) {
            Intent intent = new Intent( this, LoginActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(intent);
            return true;
        }
        if (id == R.id.FAQ) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.FAQ)));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Class2Text() {
        for (String s : knownClasses) {
            menu.add(s);
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
            Db4oSubClass db4oSubClass = new Db4oSubClass(ctx);
            reflectATTListSTRING = db4oSubClass.reflectFieldsNameANDTypeListSTRING(kClass);
            db4oSubClass.CloseDB();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reflectATTListSTRING = new ArrayList<>();
            mProgressDialog = new ProgressDialog(Initial.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Fields ");
            mProgressDialog.setMessage("Searching for the required Fields");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String t) {
            ATTListView.setAdapter(new ArrayAdapter<>(
                    getApplication().getBaseContext(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1, reflectATTListSTRING
            ));
            mProgressDialog.dismiss();
        }


    }

    private class LoadClassATTTask extends AsyncTask<Integer, String, String> {
        ProgressDialog mProgressDialog;

        protected String doInBackground(Integer... params) {
            Db4oSubClass db4oSubClass = new Db4oSubClass(ctx);
            knownClasses = db4oSubClass.reflectClassesAsSTR();
            db4oSubClass.CloseDB();
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

        @Override
        protected void onPostExecute(String t) {
            Class2Text();
            mProgressDialog.dismiss();
        }
    }
}
