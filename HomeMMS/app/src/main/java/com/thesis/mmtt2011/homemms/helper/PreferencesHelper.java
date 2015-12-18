package com.thesis.mmtt2011.homemms.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

/**
 * Created by Xpie on 11/3/2015.
 */
public class PreferencesHelper {
    //private static final String USER_PREFERENCES = "userPreferences";

    /*private static final String PREFERENCE_FIRST_NAME = USER_PREFERENCES + ".nameDisplay";
    private static final String PREFERENCE_LAST_INITIAL = USER_PREFERENCES + ".lastInitial";
    private static final String PREFERENCE_AVATAR = USER_PREFERENCES + ".avatar";*/

    private PreferencesHelper() {
        //no instance
    }

    /**
     * Writes a boolean firstRun to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param isFirstRun The isFirstRun to write.
     */
    public static void writeToPreferences(Context context, Boolean isFirstRun) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(ContantsHomeMMS.FIRST_RUN_PREFRENCES, isFirstRun);
        /*editor.putString(PREFERENCE_FIRST_NAME, player.getFirstName());
        editor.putString(PREFERENCE_LAST_INITIAL, player.getLastInitial());
        editor.putString(PREFERENCE_AVATAR, player.getAvatar().name());*/
        editor.apply();
    }

    public static void writeToPreferencesBoolean(Context context, Boolean aBoolean, String StrPref) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(StrPref, aBoolean);
        editor.apply();
    }

    public static void writeToPreferencesString(Context context, String string, String StrPref) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(StrPref, string);
        editor.apply();
    }

    public static boolean getIsFirstRun(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(ContantsHomeMMS.FIRST_RUN_PREFRENCES, true);
    }

    public static boolean getIsPreferenceBoolean(Context context, String StrPref) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(StrPref, true);
    }

    public static String getIsPreferenceString(Context context, String StrPref) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(StrPref, null);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(ContantsHomeMMS.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

}
