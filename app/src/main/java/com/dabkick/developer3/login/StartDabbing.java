package com.dabkick.developer3.login;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class StartDabbing extends Activity implements View.OnClickListener{

    Bitmap mImage;
    ImageView mProfileImage;

    String username;
    String email;

    private RelativeLayout mContentLayout;
    private ImageView mProfilePic;
    private TextView mUserName;
    private TextView mEmail;
    private LinearLayout mGoBar;
    private RelativeLayout mGoButton;
    private ImageView mArrowImageView;
    private Thread mArrowThread;

    private void findViews() {
        mContentLayout = (RelativeLayout)findViewById( R.id.contentLayout );
        mProfilePic = (ImageView)findViewById( R.id.profilePic );
        mUserName = (TextView)findViewById( R.id.userName );
        mEmail = (TextView)findViewById( R.id.email );
        mGoBar = (LinearLayout)findViewById( R.id.goBar );
        mGoButton = (RelativeLayout)findViewById( R.id.goButton );
        mArrowImageView = (ImageView)findViewById(R.id.arrowImage);
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_dabbing);

        findViews();

//        mImage = GlobalHandler.loadBitmap(this,GlobalHandler.PROFILE_PIC);
        mImage = GlobalHandler.profileBitmap;
        mProfileImage = (ImageView)findViewById(R.id.profilePic);
        mProfileImage.setImageBitmap(mImage);

        mUserName = (TextView)findViewById(R.id.userName);
        mEmail = (TextView)findViewById(R.id.email);

        username = PreferenceHandler.getUserName(this);
        email = PreferenceHandler.getUserEmail(this);

        mUserName.setText(username);
        mEmail.setText(email);

        mArrowThread = GlobalHandler.arrowAnimation(this,mArrowImageView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_dabbing, menu);
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
        switch (id){
            case R.id.goButton:
                GlobalHandler.finishArrowAnimation(mArrowThread);
                break;
        }
    }
}
