package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.adapters.ReflectFieldsRecyclerViewAdapter;
import com.example.finalthesis.db4o_the_project.fragments.ConstraintDialogFragment;
import com.example.finalthesis.db4o_the_project.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstraintsActivity extends AppCompatActivity {

    private RecyclerView reflectFieldsRecyclerView;
    private ReflectFieldsRecyclerViewAdapter reflectFieldsRecyclerViewAdapter;
    private String reflectClassName;
    private String classPath;
    private List<String> userClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userClasses = new ArrayList<>();
        reflectFieldsRecyclerView = (RecyclerView) findViewById(R.id.reflectFieldsRecyclerView);
        reflectFieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reflectFieldsRecyclerView.addItemDecoration(new DividerItemDecoration(this));


        classPath = getIntent().getExtras().getString("className");
        if (classPath != null) {
            String[] classes = classPath.split("\\.");
            if (classes.length > 0) {
                reflectClassName = classes[classes.length - 1];
            } else {
                reflectClassName = classPath;
            }
            new GetReflectFields().execute(reflectClassName);
            setTitle(classPath);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReflectFields(List<ReflectField> reflectFields) {
        reflectFieldsRecyclerViewAdapter = new ReflectFieldsRecyclerViewAdapter(reflectFields, new OnListItemClickedListener() {
            @Override
            public void onListItemLongClicked(final ReflectField reflectField) {
                if (reflectField.getFieldType().isCollection() || reflectField.getFieldType().isArray()) {
                    Toast.makeText(ConstraintsActivity.this, "Constraints in collections and arrays are not supported", Toast.LENGTH_LONG).show();
                } else if (userClasses.contains(reflectField.getFieldType().getName())) {
                    startActivity(new Intent(ConstraintsActivity.this, ConstraintsActivity.class)
                            .putExtra("className", classPath + "." + reflectField.getFieldType().getName()));
                } else {
                    ConstraintDialogFragment constraintDialogFragment = ConstraintDialogFragment.newInstance(reflectField.getName(),
                            reflectField.getFieldType().getName(), reflectClassName, new ConstraintDialogFragment.OnSaveButtonClickedListener() {
                                @Override
                                public void onSaveButtonClicked() {
                                    reflectFieldsRecyclerViewAdapter.setHasConstraint(reflectField, true);
                                    reflectFieldsRecyclerViewAdapter.notifyDataSetChanged();
                                    Toast.makeText(ConstraintsActivity.this, "Constraint added at field " + reflectField.getName(), Toast.LENGTH_LONG).show();
                                }
                            });
                    constraintDialogFragment.show(getSupportFragmentManager(), "constraintDialog");
                }
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
            //ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            reflectFields = Arrays.asList(db.ext().reflector().forName(params[0]).getDelegate().getDeclaredFields());
            ReflectClass[] reflectClasses = db.ext().reflector().knownClasses();
            for (ReflectClass reflectClass : reflectClasses) {
                if (!reflectClass.toString().contains("com.") && !reflectClass.toString().contains("java.")) {
                    userClasses.add(reflectClass.getName());
                }
            }
            db.close();
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
