package com.example.finalthesis.db4o_the_project.fragments;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.finalthesis.db4o_the_project.Constants;
import com.example.finalthesis.db4o_the_project.R;
import com.example.finalthesis.db4o_the_project.models.ConstraintsJsonData;
import com.example.finalthesis.db4o_the_project.models.MyConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class WatchMyConstraints extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ConstraintsJsonData constraintsJsonData;
    private static ObjectMapper mapper = new ObjectMapper();
    private List<MyConstraint> myConstraints;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_my_constraints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewQ);
        menu = navigationView.getMenu();
        String jsonData = getIntent().getStringExtra("ConstraintsJsonData");
        if (jsonData != null) {
            try {
                constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
                myConstraints=constraintsJsonData.getConstraints();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fillList();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

       // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void fillList(){
        menu.clear();
        int counter=1;
        for (MyConstraint myConstraint : myConstraints) {
           menu.add(Menu.NONE,counter,Menu.NONE,getString(R.string.Constraint)+counter);
            counter++;
        }
        onNavigationItemSelected(menu.getItem(0));
    }

    private void fillData(MyConstraint tmpCon){
        int size=tmpCon.getPath().size();
        String tmp="<html><body>";
        for (int i=0;i<size;i++) {
            tmp += "<ul><li>"+tmpCon.getPath().get(i);
        }
        tmp+=addOperator(tmpCon.getOperator())+tmpCon.getValue();
        for(int i1=0; i1<size;i1++){
            tmp+="</li></ul>";
        }
        tmp+="</body></html>";
        WebView tmpView=(WebView)findViewById(R.id.constraintsViewer);
        tmpView.loadData(tmp, "text/html", null);
    }

    private String addOperator(int operator){
        String tmpOperator=null;
        switch (operator) {
            case Constants.GREATER_OPERATOR:
                tmpOperator=" > ";
                break;
            case Constants.SMALLER_OPERATOR:
                tmpOperator=" < ";
                break;
            case Constants.LIKE_OPERATOR:
                tmpOperator=" LIKE ";
                break;
            case Constants.EQUALS_OPERATOR:
                tmpOperator=" = ";
                break;
            case Constants.GREATER_EQUALS_OPERATOR:
                tmpOperator=" >= ";
                break;
            case Constants.SMALLER_EQUALS_OPERATOR:
                tmpOperator=" <= ";
                break;
        }
        return tmpOperator;
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
        getMenuInflater().inflate(R.menu.watch_my_constraints, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId()-1;
        //int id=item.getOrder();
         MyConstraint tmpCon=myConstraints.get(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        fillData(tmpCon);
        return true;
    }
}
