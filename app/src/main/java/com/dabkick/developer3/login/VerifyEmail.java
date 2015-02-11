package com.dabkick.developer3.login;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class VerifyEmail extends Activity implements View.OnClickListener{

    private RelativeLayout mVerifyLayout;
    private ImageView mVerifyDelete;
    private TextView mEmail;
    private LinearLayout mGoBar;
    private RelativeLayout mGoButton;
    private ImageView mArrowImageView;
    private Thread mArrowThread;

    private void findViews() {
        mVerifyLayout = (RelativeLayout)findViewById( R.id.verifyLayout );
        mVerifyDelete = (ImageView)findViewById( R.id.verifyDelete );
        mVerifyDelete.setOnClickListener(this);
        mEmail = (TextView)findViewById( R.id.email );
        mGoBar = (LinearLayout)findViewById( R.id.goBar );
        mGoButton = (RelativeLayout)findViewById( R.id.goButton );
        mGoButton.setOnClickListener(this);
        mArrowImageView = (ImageView)findViewById(R.id.arrowImage);
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        findViews();

        String email = GlobalHandler.readString(this,GlobalHandler.USER_EMAIL);
        mEmail.setText(email);

        mArrowThread = GlobalHandler.arrowAnimation(this,mArrowImageView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify_email, menu);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.verifyDelete:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                VerifyEmail.this.finish();
                break;
            case R.id.goButton:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                break;
        }
    }
}
