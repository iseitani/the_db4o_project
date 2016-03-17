package com.example.finalthesis.db4o_the_project.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.cs.Db4oClientServer;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.R;

public class ConstraintDialogFragment extends DialogFragment {

    private static final String ARG_REFLECT_FIELD_NAME = "reflectFieldName";
    private static final String ARG_REFLECT_FIELD_TYPE = "reflectFieldType";
    private static final String ARG_REFLECT_CLASS_NAME = "reflectClassName";

    private String reflectFieldName;
    private String reflectFieldType;
    private String reflectClassName;
    private Spinner valueSpinner;
    private EditText valueEditText;
    private boolean isBoolean = false;
    private static OnSaveButtonClickedListener mOnSaveButtonClickedListener;

    private String[] fieldValues;

    private static final String[] numbersOperators = new String[4];

    static {
        numbersOperators[0] = "Select operator";
        numbersOperators[1] = "=";
        numbersOperators[2] = ">";
        numbersOperators[3] = "<";
    }

    private static final String[] stringOperators = new String[3];

    static {
        stringOperators[0] = "Select operator";
        stringOperators[1] = "=";
        stringOperators[2] = "Like";
    }

    private static final String[] charOrBooleanOperators = new String[2];

    static {
        charOrBooleanOperators[0] = "Select operator";
        charOrBooleanOperators[1] = "=";
    }

    public ConstraintDialogFragment() {
    }

    public static ConstraintDialogFragment newInstance(String reflectFieldName, String reflectFieldType, String reflectClassName, OnSaveButtonClickedListener listener) {
        mOnSaveButtonClickedListener = listener;
        ConstraintDialogFragment fragment = new ConstraintDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFLECT_FIELD_NAME, reflectFieldName);
        args.putString(ARG_REFLECT_FIELD_TYPE, reflectFieldType);
        args.putString(ARG_REFLECT_CLASS_NAME, reflectClassName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reflectFieldName = getArguments().getString(ARG_REFLECT_FIELD_NAME);
            reflectFieldType = getArguments().getString(ARG_REFLECT_FIELD_TYPE);
            reflectClassName = getArguments().getString(ARG_REFLECT_CLASS_NAME);
        }
        Log.i("MyConstraintDialog", "Field name: " + reflectFieldName + " Field type: " + reflectFieldType);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_constraint, ((ViewGroup) getView()));
        builder.setTitle("Constraint for field: " + reflectFieldName)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mOnSaveButtonClickedListener != null) {
                            mOnSaveButtonClickedListener.onSaveButtonClicked();
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setView(view);

        Spinner operatorSpinner = (Spinner) view.findViewById(R.id.operatorSpinner);
        valueSpinner = (Spinner) view.findViewById(R.id.valueSpinner);
        valueEditText = (EditText) view.findViewById(R.id.valueEditText);
        ArrayAdapter adapter;
        switch (reflectFieldType) {
            case "java.lang.String":
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stringOperators);
                valueEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "boolean":
                isBoolean = true;
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, charOrBooleanOperators);
                break;
            case "char":
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, charOrBooleanOperators);
                break;
            default:
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numbersOperators);
                if (reflectFieldType.equals("java.util.Date")) {
                    valueEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                } else {
                    int type = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
                    valueEditText.setInputType(type);
                }
                break;
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatorSpinner.setAdapter(adapter);

        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("=")) {
                    valueEditText.setVisibility(View.INVISIBLE);
                    valueSpinner.setVisibility(View.VISIBLE);
                    if (isBoolean) {
                        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"true", "false"});
                        valueSpinner.setAdapter(adapter);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    } else {
                        if (fieldValues == null) {
                            new LoadFieldValues().execute();
                        } else {
                            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fieldValues);
                            valueSpinner.setAdapter(adapter);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        }
                    }
                } else {
                    valueEditText.setVisibility(View.VISIBLE);
                    valueSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnSaveButtonClickedListener = null;
    }

    private class LoadFieldValues extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Loading...."});
            valueSpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        protected Void doInBackground(Void... params) {
            //ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/nosqlOLYMPIC.db4o");
            ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "192.168.6.153", 4000, "olympic", "olympic");
            ReflectClass reflectClass = db.ext().reflector().forName(reflectClassName);
            ReflectField reflectField = reflectClass.getDeclaredField(reflectFieldName);
            Query query = db.query();
            query.constrain(reflectClass);
            ObjectSet result = query.execute();
            fieldValues = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                fieldValues[i] = reflectField.get(result.get(i)).toString();
            }
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fieldValues);
            valueSpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

    public interface OnSaveButtonClickedListener {
        void onSaveButtonClicked();
    }
}
