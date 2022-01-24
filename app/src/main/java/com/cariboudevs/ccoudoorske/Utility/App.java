package com.cariboudevs.ccoudoorske.Utility;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;


public class App extends Application {
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    private static final String TAG = "CCOUT";
    @Override
    public void onCreate() {
        super.onCreate();





    }

    // Gloabal declaration of variable to use in whole app

    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false

    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }




}
