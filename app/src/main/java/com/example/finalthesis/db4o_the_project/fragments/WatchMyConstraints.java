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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        WebView tmpView=(WebView)findViewById(R.id.constraintsViewer);
        tmpView.loadData(fillData(id), "text/html", null);
        tmpView.invokeZoomPicker();
        tmpView.getSettings().setBuiltInZoomControls(true);
        tmpView.reload();
        return true;
    }
    private String fillData(int position){
        MyConstraint tmpCon=myConstraints.get(position);
        String css_content="/*Now the CSS*/\n" +
                "* {margin: 0; padding: 0;}\n" +
                "\n" +
                ".tree ul {\n" +
                "\tpadding-top: 20px; position: relative;\n" +
                "\t\n" +
                "\ttransition: all 0.5s;\n" +
                "\t-webkit-transition: all 0.5s;\n" +
                "\t-moz-transition: all 0.5s;\n" +
                "}\n" +
                "\n" +
                ".tree li {\n" +
                "\tfloat: left; text-align: center;\n" +
                "\tlist-style-type: none;\n" +
                "\tposition: relative;\n" +
                "\tpadding: 20px 5px 0 5px;\n" +
                "\t\n" +
                "\ttransition: all 0.5s;\n" +
                "\t-webkit-transition: all 0.5s;\n" +
                "\t-moz-transition: all 0.5s;\n" +
                "}\n" +
                "\n" +
                "/*We will use ::before and ::after to draw the connectors*/\n" +
                "\n" +
                ".tree li::before, .tree li::after{\n" +
                "\tcontent: '';\n" +
                "\tposition: absolute; top: 0; right: 50%;\n" +
                "\tborder-top: 1px solid #ccc;\n" +
                "\twidth: 50%; height: 20px;\n" +
                "}\n" +
                ".tree li::after{\n" +
                "\tright: auto; left: 50%;\n" +
                "\tborder-left: 1px solid #ccc;\n" +
                "}\n" +
                "\n" +
                "/*We need to remove left-right connectors from elements without \n" +
                "any siblings*/\n" +
                ".tree li:only-child::after, .tree li:only-child::before {\n" +
                "\tdisplay: none;\n" +
                "}\n" +
                "\n" +
                "/*Remove space from the top of single children*/\n" +
                ".tree li:only-child{ padding-top: 0;}\n" +
                "\n" +
                "/*Remove left connector from first child and \n" +
                "right connector from last child*/\n" +
                ".tree li:first-child::before, .tree li:last-child::after{\n" +
                "\tborder: 0 none;\n" +
                "}\n" +
                "/*Adding back the vertical connector to the last nodes*/\n" +
                ".tree li:last-child::before{\n" +
                "\tborder-right: 1px solid #ccc;\n" +
                "\tborder-radius: 0 5px 0 0;\n" +
                "\t-webkit-border-radius: 0 5px 0 0;\n" +
                "\t-moz-border-radius: 0 5px 0 0;\n" +
                "}\n" +
                ".tree li:first-child::after{\n" +
                "\tborder-radius: 5px 0 0 0;\n" +
                "\t-webkit-border-radius: 5px 0 0 0;\n" +
                "\t-moz-border-radius: 5px 0 0 0;\n" +
                "}\n" +
                "\n" +
                "/*Time to add downward connectors from parents*/\n" +
                ".tree ul ul::before{\n" +
                "\tcontent: '';\n" +
                "\tposition: absolute; top: 0; left: 50%;\n" +
                "\tborder-left: 1px solid #ccc;\n" +
                "\twidth: 0; height: 20px;\n" +
                "}\n" +
                "\n" +
                ".tree li a{\n" +
                "\tborder: 1px solid #ccc;\n" +
                "\tpadding: 5px 10px;\n" +
                "\ttext-decoration: none;\n" +
                "\tcolor: #666;\n" +
                "\tfont-family: arial, verdana, tahoma;\n" +
                "\tfont-size: 11px;\n" +
                "\tdisplay: inline-block;\n" +
                "\t\n" +
                "\tborder-radius: 5px;\n" +
                "\t-webkit-border-radius: 5px;\n" +
                "\t-moz-border-radius: 5px;\n" +
                "\t\n" +
                "\ttransition: all 0.5s;\n" +
                "\t-webkit-transition: all 0.5s;\n" +
                "\t-moz-transition: all 0.5s;\n" +
                "}\n" +
                "\n" +
                "/*Time for some hover effects*/\n" +
                "/*We will apply the hover effect the the lineage of the element also*/\n" +
                ".tree li a:hover, .tree li a:hover+ul li a {\n" +
                "\tbackground: #c8e4f8; color: #000; border: 1px solid #94a0b4;\n" +
                "}\n" +
                "/*Connector styles on hover*/\n" +
                ".tree li a:hover+ul li::after, \n" +
                ".tree li a:hover+ul li::before, \n" +
                ".tree li a:hover+ul::before, \n" +
                ".tree li a:hover+ul ul::before{\n" +
                "\tborder-color:  #94a0b4;\n" +
                "}\n" +
                "\n" +
                "/*Thats all. I hope you enjoyed it.\n" +
                "Thanks :)*/";
        String css="<style>"+css_content+"</style>";
        int size=tmpCon.getPath().size();
        String tmp="<html>"+css+"<body><div class=\"tree\">";
        for (int i=0;i<size-1;i++) {
            tmp += "<ul><li><a href=\"#\">"+tmpCon.getPath().get(i)+"</a>";
        }
        tmp+="<ul><li><a href=\"#\">"+tmpCon.getPath().get(size-1)+addOperator(tmpCon.getOperator())+tmpCon.getValue()+"</a>";
        for(int i1=0; i1<size;i1++){
            tmp+="</li></ul>";
        }
        tmp+="</div></body></html>";
        return tmp;
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

}
