package com.example.finalthesis.db4o_the_project.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.ConstraintsActivity;
import com.example.finalthesis.db4o_the_project.R;

import java.util.List;

public class ReflectFieldsRecyclerViewAdapter extends RecyclerView.Adapter<ReflectFieldsRecyclerViewAdapter.ViewHolder> {

    private final List<ReflectField> mValues;
    //private boolean[] checkBoxArray;
    private boolean[] hasConstraint;
    private final ConstraintsActivity.OnListItemLongClickedListener mListener;

    public ReflectFieldsRecyclerViewAdapter(List<ReflectField> items, ConstraintsActivity.OnListItemLongClickedListener listener) {
        mValues = items;
        mListener = listener;
        //checkBoxArray = new boolean[mValues.size()];
        hasConstraint = new boolean[mValues.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reflect_field_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ReflectField reflectField = mValues.get(position);
        holder.mItem = reflectField;
        String textToShow;
        if (reflectField.getFieldType().isCollection()) {
            textToShow = reflectField.getName() + " : " + "Collection";
        } else {
            textToShow = reflectField.getName() + " : " + reflectField.getFieldType().getName();
        }
        holder.mNameView.setText(textToShow);
          /*
        holder.mCheckedView.setTag(reflectField);
        holder.mCheckedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                ReflectField reflectField = (ReflectField) checkBox.getTag();
               checkBoxArray[mValues.indexOf(reflectField)] = checkBox.isChecked();
            }
        });
        */
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemLongClicked(reflectField);
                }
                return true;
            }
        });
        if (hasConstraint[position]) {
            holder.mView.setBackgroundColor(Color.YELLOW);
            holder.mNameView.setTextColor(Color.BLACK);
        } else {
            holder.mView.setBackgroundColor(Color.TRANSPARENT);
            holder.mNameView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
/*
    public List<ReflectField> getSelectedItems() {
        List<ReflectField> selectedReflectFields = new ArrayList<>();
        for (int i = 0; i < checkBoxArray.length; i++) {
            if (checkBoxArray[i]) {
                selectedReflectFields.add(mValues.get(i));
            }
        }
        return selectedReflectFields;
    }
*/
    public void setHasConstraint(ReflectField reflectField, boolean hasConstraint) {
        this.hasConstraint[mValues.indexOf(reflectField)] = hasConstraint;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
      //  public final CheckBox mCheckedView;
        public final ImageView mImageView;
        public ReflectField mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.itemName);
            mImageView = (ImageView) view.findViewById(R.id.itemImage);
           // mCheckedView = (CheckBox) view.findViewById(R.id.itemCheckBox);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
