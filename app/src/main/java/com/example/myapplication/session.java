package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.myapplication.LoginActivity;


public class session {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "userName";
    private static final String PREF_First = "userFirst";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_First = "first";



    // Constructor
    public session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref = _context.getSharedPreferences(PREF_First, PRIVATE_MODE);
        editor = pref.edit();
        editor.putBoolean(IS_LOGIN, false);

    }

    /**
     * Create login session
     * */
    public void createLoginSession(long name,int first) {
        // Storing name in pref
        editor.putLong(KEY_NAME, name);
        editor.putInt(KEY_First,first);


        // commit changes
        editor.commit();
    }

    public void valideLoginSession() {
        // Storing name in pref
        editor.putBoolean(IS_LOGIN, true);


        // commit changes
        editor.commit();
    }

    public long getUserNames() {
        long usename = pref.getLong(KEY_NAME,0);
        return usename;
    }
    public long getUserfirst() {
        long first = pref.getInt(KEY_First,0);
        return first;
    }

    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     * */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Clear session details
     * */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
