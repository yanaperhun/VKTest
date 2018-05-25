package com.vktest.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by seishu on 25.05.18.
 */

public class Prefs {

    private static String EMAILS_PREF = "email";
    private static String NAME_PREF = "name";
    private static String PHOTO_PREF = "photo";

    public static void saveUser(Context c, String email, String fullname, String photo) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        sp.edit().putString(EMAILS_PREF, email).apply();
        sp.edit().putString(NAME_PREF, fullname).apply();
        sp.edit().putString(PHOTO_PREF, photo).apply();
    }

    public static String getEmail(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(EMAILS_PREF, "");
    }

    public static String getFullName(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(NAME_PREF, "");
    }

    public static String getPhotoUrl(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(PHOTO_PREF, "");
    }


    //в идеале надо объединить этот метод и все методы получения данны
    public static boolean userWasLogined(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return !TextUtils.isEmpty(getEmail(c)) && !TextUtils.isEmpty(getPhotoUrl(c)) && !TextUtils.isEmpty(getFullName(c));
    }



}
