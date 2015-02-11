package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfilePictureActivity extends Activity implements View.OnClickListener {

    final static int PICK_IMAGES = 0;

    boolean isGlobalLayoutInitialSetup = true;
    CameraPreview mCameraPreview = null;
    FrameLayout mDisplayView;

    private RelativeLayout mRootLayout;
    private RelativeLayout mMaskView;
    private TextView mTopText;
    private RelativeLayout mBottomPortion;
    private RelativeLayout mSnapBtnsLayout;
    private ImageView mSnapBtn;
    private RelativeLayout mYesNoBtnsLayout;
    private RelativeLayout mDiscardLayout;
    private ImageView mDiscardBtn;
    private ImageView mUseBtn;
    private TextView mAlbumBtn;
    private ImageView mResultView;
    private Bitmap mBitMapImage;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2015-02-05 13:37:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mRootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        mMaskView = (RelativeLayout) findViewById(R.id.maskView);
        mTopText = (TextView) findViewById(R.id.topText);
        mBottomPortion = (RelativeLayout) findViewById(R.id.bottomPortion);
        mSnapBtnsLayout = (RelativeLayout) findViewById(R.id.snapBtnsLayout);

        mSnapBtn = (ImageView) findViewById(R.id.snapBtn);
        mSnapBtn.setOnClickListener(this);

        mYesNoBtnsLayout = (RelativeLayout) findViewById(R.id.yesNoBtnsLayout);
        mDiscardLayout = (RelativeLayout) findViewById(R.id.discardLayout);

        mDiscardBtn = (ImageView) findViewById(R.id.discardBtn);
        mDiscardBtn.setOnClickListener(this);

        mUseBtn = (ImageView) findViewById(R.id.useBtn);
        mUseBtn.setOnClickListener(this);

        mAlbumBtn = (TextView) findViewById(R.id.albumBtn);
        mAlbumBtn.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        findViews();

        GlobalHandler.turnOffStatusBar(this);
        setupCamera();
    }

    @Override
    public void onResume() {

        super.onResume();

        if (mCameraPreview != null)
            mCameraPreview.restartCamera();

    }

    public void onPause(){
        super.onPause();

//        if (mCameraPreview != null)
//            mCameraPreview.releaseCamera();

    }

    @Override
    public void onBackPressed() {
    }

    void setupCamera() {

        getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isGlobalLayoutInitialSetup) {
                    isGlobalLayoutInitialSetup = false;

                    mDisplayView = new FrameLayout(ProfilePictureActivity.this);
                    mDisplayView.setBackgroundColor(Color.BLACK);
                    int w = (int) (GlobalHandler.getScreenWidth(ProfilePictureActivity.this) * 0.9);
                    int h = (int) (((float) w / 9) * 16);

                    RelativeLayout.LayoutParams frameLayoutParams = new RelativeLayout.LayoutParams(w, h);
                    frameLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    mDisplayView.setLayoutParams(frameLayoutParams);
                    mRootLayout.addView(mDisplayView);

                    float displayHeight = ((float) w / 215) * 154;
                    mCameraPreview = new CameraPreview(getApplicationContext(), ProfilePictureActivity.this);
                    mCameraPreview.mDisplayWidth = w;
                    mCameraPreview.mDisplayHeight = displayHeight;

                    //calculate the start Y of all component
                    int startY = (int) (GlobalHandler.getScreenHeight(ProfilePictureActivity.this) * 0.05);

                    mDisplayView.setY(startY + GlobalHandler.convertDpToPixel(ProfilePictureActivity.this, 102));
                    mTopText.setY((mDisplayView.getY() - mTopText.getHeight()) / 2);
                    mMaskView.setY(mDisplayView.getY() + displayHeight);
                    mBottomPortion.setY(mMaskView.getY() + GlobalHandler.convertDpToPixel(ProfilePictureActivity.this, 6));

                    mMaskView.bringToFront();

                    mBottomPortion.bringToFront();

                    //Calculate the diff on image top
                    //This is used to crop result image
                    Rect rect = GlobalHandler.getVisibleScreenRect(ProfilePictureActivity.this);
                    int barHeight = GlobalHandler.getNavigationBarHeight(ProfilePictureActivity.this);
                    int diff = (int) mDisplayView.getY() + h - rect.height() - barHeight;
                    mCameraPreview.mDiff = diff;

                    mResultView = new ImageView(ProfilePictureActivity.this);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, (int) displayHeight);
                    mResultView.setLayoutParams(params);
                    mResultView.setAdjustViewBounds(true);
                    mResultView.setBackgroundColor(Color.WHITE);
                    mDisplayView.addView(mResultView);
                    mDisplayView.addView(mCameraPreview);

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_picture, menu);
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

    MediaPlayer mShootMP = null;

    public void shootSound() {
        AudioManager meng = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(getBaseContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShootMP != null)
                mShootMP.start();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.snapBtn:
                shootSound();

                mCameraPreview.capture(mResultView);
                mSnapBtnsLayout.setVisibility(View.INVISIBLE);
                mYesNoBtnsLayout.setVisibility(View.VISIBLE);
                mAlbumBtn.setVisibility(View.INVISIBLE);

                //show result view animation
//                mResultView.setAlpha(0.0f);
//                mCameraPreview.setAlpha(0);
//                mResultView.animate().setDuration(300).alpha(1.0f);

                break;

            case R.id.useBtn:
                mBitMapImage = ((BitmapDrawable) mResultView.getDrawable()).getBitmap();
//                GlobalHandler.saveBitmap(this, mBitMapImage, GlobalHandler.PROFILE_PIC);
                GlobalHandler.profileBitmap = mBitMapImage;

                if (GlobalHandler.isTabletSize(this)) {
                    //tablet
                    Intent intent = new Intent(ProfilePictureActivity.this, StartDabbing.class);
                    startActivity(intent);
                } else {
                    //smart phone
                    Intent intent = new Intent(ProfilePictureActivity.this, VerifyPhone.class);
                    startActivity(intent);

                }
                break;

            case R.id.discardBtn:
                mYesNoBtnsLayout.setVisibility(View.INVISIBLE);
                mSnapBtnsLayout.setVisibility(View.VISIBLE);
                mCameraPreview.setAlpha(1);

                mAlbumBtn.setVisibility(View.VISIBLE);
                mDisplayView.bringToFront();
                mCameraPreview.bringToFront();
                mMaskView.bringToFront();
                mBottomPortion.bringToFront();
                mCameraPreview.resumePreview();
                break;

            case R.id.albumBtn:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGES);
                break;
        }

    }

    public Bitmap rotate(Bitmap in, int angle, boolean mirror) {
        Matrix mat = new Matrix();

        if (mirror)
            mat.preScale(-1, 1);

        mat.postRotate(angle);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case PICK_IMAGES:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    mBitMapImage = BitmapFactory.decodeFile(filePath);

                    int width = mResultView.getWidth();
                    int height = (int) (mBitMapImage.getHeight() * ((float) mResultView.getWidth() / mBitMapImage.getWidth()));
                    mBitMapImage = Bitmap.createScaledBitmap(mBitMapImage, width, height, false);

                    //crop the image
                    int startY = (mBitMapImage.getHeight() - mResultView.getHeight()) / 2;
                    if (startY > 0)
                        mBitMapImage = Bitmap.createBitmap(mBitMapImage, 0, startY, mBitMapImage.getWidth(), mResultView.getHeight());

                    mDisplayView.bringChildToFront(mResultView);
                    mSnapBtnsLayout.setVisibility(View.INVISIBLE);
                    mYesNoBtnsLayout.setVisibility(View.VISIBLE);
                    mAlbumBtn.setVisibility(View.INVISIBLE);
                    mResultView.setImageBitmap(mBitMapImage);
                }
        }
    }


}
