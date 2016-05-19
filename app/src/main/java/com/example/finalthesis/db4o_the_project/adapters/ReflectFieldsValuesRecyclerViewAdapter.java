package com.example.finalthesis.db4o_the_project.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db4o.reflect.ReflectField;
import com.example.finalthesis.db4o_the_project.R;
import com.example.finalthesis.db4o_the_project.RecursivePrint;

import java.util.List;

public class ReflectFieldsValuesRecyclerViewAdapter extends RecyclerView.Adapter<ReflectFieldsValuesRecyclerViewAdapter.ViewHolder> {

    private final List<ReflectField> reflectFields;
    private final List<String> mValues;
    private final RecursivePrint.OnReflectFieldItemClickedListener mListener;

    public ReflectFieldsValuesRecyclerViewAdapter(List<String> items, List<ReflectField> reflectFields, RecursivePrint.OnReflectFieldItemClickedListener listener) {
        this.reflectFields = reflectFields;
        this.mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recursive_field_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ReflectField reflectField = reflectFields.get(position);
        final String value = mValues.get(position);
        holder.mItem = value;
        String textToShow = reflectField.getName() + " : " + value;
        holder.mNameView.setText(textToShow);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemClicked(value, reflectField);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
      //  public final ImageView mImageView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.itemName);
            //mImageView = (ImageView) view.findViewById(R.id.itemImage);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
