package com.example.finalthesis.db4o_the_project.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.Constants;
import com.example.finalthesis.db4o_the_project.R;
import com.example.finalthesis.db4o_the_project.ReflectMTypes;
import com.example.finalthesis.db4o_the_project.models.Db4oSubClass;

public class ConstraintDialogFragment extends DialogFragment {

    private static final String ARG_REFLECT_FIELD_NAME = "reflectFieldName";
    private static final String ARG_REFLECT_FIELD_TYPE = "reflectFieldType";
    private static final String ARG_REFLECT_CLASS_NAME = "reflectClassName";

    private String reflectFieldName;
    private String reflectFieldType;
    private String reflectClassName;
    private Spinner operatorSpinner;
    private Spinner valueSpinner;
    private EditText valueEditText;
    private TextInputLayout valueTextInputLayout;
    private boolean isBoolean = false;
    private static OnSaveButtonClickedListener mOnSaveButtonClickedListener;
    private AlertDialog mAlertDialog;
    private Context ctx;
    private String[] fieldValues;

    private static final String[] numbersOperators = new String[6];

    static {
        numbersOperators[0] = "Select operator";
        numbersOperators[1] = "=";
        numbersOperators[2] = ">";
        numbersOperators[3] = "<";
        numbersOperators[4] = ">=";
        numbersOperators[5] = "<=";

    }

    private static final String[] stringOperators = new String[3];

    static {
        stringOperators[0] = "Select operator";
        stringOperators[1] = "(=)/Equals";
        stringOperators[2] = "Contains";
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
        //Log.i("MyConstraintDialog", "Field name: " + reflectFieldName + " Field type: " + reflectFieldType);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        ctx = getActivity();
        if (getArguments() != null) {
            reflectFieldName = getArguments().getString(ARG_REFLECT_FIELD_NAME);
            reflectFieldType = getArguments().getString(ARG_REFLECT_FIELD_TYPE);
            reflectClassName = getArguments().getString(ARG_REFLECT_CLASS_NAME);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_constraint, ((ViewGroup) getView()));
        builder.setTitle(getString(R.string.ConstraintFragmentTitle) + reflectFieldName)
                .setPositiveButton(getString(R.string.SaveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mOnSaveButtonClickedListener != null) {
                            int operator;
                            String value;
                            switch (operatorSpinner.getSelectedItem().toString()) {
                                case "=":
                                case "(=)/Equals":
                                    operator = Constants.EQUALS_OPERATOR;
                                    break;
                                case ">":
                                    operator = Constants.GREATER_OPERATOR;
                                    break;
                                case "<":
                                    operator = Constants.SMALLER_OPERATOR;
                                    break;
                                case ">=":
                                    operator = Constants.GREATER_EQUALS_OPERATOR;
                                    break;
                                case "<=":
                                    operator = Constants.SMALLER_EQUALS_OPERATOR;
                                    break;
                                case "Like":
                                    operator = Constants.LIKE_OPERATOR;
                                    break;
                                default:
                                    operator = -1;
                                    break;
                            }
                            if (valueTextInputLayout.getVisibility() == View.VISIBLE) {
                                value = valueEditText.getText().toString();
                            } else {
                                value = valueSpinner.getSelectedItem().toString();
                                //Log.i("MyConstraintDialog", valueSpinner.getSelectedItem().toString());
                            }
                            mOnSaveButtonClickedListener.onSaveButtonClicked(value, operator);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.CancelButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setView(view);

        operatorSpinner = (Spinner) view.findViewById(R.id.operatorSpinner);
        valueSpinner = (Spinner) view.findViewById(R.id.valueSpinner);
        valueEditText = (EditText) view.findViewById(R.id.valueEditText);
        valueTextInputLayout = (TextInputLayout) view.findViewById(R.id.valueTextInputLayout);

        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shouldPositiveButtonEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shouldPositiveButtonEnabled();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapter;
        switch (reflectFieldType) {
            case ReflectMTypes.STRING:
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stringOperators);
                valueEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case ReflectMTypes.BOOLEAN:
                isBoolean = true;
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, charOrBooleanOperators);
                break;
            case ReflectMTypes.CHAR:
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, charOrBooleanOperators);
                break;
            default:
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numbersOperators);
                if (reflectFieldType.equals(ReflectMTypes.UTILDATE) || reflectFieldType.equals(ReflectMTypes.SQLDATE)) {
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
                String selectedOperator = parent.getItemAtPosition(position).toString();
                if ((selectedOperator.equals(getString(R.string.EqualsSign)) || selectedOperator.equals(getString(R.string.Equals))) && (reflectFieldType.equalsIgnoreCase(ReflectMTypes.STRING) || reflectFieldType.equalsIgnoreCase(ReflectMTypes.CHAR))) {
                    valueTextInputLayout.setVisibility(View.INVISIBLE);
                    valueSpinner.setVisibility(View.VISIBLE);

                    if (fieldValues == null) {
                        new LoadFieldValues().execute();
                    } else {
                        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fieldValues);
                        valueSpinner.setAdapter(adapter);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }
                } else if (isBoolean) {
                    valueTextInputLayout.setVisibility(View.INVISIBLE);
                    valueSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{getString(R.string.TRUE), getString(R.string.FALSE)});
                    valueSpinner.setAdapter(adapter);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                } else {
                    valueTextInputLayout.setVisibility(View.VISIBLE);
                    valueSpinner.setVisibility(View.INVISIBLE);
                }
                shouldPositiveButtonEnabled();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Create the AlertDialog object and return it
        mAlertDialog = builder.create();
        return mAlertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void shouldPositiveButtonEnabled() {
        if (mAlertDialog != null) {
            Button positiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (((valueEditText.getText().length() > 0 && valueTextInputLayout.getVisibility() == View.VISIBLE) ||
                    (valueSpinner.getSelectedItem() != null && !valueSpinner.getSelectedItem().toString().equals(getString(R.string.Loading)) && valueSpinner.getVisibility() == View.VISIBLE)) &&
                    operatorSpinner.getSelectedItemPosition() != 0) {
                positiveButton.setEnabled(true);
            } else {
                positiveButton.setEnabled(false);
            }
        }
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
            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{getString(R.string.Loading)});
            valueSpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        protected Void doInBackground(Void... params) {

            Db4oSubClass db4oSubClass = new Db4oSubClass(ctx);
            ReflectClass reflectClass = db4oSubClass.reflectClass(reflectClassName);
            ReflectField reflectField = reflectClass.getDeclaredField(reflectFieldName);

            Query query = db4oSubClass.getDb().query();
            query.constrain(reflectClass);
            ObjectSet result = query.execute();
            fieldValues = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                fieldValues[i] = reflectField.get(result.get(i)).toString();
            }
            db4oSubClass.CloseDB();
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
        void onSaveButtonClicked(String value, int operator);
    }
}
