package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener,View.OnFocusChangeListener{

    //Set the login server address here
    //final String serverAddress = "http://54.186.104.146/login.php";
    String serverAddress = "http://54.92.132.48/android_php/androidValidateLogin.php";



    enum WarningMode {
        SHOW_ALL_WARNING,
        SHOW_EMAIL_WARNING,
        SHOW_PASSWORD_WARNING
    }

    enum EditLocation {
        NAME,
        EMAIL,
        PASSWORD
    }

    private RelativeLayout mRelativeLayout;
    private ImageView mDismissBtn;
    private ScrollView mLoginScrollView;
    private EditText mEmailField;
    private TextView mEmailWarning;
    private EditText mPasswordField;
    private TextView mPasswordWarning;
    private TextView mForgotTextView;
    private LinearLayout mGoBar;
    private TextView mTermOfServiceLink;
    private LinearLayout mGoButton;
    private ImageView mArrowImage;

    private EditLocation mEditLocation;

    private Thread mArrowThread;

    private void findViews() {
        mRelativeLayout = (RelativeLayout)findViewById( R.id.relativeLayout );

        mDismissBtn = (ImageView)findViewById( R.id.dismissBtn );
        mDismissBtn.setOnClickListener(this);

        mLoginScrollView = (ScrollView)findViewById( R.id.loginScrollView );

        mEmailField = (EditText)findViewById( R.id.emailField );
        mEmailField.setOnFocusChangeListener(this);
        mEmailWarning = (TextView)findViewById( R.id.emailWarning );

        mPasswordField = (EditText)findViewById( R.id.passwordField );
        mPasswordField.setOnFocusChangeListener(this);
        mPasswordWarning = (TextView)findViewById( R.id.passwordWarning );

        mForgotTextView = (TextView)findViewById( R.id.forgotTextView );
        mForgotTextView.setOnClickListener(this);

        mGoBar = (LinearLayout)findViewById( R.id.goBar );

        mTermOfServiceLink = (TextView)findViewById( R.id.termOfServiceLink );
        mTermOfServiceLink.setOnClickListener(this);

        mGoButton = (LinearLayout)findViewById( R.id.goButton );
        mGoButton.setOnClickListener(this);

        mArrowImage = (ImageView)findViewById(R.id.arrowImage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();

        GlobalHandler.setUnderLine(mTermOfServiceLink);

        //keep soft keyboard open
        View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                bringGoBarToVisible();
            }
        });

        mArrowThread = GlobalHandler.arrowAnimation(this,mArrowImage);
    }



    void bringGoBarToVisible(){
        Rect rect = GlobalHandler.getVisibleScreenRect(this);
        mGoBar.setY(rect.height()-mGoBar.getHeight());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.dismissBtn:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
                break;

            case R.id.goButton:
                goBtnAction();
                break;

            case R.id.forgotTextView:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                intent = new Intent(LoginActivity.this,ForgotPassword.class);
                LoginActivity.this.startActivity(intent);
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
        switch (id){
            case R.id.emailField:

                if (hasFocus) {
                    if (mEditLocation == EditLocation.PASSWORD)
                        checkToShowWarning(WarningMode.SHOW_PASSWORD_WARNING);

                    mEditLocation = EditLocation.EMAIL;
                }
                break;
            case R.id.passwordField:
                if (hasFocus) {

                    if (mEditLocation == EditLocation.EMAIL)
                        checkToShowWarning(WarningMode.SHOW_EMAIL_WARNING);

                    if (!isValidEmail(mEmailField.getText().toString())) {
                        requestFocus(mEmailField);
                        return;
                    }

                    mEditLocation = EditLocation.PASSWORD;

                    mGoBar.setVisibility(View.VISIBLE);

                    //if forgot password text is blocked , we should scroll up
                    float forgotBtnY = mForgotTextView.getY() - mLoginScrollView.getScrollY()+mForgotTextView.getHeight();
                    float diff = forgotBtnY - mGoBar.getY();
                    if (diff > 0 || Math.abs(diff) < 2 * mForgotTextView.getHeight()) {
                        float move = 2*mForgotTextView.getHeight() + diff;
                        mLoginScrollView.smoothScrollBy(0, (int) move);
                    }

                }
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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



    String appendUrlParams(String url,ArrayList<Pair> params)
    {
        if (!url.endsWith("?"))
            url+="?";

        for (Pair pair : params)
        {
            url += pair.first + "=" + pair.second + "&";
        }

        url = url.substring(0,url.length()-1);

        return url;
    }

    void goBtnAction(){
        EditText editText = null;
        if ((editText = checkToShowWarning(WarningMode.SHOW_ALL_WARNING)) == null) {
            //connect to server

            String userPassword = mPasswordField.getText().toString();
            String userEmail = mEmailField.getText().toString();

            userEmail = "balaji@dabkicktesting.com";
            userPassword = "asdfgf";
            ArrayList<Pair> list = new ArrayList<Pair>();
            list.add(new Pair("e",userEmail));
            list.add(new Pair("p",userPassword));
            list.add(new Pair("d","deviceid"));
            list.add(new Pair("t","token"));

            String url = appendUrlParams(serverAddress,list);

            new SignupTask().execute(url);
        } else
            editText.requestFocus();
    }

    public boolean isValidEmail(String emailString) {
        EmailValidator validator = new EmailValidator();

        return emailString.length() > 3;
        //return validator.validate(emailString);
    }

    public boolean isValidPassword(String passwordString) {
        return !passwordString.isEmpty();
    }

    //return first warning field, return null if no warning
    private EditText checkToShowWarning(WarningMode mode) {
        TextView emailWarning = (TextView) findViewById(R.id.emailWarning);
        TextView passwordWarning = (TextView) findViewById(R.id.passwordWarning);

        EditText emailField = (EditText) findViewById(R.id.emailField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);

        EditText firstWarningField = null;
        if (!isValidEmail(emailField.getText().toString())) {
            if (mode == WarningMode.SHOW_EMAIL_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                emailWarning.setVisibility(View.VISIBLE);
            firstWarningField = emailField;
        } else {
            if (mode == WarningMode.SHOW_EMAIL_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                emailWarning.setVisibility(View.INVISIBLE);
        }

        if (!isValidPassword(passwordField.getText().toString())) {
            if (mode == WarningMode.SHOW_PASSWORD_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                passwordWarning.setVisibility(View.VISIBLE);

            if (firstWarningField == null)
                firstWarningField = passwordField;
        } else {
            if (mode == WarningMode.SHOW_PASSWORD_WARNING || mode == WarningMode.SHOW_ALL_WARNING)
                passwordWarning.setVisibility(View.INVISIBLE);
        }

        return firstWarningField;

    }

    class SignupTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... objects) {
            try {
                //JSONObject object = objects[0];
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(objects[0]);

                //make connection
                HttpResponse resp = httpclient.execute(httpGet);

                String responseText = null;
                try {
                    responseText = EntityUtils.toString(resp.getEntity());


                    JSONObject jObject = null;
                    try {
                        //change response to json object
                        jObject = new JSONObject(responseText);

                        //Log.d("test", (String) (jObject.get("status")));

                        Activity activity = LoginActivity.this;
                        PreferenceHandler.setLogin(activity,true);
                        PreferenceHandler.setUserEmail(activity,mEmailField.getText().toString());
                        PreferenceHandler.setUserPassword(activity,mPasswordField.getText().toString());
                        PreferenceHandler.setFromJason(activity,jObject);

                        GlobalHandler.finishArrowAnimation(mArrowThread);

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
