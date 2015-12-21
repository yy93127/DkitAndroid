package cn.runhe.dkitandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import cn.runhe.dkitandroid.R;
import cn.runhe.dkitandroid.domain.LoginUserInfo;
import cn.runhe.dkitandroid.utils.Constant;
import cn.runhe.dkitandroid.utils.HttpUtil;
import cn.runhe.dkitandroid.utils.LogUtil;
import cn.runhe.dkitandroid.utils.SPUtil;
import cn.runhe.dkitandroid.utils.ToastUtil;

/**
 * A login screen that offers login via usernames/password.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView userNameView;
    private EditText mPasswordView;
    private View mProgressView;
//    private View mLoginFormView;
    private HttpUtil httpUtil;
    private Gson gson;
    private GetDataTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String value = SPUtil.getString(LoginActivity.this, Constant.SP_NAME_LOGIN);
        if (!TextUtils.isEmpty(value)) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);

        httpUtil = new HttpUtil();
        gson = new Gson();
        // Set up the login form.
        userNameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

//        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.loading_dialog);
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

        // Reset errors.
        userNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = userNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(username, password);
            RequestBody body = new FormEncodingBuilder().add("pageNum", "1").add("pageSize", "999").build();
            mTask = new GetDataTask(Constant.QUERY_CLIENT, body);
            mAuthTask.execute((Void) null);
            mTask.execute((Void[]) null);
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        private String url;
        private RequestBody body;

        public GetDataTask(String url, RequestBody body) {
            this.url = url;
            this.body = body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                result = httpUtil.requestServer(url, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mTask = null;
            SPUtil.putString(LoginActivity.this,Constant.SP_USER_CLIENT,result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                RequestBody body = new FormEncodingBuilder().add("username", mUsername)
                        .add("password", mPassword).build();
                result = httpUtil.requestServer(Constant.LOGIN, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            mProgressView.setVisibility(View.GONE);
            LogUtil.i(LoginActivity.this, "登陆返回：" + result);
            LoginUserInfo loginUserInfo = gson.fromJson(result.substring(1, result.length() - 1), LoginUserInfo.class);
            if (null != loginUserInfo) {
                ToastUtil.showToast(LoginActivity.this, "登陆成功");
                SPUtil.putString(LoginActivity.this, Constant.SP_NAME_LOGIN, result.substring(1, result.length() - 1));
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressView.setVisibility(View.GONE);
        }
    }
}

