package com.example.finalthesis.db4o_the_project;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.db4o.ObjectContainer;
import com.db4o.cs.Db4oClientServer;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView UsernameView;
    private AutoCompleteTextView UrlView;
    private AutoCompleteTextView PortView;
    private EditText PasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button FAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UsernameView = (AutoCompleteTextView) findViewById(R.id.LoginUser);
        UrlView = (AutoCompleteTextView) findViewById(R.id.LoginServerUser);
        PortView = (AutoCompleteTextView) findViewById(R.id.LoginPort);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        PasswordView = (EditText) findViewById(R.id.LoginPassword);
        FAQ=(Button)findViewById(R.id.FAQB);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        PortView.setInputType(InputType.TYPE_CLASS_NUMBER);
        FAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.FAQ)));
                    startActivity(intent);
            }
        });
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Store values at the time of the login attempt.
        String username = UsernameView.getText().toString();
        String password = PasswordView.getText().toString();
        String url = UrlView.getText().toString();
        int port = Integer.parseInt(PortView.getText().toString());
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UserLoginTask(username, password, url, port);
        mAuthTask.execute((Void) null);

    }

    //Everything here will be moved to strings file
    private void returnMessage(int msg, String UserNi, final String PasswordNi, final String ServerNi, final int PortNi) {
        String message = null;
        String title = null;
        final String uis = UserNi;
        final String pass = PasswordNi;
        final String serv = ServerNi;
        final int po = PortNi;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (msg == 0) {
            message = getString(R.string.LoginMSGOK);
            title = getString(R.string.LoginTitleOK);
            alertDialogBuilder.setNeutralButton(getString(R.string.OkButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(getString(R.string.UserN), uis);
                    editor.putString(getString(R.string.PasswordN), pass);
                    editor.putInt(getString(R.string.PortN), po);
                    editor.putString(getString(R.string.ServerN), serv);
                    editor.commit();
                    //Go the Main Activity
                    Intent intent = new Intent(LoginActivity.this, Initial.class);
                    startActivity(intent);
                }
                //
            });
        } else {
            message = getString(R.string.LoginMSGNAK);
            title = getString(R.string.LoginTitleNAK);
            alertDialogBuilder.setNeutralButton(getString(R.string.TryAgainButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do Nothing
                }
            });
        }
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mUser;
        private String mPassword;
        private String mServer;
        private int mPort;
        private int falseAlarm = 0;

        UserLoginTask(String user, String password, String server, int port) {
            mUser = user;
            mPassword = password;
            mServer = server;
            mPort = port;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ObjectContainer db = Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), mServer, mPort, mUser, mPassword);
                db.close();
                falseAlarm = 0;
            } catch (Exception ex) {
                falseAlarm = 1;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            returnMessage(falseAlarm, mUser, mPassword, mServer, mPort);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

