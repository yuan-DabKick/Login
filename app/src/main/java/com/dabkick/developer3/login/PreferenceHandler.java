package com.dabkick.developer3.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by developer3 on 2/7/15.
 */
public class PreferenceHandler {

    static final String USER_DETAIL = "USER_DETAIL";

    static public SharedPreferences.Editor getUserDetailEditor(Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences(USER_DETAIL,activity.MODE_PRIVATE);
        return preferences.edit();
    }

    static public void setUserEmail(Activity activity,String s)
    {
        SharedPreferences.Editor editor = getUserDetailEditor(activity);
        editor.putString("email",s);
        editor.commit();
    }

    static public String getUserEmail(Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences(USER_DETAIL, activity.MODE_PRIVATE);
        return preferences.getString("email","");
    }

    static public void setUserPassword(Activity activity,String s)
    {
        SharedPreferences.Editor editor = getUserDetailEditor(activity);
        editor.putString("password",s);
        editor.commit();
    }

    static public String getUserPassword(Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences(USER_DETAIL,activity.MODE_PRIVATE);
        return preferences.getString("password","");
    }

    static public void setUserName(Activity activity,String s)
    {
        SharedPreferences.Editor editor = getUserDetailEditor(activity);
        editor.putString("username",s);
        editor.commit();
    }

    static public String getUserName(Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences(USER_DETAIL,activity.MODE_PRIVATE);
        return preferences.getString("username","");
    }

    static public void setLogin(Activity activity,boolean value)
    {
        SharedPreferences.Editor editor = getUserDetailEditor(activity);
        editor.putBoolean("login", value);
        editor.commit();
    }

    static public boolean isLogin(Activity activity)
    {
        SharedPreferences preferences = activity.getSharedPreferences(USER_DETAIL, activity.MODE_PRIVATE);
        return preferences.getBoolean("login", false);
    }

    static public void setFromJason(Activity activity,JSONObject json) {
        SharedPreferences.Editor editor = getUserDetailEditor(activity);

        Iterator iterator = json.keys();
        while (iterator.hasNext())
        {
            String key;
            String value;
            try {
                key = (String)iterator.next();
                value = (String) json.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
                break;
            }
            editor.putString(key,value);
        }
        editor.commit();
    }

}
