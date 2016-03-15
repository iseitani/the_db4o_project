package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.adapters.ReflectFieldsRecyclerViewAdapter;
import com.example.finalthesis.db4o_the_project.fragments.ConstraintDialogFragment;
import com.example.finalthesis.db4o_the_project.views.DividerItemDecoration;

import java.util.Arrays;
import java.util.List;

public class ConstraintsActivity extends AppCompatActivity {

    RecyclerView reflectFieldsRecyclerView;
    ReflectFieldsRecyclerViewAdapter reflectFieldsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reflectFieldsRecyclerView = (RecyclerView) findViewById(R.id.reflectFieldsRecyclerView);
        reflectFieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reflectFieldsRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        String reflectClassName = getIntent().getExtras().getString("className");
        if (reflectClassName != null) {
            new GetReflectFields().execute(reflectClassName);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = reflectFieldsRecyclerViewAdapter.getSelectedItems().size() + " fields selected";
                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showReflectFields(List<ReflectField> reflectFields) {
        reflectFieldsRecyclerViewAdapter = new ReflectFieldsRecyclerViewAdapter(reflectFields, new OnListItemClickedListener() {
            @Override
            public void onListItemLongClicked(ReflectField reflectField) {
                //new ConstraintDialog(ConstraintsActivity.this, reflectField.getName()).show();
                ConstraintDialogFragment constraintDialogFragment = new ConstraintDialogFragment(reflectField);
                constraintDialogFragment.show(getSupportFragmentManager(), "constraintDialog");
            }
        });
        reflectFieldsRecyclerView.setAdapter(reflectFieldsRecyclerViewAdapter);
    }

    public interface OnListItemClickedListener {
        void onListItemLongClicked(ReflectField reflectField);
    }

    class GetReflectFields extends AsyncTask<String, Void, Void> {

        ProgressDialog mProgressDialog;
        List<ReflectField> reflectFields;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(ConstraintsActivity.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Fields");
            mProgressDialog.setMessage("Searching for the required Fields");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            reflectFields = Arrays.asList(db.ext().reflector().forName(params[0]).getDelegate().getDeclaredFields());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showReflectFields(reflectFields);
            mProgressDialog.dismiss();
        }
    }

}
