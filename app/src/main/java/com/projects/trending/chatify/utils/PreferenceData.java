package com.projects.trending.chatify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceData
{
    static final String PREF_LOGGEDIN_USER_EMAIL = "logged_in_uid";
    static final String PREF_USER_LOGGEDIN_STATUS = "logged_in_status";
    static final String PREF_USER_NAME = "logged_in_name" ;

    public static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoggedInUserUid(Context ctx, String uid)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGEDIN_USER_EMAIL, uid);
        editor.commit();
    }

    public  static void setPrefUserName(Context ctx , String name){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, name);
        editor.commit();
    }

    public static String getPrefUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getLoggedInUserUid(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER_EMAIL, "");
    }

    public static void setUserLoggedInStatus(Context ctx, boolean status)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.commit();
    }

    public static boolean getUserLoggedInStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    public static void clearLoggedInUserId(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_LOGGEDIN_USER_EMAIL);
        editor.remove(PREF_USER_LOGGEDIN_STATUS);
        editor.remove(PREF_USER_NAME);
        editor.commit();
    }
}