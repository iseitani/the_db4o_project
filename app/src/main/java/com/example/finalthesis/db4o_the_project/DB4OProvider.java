package com.example.finalthesis.db4o_the_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

import java.util.Arrays;
import java.util.List;

public class DB4OProvider extends Db4oHelper {

    private OnGetReflectClassesResponseListener mOnGetReflectClassesResponseListener;
    private static DB4OProvider provider = null;
    private Context context;

    //configure Db4oHelper by Passing Context.
    private DB4OProvider(Context ctx) {
        context = ctx;
    }

    public static DB4OProvider getInstance(Context ctx) {
        if (provider == null)
            provider = new DB4OProvider(ctx);
        return provider;
    }

    public void getReflectClasses(OnGetReflectClassesResponseListener listener) {
        mOnGetReflectClassesResponseListener = listener;
        new GetReflectClasses().execute();
    }

    public interface OnGetReflectClassesResponseListener {
        void onGetReflectClassesResponse(List<ReflectClass> reflectClasses);
    }

    class GetReflectClasses extends AsyncTask<Void, Void, Void> {
        List<ReflectClass> reflectClasses = null;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle("Loading data");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (db() != null) {
                ReflectClass[] reflectClassesArray = db().ext().reflector().knownClasses();
                reflectClasses = Arrays.asList(reflectClassesArray);
                close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mOnGetReflectClassesResponseListener != null) {
                mOnGetReflectClassesResponseListener.onGetReflectClassesResponse(reflectClasses);
                mOnGetReflectClassesResponseListener = null;
            }
            mProgressDialog.dismiss();
        }
    }
}