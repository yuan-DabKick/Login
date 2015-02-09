package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by developer3 on 1/30/15.
 */
public class GlobalHandler {

    final static public String USER_NAME = "userName";
    final static public String USER_PASSWORD = "userPassword";
    final static public String USER_EMAIL = "userEmail";
    final static public String PROFILE_PIC = "profile_pic.png";



    static public int getNavigationBarHeight(Activity activity)
    {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    static public void turnOffStatusBar(Activity activity){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    static public float getScreenWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    static public float getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    static public Rect getVisibleScreenRect(Activity activity){
        Rect rectangle= new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle;
    }

    static public void showKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

    }

    static public void dismissKeyboard(Activity activity, View view) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static public float convertPixelToDp(Activity activity, float pixel) {
        float density = activity.getResources().getDisplayMetrics().density;
        float dp = pixel / density;

        return dp;
    }

    static public float convertDpToPixel(Activity activity, float dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        float pixel = dp * density;

        return pixel;
    }

    static public boolean isTabletSize(Activity activity) {

        float width = getScreenWidth(activity);
        float height = getScreenHeight(activity);

        width = convertPixelToDp(activity, width);
        height = convertPixelToDp(activity, height);

        float min = Math.min(width, height);

        if (min >= 600)
            return true;

        return false;
    }

    public static void saveBitmap(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("test", "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("test", "io exception");
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(Context context, String picName) {
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        } catch (FileNotFoundException e) {
            Log.d("test", "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("test", "io exception");
            e.printStackTrace();
        }
        return b;
    }

    public static void saveString(Activity c,String filename,  String string) {
        try {
            FileOutputStream fos = c.openFileOutput(filename, Context.MODE_PRIVATE);

            try {
                fos.write(string.getBytes());
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String readString(Activity c,String filename)  {

        StringBuffer buffer = new StringBuffer();

        try {
            FileInputStream fis = c.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            try {
                String s;
                if (fis != null) {
                    while ((s = reader.readLine()) != null) {
                        buffer.append(s + "\n");
                    }
                }
                fis.close();
                return buffer.toString();

            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }



    static public void setUnderLine(TextView view)
    {
        view.setPaintFlags(view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}



