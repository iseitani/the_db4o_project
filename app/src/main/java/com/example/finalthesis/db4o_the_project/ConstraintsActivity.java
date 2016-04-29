package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.adapters.ReflectFieldsRecyclerViewAdapter;
import com.example.finalthesis.db4o_the_project.fragments.ConstraintDialogFragment;
import com.example.finalthesis.db4o_the_project.models.ConstraintsJsonData;
import com.example.finalthesis.db4o_the_project.models.Db4oSubClass;
import com.example.finalthesis.db4o_the_project.models.MyConstraint;
import com.example.finalthesis.db4o_the_project.views.DividerItemDecoration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstraintsActivity extends AppCompatActivity {

    private RecyclerView reflectFieldsRecyclerView;
    private ReflectFieldsRecyclerViewAdapter reflectFieldsRecyclerViewAdapter;
    private String reflectClassName;
    private String classPath;
    private List<String> userClasses;
    private List<MyConstraint> myConstraints;
    private static ObjectMapper mapper = new ObjectMapper();
    private static final int REQUEST_CODE = 1;
    private Context ctx;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx = this;
        // preferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        userClasses = new ArrayList<>();
        myConstraints = new ArrayList<>();
        reflectFieldsRecyclerView = (RecyclerView) findViewById(R.id.reflectFieldsRecyclerView);
        reflectFieldsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reflectFieldsRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        reflectClassName = getIntent().getStringExtra(getString(R.string.CLASS_NAME));
        if (reflectClassName != null) {
            new GetReflectFields().execute(reflectClassName);
        }

        classPath = getIntent().getStringExtra(getString(R.string.CLASS_PATH));
        if (classPath != null) {
            setTitle(classPath + " (" + reflectClassName + ")");
        } else {
            setTitle(reflectClassName);
        }

        String jsonData = getIntent().getStringExtra(getString(R.string.ConsJSOND));
        if (jsonData != null) {
            try {
                ConstraintsJsonData constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
                myConstraints = constraintsJsonData.getConstraints();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(ConstraintsActivity.this, RecursivePrint.class);
                    if (!myConstraints.isEmpty()) {
                        final String[] operators = new String[]{"AND", "OR"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConstraintsActivity.this);
                        builder.setTitle("Select operator")
                                .setItems(operators, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ConstraintsJsonData constraintsJsonData = new ConstraintsJsonData();
                                        constraintsJsonData.setConstraints(myConstraints);
                                        constraintsJsonData.setOperator(which);
                                        try {
                                            intent.putExtra("QueryFlag", true);
                                            intent.putExtra(getString(R.string.CLASS_NAME), constraintsJsonData.getConstraints().get(0).getPath().get(0));
                                            intent.putExtra(getString(R.string.ConsJSOND), mapper.writeValueAsString(constraintsJsonData));
                                            intent.putExtra("reflectClassIndex", -1);
                                            Log.i("MyConstraintsActivity", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(constraintsJsonData));
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent); //Auto einai edo giati prota prepei na epileksei o xristis ton operator
                                    }
                                });
                        builder.create().show();
                    } else {
                        intent.putExtra("QueryFlag", true);
                        if (classPath != null) {
                            intent.putExtra(getString(R.string.CLASS_NAME), classPath.split("\\.")[0]);
                        } else {
                            intent.putExtra(getString(R.string.CLASS_NAME), reflectClassName);
                        }
                        intent.putExtra("reflectClassIndex", -1);
                        startActivity(intent);
                    }
                }
            });
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ConstraintsJsonData constraintsJsonData = new ConstraintsJsonData();
        constraintsJsonData.setConstraints(myConstraints);
        Intent intent = new Intent();
        try {
            intent.putExtra(getString(R.string.ConsJSOND), mapper.writeValueAsString(constraintsJsonData));
            Log.i("MyConstraintsActivity", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(constraintsJsonData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                String jsonData = data.getExtras().getString(getString(R.string.ConsJSOND));
                if (jsonData != null) {
                    try {
                        ConstraintsJsonData constraintsJsonData = mapper.readValue(jsonData, ConstraintsJsonData.class);
                        myConstraints = constraintsJsonData.getConstraints();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showReflectFields(List<ReflectField> reflectFields) {
        reflectFieldsRecyclerViewAdapter = new ReflectFieldsRecyclerViewAdapter(reflectFields, new OnListItemLongClickedListener() {
            @Override
            public void onListItemLongClicked(final ReflectField reflectField) {
                if (reflectField.getFieldType().isCollection() || reflectField.getFieldType().isArray()) {
                    Toast.makeText(ConstraintsActivity.this, getString(R.string.ConstraintsCollectionMSG), Toast.LENGTH_LONG).show();
                } else if (userClasses.contains(reflectField.getFieldType().getName())) {
                    Intent intent = new Intent(ConstraintsActivity.this, ConstraintsActivity.class);
                    intent.putExtra(getString(R.string.CLASS_NAME), reflectField.getFieldType().getName());
                    if (classPath != null) {
                        intent.putExtra(getString(R.string.CLASS_PATH), classPath + getString(R.string.Dot) + reflectField.getName());
                    } else {
                        intent.putExtra(getString(R.string.CLASS_PATH), reflectClassName + getString(R.string.Dot) + reflectField.getName());
                    }
                    try {
                        ConstraintsJsonData constraintsJsonData = new ConstraintsJsonData();
                        constraintsJsonData.setConstraints(myConstraints);
                        intent.putExtra(getString(R.string.ConsJSOND), mapper.writeValueAsString(constraintsJsonData));
                        Log.i("MyConstraintsActivity", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(constraintsJsonData));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    ConstraintDialogFragment constraintDialogFragment = ConstraintDialogFragment.newInstance(reflectField.getName(),
                            reflectField.getFieldType().getName(), reflectClassName, new ConstraintDialogFragment.OnSaveButtonClickedListener() {
                                @Override
                                public void onSaveButtonClicked(String value, int operator) {
                                    reflectFieldsRecyclerViewAdapter.setHasConstraint(reflectField, true);
                                    reflectFieldsRecyclerViewAdapter.notifyDataSetChanged();
                                    MyConstraint myConstraint = new MyConstraint();
                                    List<String> path;
                                    if (classPath != null) {
                                        path = new ArrayList<>(Arrays.asList(classPath.split("\\.")));
                                    } else {
                                        path = new ArrayList<>();
                                        path.add(reflectClassName);
                                    }
                                    path.add(reflectField.getName());
                                    myConstraint.setPath(path);
                                    myConstraint.setOperator(operator);
                                    myConstraint.setReflectFieldType(reflectField.getFieldType().getName());
                                    myConstraint.setValue(value);
                                    myConstraints.add(myConstraint);
                                    Toast.makeText(ConstraintsActivity.this, getString(R.string.ConstraintAdded) + reflectField.getName(), Toast.LENGTH_LONG).show();
                                }
                            });
                    constraintDialogFragment.show(getSupportFragmentManager(), getString(R.string.constraintDialog));
                }
            }
        });
        for (ReflectField reflectField : reflectFields) {
            String reflectFieldName = reflectField.getName();
            for (MyConstraint myConstraint : myConstraints) {
                List<String> path = myConstraint.getPath();
                if (classPath != null) {
                    String[] splitClassPath = classPath.split("\\.");
                    if (path.get(path.size() - 2).equals(splitClassPath[splitClassPath.length - 1]) && path.get(path.size() - 1).equals(reflectFieldName)) {
                        reflectFieldsRecyclerViewAdapter.setHasConstraint(reflectField, true);
                    }
                }
            }
        }
        reflectFieldsRecyclerView.setAdapter(reflectFieldsRecyclerViewAdapter);
    }

    public interface OnListItemLongClickedListener {
        void onListItemLongClicked(ReflectField reflectField);
    }

    class GetReflectFields extends AsyncTask<String, Void, Void> {

        ProgressDialog mProgressDialog;
        List<ReflectField> reflectFields;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reflectFields = new ArrayList<>();
            mProgressDialog = new ProgressDialog(ConstraintsActivity.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading Fields");
            mProgressDialog.setMessage("Searching for the required Fields");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            Db4oSubClass db4oSubClass = new Db4oSubClass(ctx);
            reflectFields = db4oSubClass.reflectFieldsNameASRF(params[0]);
            //Start
            // ObjectContainer db =db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), host, port, username, password);
            //ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            //ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.2.2", 4000, "olympic", "olympic");

            // ReflectField[] allReflectFields = db.ext().reflector().forName(params[0]).getDelegate().getDeclaredFields();
            // for (ReflectField reflectField : allReflectFields) {
            //     if (!reflectField.getFieldType().getName().contains(".Object")) {
            //         reflectFields.add(reflectField);
            //     }
            // }
            /*
            ReflectClass[] reflectClasses = db.ext().reflector().knownClasses();
            for (ReflectClass reflectClass : reflectClasses) {
                if (!reflectClass.toString().contains("com.") && !reflectClass.toString().contains("java.")) {
                    userClasses.add(reflectClass.getName());
                }
            }
            db.close();

            */
            userClasses = db4oSubClass.reflectClassesAsSTR();
            db4oSubClass.CloseDB();
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
