package com.example.finalthesis.db4o_the_project.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalthesis.db4o_the_project.R;
import com.example.finalthesis.db4o_the_project.RecursivePrint;

import java.util.List;

public class ReflectClassesResultsRecyclerViewAdapter extends RecyclerView.Adapter<ReflectClassesResultsRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final RecursivePrint.OnReflectClassItemClickedListener mListener;

    public ReflectClassesResultsRecyclerViewAdapter(List<String> items, RecursivePrint.OnReflectClassItemClickedListener listener) {
        this.mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recursive_field_item, parent, false);
        return new ViewHolder(view);
    }
//----------------------------------
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String textToShow = mValues.get(position);
        holder.mItem = textToShow;//deixnei to epilegmeno antikeimeno
        holder.mNameView.setText(textToShow);//probalei to antikeimeno pou 8es na emfaniseis

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemClicked(position);
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
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.itemName);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
