package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ForgotPassword extends Activity implements View.OnClickListener {

    private ImageView mDeleteBtn;
    private EditText mEmailField;
    private TextView mSendBtn;
    private RelativeLayout mBlackBoxWaiting;
    private RelativeLayout mBlackBoxOK;
    private TextView mOkBtn;
    private RelativeLayout mMaskView;

    private void findViews() {
        mDeleteBtn = (ImageView) findViewById(R.id.deleteBtn);
        mDeleteBtn.setOnClickListener(this);

        mEmailField = (EditText) findViewById(R.id.emailField);

        mSendBtn = (TextView) findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(this);

        mBlackBoxWaiting = (RelativeLayout) findViewById(R.id.blackBoxWaiting);
        mBlackBoxOK = (RelativeLayout) findViewById(R.id.blackBoxOK);

        mOkBtn = (TextView) findViewById(R.id.okBtn);
        mOkBtn.setOnClickListener(this);

        mMaskView = (RelativeLayout)findViewById(R.id.maskView);
        mMaskView.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViews();

        GlobalHandler.showKeyboard(this,mEmailField);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.sendBtn:
                mMaskView.setVisibility(View.VISIBLE);
                mBlackBoxWaiting.setVisibility(View.VISIBLE);
                GlobalHandler.dismissKeyboard(ForgotPassword.this,mEmailField);
                mEmailField.setClickable(false);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBlackBoxWaiting.getVisibility() == View.VISIBLE) {
                                        mBlackBoxOK.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;

            case R.id.okBtn:
                ForgotPassword.this.finish();
                break;

            case R.id.deleteBtn:
                ForgotPassword.this.finish();
                break;
        }
    }
}
