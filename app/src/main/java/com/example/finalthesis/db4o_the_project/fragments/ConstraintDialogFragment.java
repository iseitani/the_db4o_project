package com.example.finalthesis.db4o_the_project.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.R;

public class ConstraintDialogFragment extends DialogFragment {

    private ReflectField reflectField;
    private Spinner operatorSpinner, valueSpinner;
    private EditText valueEditText;

    private static final String[] numbersOperators = new String[3];
    static {
        numbersOperators[0] = "=";
        numbersOperators[1] = ">";
        numbersOperators[2] = "<";
    }

    private static final String[] stringOperators = new String[2];
    static {
        stringOperators[0] = "=";
        stringOperators[1] = "Like";
    }

    public ConstraintDialogFragment(ReflectField reflectField) {
        this.reflectField = reflectField;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_constraint, null);
        builder.setTitle("Constraint for field: " + reflectField.getName())
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setView(view);

        operatorSpinner = (Spinner) view.findViewById(R.id.operatorSpinner);
        if (reflectField.getFieldType().getName().contains(".String")) {
            operatorSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stringOperators));
        } else {
            operatorSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, numbersOperators));
        }
        valueSpinner = (Spinner) view.findViewById(R.id.valueSpinner);
        valueEditText = (EditText) view.findViewById(R.id.valueEditText);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.operatorSpinner);
    }
}
