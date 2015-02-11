package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class VerifyPhone extends Activity implements View.OnClickListener {

    private RelativeLayout mVerifyLayout;
    private LinearLayout mGoBar;
    private RelativeLayout mGoButton;
    private RelativeLayout mVerifying;
    private RelativeLayout mVerified;
    private RelativeLayout mNoLayout;
    private ImageView mNoImage;
    private RelativeLayout mYesLayout;
    private ImageView mYesImage;
    private RelativeLayout mGotItBox;
    private TextView mGotItBtn;
    private ImageView mVerifyingDelete;
    private ImageView mVerifyDelete;
    private RelativeLayout mContentLayout;
    private ImageView mArrowImageView;
    private Thread mArrowThread;

    private void findViews() {
        mVerifyLayout = (RelativeLayout)findViewById( R.id.verifyLayout );
        mVerifyLayout.setOnClickListener(this);

        mGoBar = (LinearLayout)findViewById( R.id.goBar );
        mGoBar.setOnClickListener(this);

        mGoButton = (RelativeLayout)findViewById( R.id.goButton );
        mGoButton.setOnClickListener(this);

        mVerifying = (RelativeLayout)findViewById( R.id.verifying );
        mVerifying.setOnClickListener(this);

        mVerified = (RelativeLayout)findViewById( R.id.verified );
        mVerified.setOnClickListener(this);

        mNoLayout = (RelativeLayout)findViewById( R.id.noLayout );
        mNoLayout.setOnClickListener(this);

        mNoImage = (ImageView)findViewById( R.id.noImage );

        mYesLayout = (RelativeLayout)findViewById( R.id.yesLayout );
        mYesLayout.setOnClickListener(this);

        mYesImage = (ImageView)findViewById( R.id.yesImage );

        mGotItBox = (RelativeLayout)findViewById( R.id.gotItBox );
        mGotItBox.setOnClickListener(this);

        mGotItBtn = (TextView)findViewById( R.id.gotItBtn );
        mGotItBtn.setOnClickListener(this);

        mVerifyDelete = (ImageView)findViewById(R.id.verifyDelete);
        mVerifyDelete.setOnClickListener(this);

        mVerifyingDelete = (ImageView)findViewById(R.id.verifyingDelete);
        mVerifyingDelete.setOnClickListener(this);

        mContentLayout = (RelativeLayout)findViewById(R.id.contentLayout);

        mArrowImageView = (ImageView)findViewById(R.id.arrowImage);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        findViews();

        mArrowThread = GlobalHandler.arrowAnimation(this,mArrowImageView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify_phone, menu);
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
    public void onBackPressed(){
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.goButton:
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+ "9512756463")));
                mVerifying.setVisibility(View.VISIBLE);
                mVerifyLayout.setVisibility(View.INVISIBLE);
                GlobalHandler.finishArrowAnimation(mArrowThread);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);

                            if (mVerifying.getVisibility() == View.VISIBLE) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVerifying.setVisibility(View.INVISIBLE);
                                        mVerified.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;

            case R.id.yesLayout:
                mGotItBox.setVisibility(View.VISIBLE);
                mYesLayout.setClickable(false);
                mNoLayout.setClickable(false);
                break;

            case R.id.noLayout:
                mVerifyLayout.setVisibility(View.VISIBLE);
                mVerified.setVisibility(View.INVISIBLE);
                mArrowThread = GlobalHandler.arrowAnimation(VerifyPhone.this,mArrowImageView);
                break;

            case R.id.gotItBtn:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                Intent intent = new Intent(VerifyPhone.this,StartDabbing.class);
                this.startActivity(intent);
                this.finish();
                break;

            case R.id.verifyDelete:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                intent = new Intent(VerifyPhone.this,StartDabbing.class);
                this.startActivity(intent);
                this.finish();
                break;

            case R.id.verifyingDelete:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                mVerifying.setVisibility(View.INVISIBLE);
                mVerifyLayout.setVisibility(View.VISIBLE);
                break;


        }
    }
}
