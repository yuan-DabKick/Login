package com.dabkick.developer3.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import java.io.IOException;
import java.util.List;

/**
 * Created by developer3 on 1/29/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    boolean mIsFrontCamera = true;
    ProfilePictureActivity mActivity;
    float mDisplayWidth;
    float mDisplayHeight;
    float mDiff;
    boolean initialSetup = true;

    @SuppressWarnings("deprecation")
    Camera mCamera = null;


    public CameraPreview(Context context, ProfilePictureActivity activity) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mActivity = activity;

    }

    @Override
    @SuppressWarnings("deprecation")
    public void surfaceCreated(SurfaceHolder holder) {
        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

        mCamera = Camera.open(cameraId);
        setFlashAuto();

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (initialSetup == false)
            return;
        initialSetup = false;

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @SuppressWarnings("deprecation")
    void capture(final ImageView resultView) {

        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                //mCamera.stopPreview();

                int width = mCamera.getParameters().getPreviewSize().width;
                int height = mCamera.getParameters().getPreviewSize().height;

                int []rgb = new int[width*height+1];
                YUV_NV21_TO_RGB(rgb, data, width, height);

                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bmp.setPixels(rgb, 0, width, 0, 0, width, height);
                bmp = rotate(bmp, 90, true);
                //scale to 19 : 6 ratio first
                bmp = Bitmap.createScaledBitmap(bmp,mActivity.mDisplayView.getWidth(),mActivity.mDisplayView.getHeight(),false);

                //crop the image
                bmp = Bitmap.createBitmap(bmp, 0, (int)mDiff, bmp.getWidth(), (int)mDisplayHeight);

                resultView.setImageBitmap(bmp);


                //resultView.setAlpha(1);

                //mActivity.mCameraPreview.animate().alpha(0).setDuration(4000);
                resultView.bringToFront();

//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mActivity.mCameraPreview.animate().alpha(0).setDuration(4000);
//                    }
//                });

                //resultView.animate().alpha(1);

            }
        });

    }

    public static void YUV_NV21_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) yuv[ci * width + cj]));
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int a0 = 1192 * (y - 16);
                int a1 = 1634 * (v - 128);
                int a2 = 832 * (v - 128);
                int a3 = 400 * (u - 128);
                int a4 = 2066 * (u - 128);

                int r = (a0 + a1) >> 10;
                int g = (a0 - a2 - a3) >> 10;
                int b = (a0 + a4) >> 10;

//                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
//                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
//                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    void resumePreview() {
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @SuppressWarnings("deprecation")
    void setFlashAuto() {
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(params.FLASH_MODE_AUTO);
        mCamera.setParameters(params);
    }

    @SuppressWarnings("deprecation")
    void setFlashOn() {
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(params.FLASH_MODE_TORCH);
        mCamera.setParameters(params);
    }

    @SuppressWarnings("deprecation")
    void setFlashOff() {
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(params.FLASH_MODE_OFF);
        mCamera.setParameters(params);
    }

    @SuppressWarnings("deprecation")
    void switchToFrontCamera() {
        mCamera.stopPreview();
        mCamera.release();

        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = Camera.open(cameraId);
        try {
            //this step is critical or preview on new camera will no know where to render to
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mIsFrontCamera = true;

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @SuppressWarnings("deprecation")
    void switchToBackCamera() {
        mCamera.stopPreview();
        mCamera.release();

        int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = Camera.open(cameraId);
        try {
            //this step is critical or preview on new camera will no know where to render to
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mIsFrontCamera = false;

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    boolean isFrontCamera() {
        return mIsFrontCamera;
    }

    @SuppressWarnings("deprecation")
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int h, int w) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public Bitmap rotate(Bitmap in, int angle, boolean mirror) {
        Matrix mat = new Matrix();

        if (mirror)
            mat.preScale(-1, 1);

        mat.postRotate(angle);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
    }

}
