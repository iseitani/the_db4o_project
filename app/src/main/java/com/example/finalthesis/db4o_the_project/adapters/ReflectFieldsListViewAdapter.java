package com.example.finalthesis.db4o_the_project.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.finalthesis.db4o_the_project.R;

import java.util.List;


/*
    Atos einai o antaptoras pou xrisimopoiite gia tin emfanisei ton antikeimenon
    ReflectField sto ListView
 */

public class ReflectFieldsListViewAdapter extends ArrayAdapter<String> {

    List<String> userClasses;

    public ReflectFieldsListViewAdapter(Context context, List<String> items, List<String> userClasses) {
        super(context, 0, items);
        this.userClasses = userClasses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        String reflectField = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reflect_field_item, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.itemName);

        // Populate the data into the template view using the data object
        String[] stringArray = reflectField.split("-->");
        String reflectFieldType = stringArray[1].replace(" ","");
        if (userClasses.contains(reflectFieldType)) {
            String textToShow = stringArray[0] + "--><font color='#8bc34a'>" + reflectFieldType + "</font>";
            title.setText(Html.fromHtml(textToShow));
        } else {
            title.setText(reflectField);
        }

        // Return the completed view to render on screen
        return convertView;
    }


}
