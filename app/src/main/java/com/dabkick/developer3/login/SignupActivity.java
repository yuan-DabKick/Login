package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {

    //Set the login server address here
    final String serverAddress = "http://54.186.104.146/login.php";

    enum WarningMode {
        SHOW_ALL_WARNING,
        SHOW_EMAIL_WARNING,
        SHOW_PASSWORD_WARNING,
        SHOW_NAME_WARNING
    }

    enum EditLocation {
        NAME,
        EMAIL,
        PASSWORD
    }

    private RelativeLayout mRelativeLayout;
    private ImageView mDismissBtn;
    private ScrollView mLoginScrollView;
    private EditText mNameField;
    private TextView mNameWarning;
    private EditText mEmailField;
    private TextView mEmailWarning;
    private EditText mPasswordField;
    private TextView mPasswordWarning;
    private LinearLayout mGoBar;
    private TextView mTermOfServiceLink;
    private LinearLayout mGoButton;
    private ImageView mArrowImageView;

    private EditLocation mEditLocation;

    private Thread mArrowThread;

    private void findViews() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        mDismissBtn = (ImageView) findViewById(R.id.dismissBtn);
        mDismissBtn.setOnClickListener(this);

        mLoginScrollView = (ScrollView) findViewById(R.id.loginScrollView);

        mNameField = (EditText) findViewById(R.id.nameField);
        mNameField.setOnClickListener(this);
        mNameField.setOnFocusChangeListener(this);
        mNameWarning = (TextView) findViewById(R.id.nameWarning);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mEmailField.setOnFocusChangeListener(this);
        mEmailField.setOnClickListener(this);
        mEmailWarning = (TextView) findViewById(R.id.emailWarning);

        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mPasswordField.setOnFocusChangeListener(this);
        mPasswordField.setOnClickListener(this);
        mPasswordWarning = (TextView) findViewById(R.id.passwordWarning);

        mGoBar = (LinearLayout) findViewById(R.id.goBar);

        mTermOfServiceLink = (TextView) findViewById(R.id.termOfServiceLink);
        mTermOfServiceLink.setOnClickListener(this);

        mGoButton = (LinearLayout) findViewById(R.id.goButton);
        mGoButton.setOnClickListener(this);

        mArrowImageView = (ImageView) findViewById(R.id.arrowImage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViews();

        mEditLocation = EditLocation.NAME;

        //set the term of service underline feature
        GlobalHandler.setUnderLine(mTermOfServiceLink);

        //keep soft keyboard open
        View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                InputMethodManager inputMethodManager = (InputMethodManager) SignupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                bringGoBarToVisible();
            }
        });

        mArrowThread = GlobalHandler.arrowAnimation(this, mArrowImageView);

    }



    void bringGoBarToVisible() {
        Rect rect = GlobalHandler.getVisibleScreenRect(this);
        mGoBar.setY(rect.height() - mGoBar.getHeight());

    }

    @Override
    public void onDestroy(){
        super.onDestroy();



    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.dismissBtn:

                GlobalHandler.finishArrowAnimation(mArrowThread);

                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                SignupActivity.this.startActivity(intent);
                finish();
                break;
            case R.id.goButton:
                goBtnClickAction();
                break;
            case R.id.forgotTextView:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                intent = new Intent(SignupActivity.this, ForgotPassword.class);
                SignupActivity.this.startActivity(intent);
                break;
        }
    }

    void requestFocus(final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.nameField:
                if (hasFocus) {
                    if (mEditLocation == EditLocation.EMAIL)
                        checkToShowWarning(WarningMode.SHOW_EMAIL_WARNING);
                    else if (mEditLocation == EditLocation.PASSWORD)
                        checkToShowWarning(WarningMode.SHOW_PASSWORD_WARNING);

                    mEditLocation = EditLocation.NAME;
                }

                break;
            case R.id.emailField:

                if (hasFocus) {

                    if (mEditLocation == EditLocation.PASSWORD)
                        checkToShowWarning(WarningMode.SHOW_PASSWORD_WARNING);

                    checkToShowWarning(WarningMode.SHOW_NAME_WARNING);
                    if (!isValidName(mNameField.getText().toString())) {
                        requestFocus(mNameField);
                        return;
                    }

                    mEditLocation = EditLocation.EMAIL;
                }
                break;
            case R.id.passwordField:
                if (hasFocus) {

                    if (mEditLocation == EditLocation.NAME)
                        checkToShowWarning(WarningMode.SHOW_NAME_WARNING);

                    if (mEditLocation == EditLocation.EMAIL)
                        checkToShowWarning(WarningMode.SHOW_EMAIL_WARNING);

                    if (!isValidName(mNameField.getText().toString())) {
                        requestFocus(mNameField);
                        return;
                    }
                    if (!isValidEmail(mEmailField.getText().toString())) {
                        requestFocus(mEmailField);
                        return;
                    }

                    mEditLocation = EditLocation.PASSWORD;

                    mGoBar.setVisibility(View.VISIBLE);

                    //if forgot password text is blocked , we should scroll up
                    float passwordBtnY = mPasswordField.getY() - mLoginScrollView.getScrollY() + mPasswordField.getHeight();
                    float diff = passwordBtnY - mGoBar.getY();
                    if (diff > 0 || Math.abs(diff) < 2 * mPasswordField.getHeight()) {
                        float move = 2 * mPasswordField.getHeight() + diff;
                        mLoginScrollView.smoothScrollBy(0, (int) move);
                    }

                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void goBtnClickAction() {
        EditText editText = null;
        if ((editText = checkToShowWarning(WarningMode.SHOW_ALL_WARNING)) == null || true) {

            //connect to server
            String username = mNameField.getText().toString();
            String userPassword = mPasswordField.getText().toString();
            String userEmail = mEmailField.getText().toString();

            //set the parameters
            JSONObject Params = new JSONObject();
            try {
                Params.put("url", serverAddress);
                Params.put("name", username);
                Params.put("password", userPassword);
                Params.put("email", userEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new SignupTask().execute(Params);

        } else {
            editText.requestFocus();
        }
    }

    public boolean isValidEmail(String emailString) {
        EmailValidator validator = new EmailValidator();

        return emailString.length() > 3;
        //return validator.validate(emailString);
    }

    public boolean isValidPassword(String passwordString) {
        return !passwordString.isEmpty();
    }

    public boolean isValidName(String nameString) {
        return nameString.length() >= 2 ? true : false;
    }

    //return first warning field, return null if no warning
    private EditText checkToShowWarning(WarningMode mode) {

        EditText firstWarningField = null;
        if (!isValidName(mNameField.getText().toString())) {
            if (mode == WarningMode.SHOW_NAME_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mNameWarning.setVisibility(View.VISIBLE);

            firstWarningField = mNameField;
        } else {
            if (mode == WarningMode.SHOW_NAME_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mNameWarning.setVisibility(View.INVISIBLE);
        }

        if (!isValidEmail(mEmailField.getText().toString())) {
            if (mode == WarningMode.SHOW_EMAIL_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mEmailWarning.setVisibility(View.VISIBLE);
            if (firstWarningField == null)
                firstWarningField = mEmailField;
        } else {
            if (mode == WarningMode.SHOW_EMAIL_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mEmailWarning.setVisibility(View.INVISIBLE);
        }

        if (!isValidPassword(mPasswordField.getText().toString())) {
            if (mode == WarningMode.SHOW_PASSWORD_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mPasswordWarning.setVisibility(View.VISIBLE);

            if (firstWarningField == null)
                firstWarningField = mPasswordField;
        } else {
            if (mode == WarningMode.SHOW_PASSWORD_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                mPasswordWarning.setVisibility(View.INVISIBLE);
        }

        return firstWarningField;

    }


    class SignupTask extends AsyncTask<JSONObject, Integer, Void> {
        @Override
        protected Void doInBackground(JSONObject... objects) {
            try {
                JSONObject object = objects[0];
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(object.getString("url"));
                object.remove("url");

                StringEntity se = new StringEntity(object.toString());
                httppost.setEntity(se);

                //make connection
                HttpResponse resp = httpclient.execute(httppost);

                String responseText = null;
                try {
                    responseText = EntityUtils.toString(resp.getEntity());

                    JSONObject jObject = null;
                    try {
                        //change response to json object
                        jObject = new JSONObject(responseText);

                        Log.d("test", (String) (jObject.get("status")));

                        String status = (String) (jObject.get("status"));
                        if (status == "ok" || true) {

                            Activity activity = SignupActivity.this;
                            PreferenceHandler.setLogin(activity, true);
                            PreferenceHandler.setUserName(activity, (String) object.get("name"));
                            PreferenceHandler.setUserEmail(activity, (String) object.get("email"));
                            PreferenceHandler.setUserPassword(activity, (String) object.get("password"));

                            GlobalHandler.finishArrowAnimation(mArrowThread);
                            Intent intent = new Intent(SignupActivity.this, ProfilePictureActivity.class);
                            SignupActivity.this.startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("test", e + "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}
