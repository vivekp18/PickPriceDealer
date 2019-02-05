package com.efunhub.starkio.pickpricedealer.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.efunhub.starkio.pickpricedealer.Activity.MainActivity;
import com.efunhub.starkio.pickpricedealer.Activity.WelcomeActivity;

import java.util.HashMap;

/**
 * Created by Admin on 08-12-2018.
 */

public class SessionManager {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context mContext;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PickPriceDealer";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // ID (make variable public to access from outside)
    public static final String KEY_ID = "dealer_id";

    public static final String KEY_IMAGE_URL = "image_url";

    // Constructor
    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create login session
     */
    public void createLoginSession(String id) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing ID in pref
        editor.putString(KEY_ID, id);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect guard to login page
     * Else won't do anything
     */

    public boolean checkDealerLoginStatus(){
        if (this.isLoggedIn()) {
            return true;
        }else{
            return false;
        }

    }

    public void checkLogin() {
        // Check login status
        if (this.isLoggedIn()) {

            //checkDealerStatus();

            // user is logged in redirect him to guard Main Activity
            Intent i = new Intent(mContext, MainActivity.class);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);


        } else {
            // guard is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, WelcomeActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
        }
    }

    /*public void storeRegIdInPref(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.commit();
    }
*/
    /*public HashMap<String, String> getRegIdPref() {
        HashMap<String, String> token = new HashMap<String, String>();

        // token
        token.put(KEY_FCM_TOKEN, pref.getString(KEY_FCM_TOKEN, null));

        return token;
    }*/

    /**
     * Get stored session data
     */
    public HashMap<String, String> getDealerDetails() {

        HashMap<String, String> dealer = new HashMap<String, String>();

        // dealer ID
        dealer.put(KEY_ID, pref.getString(KEY_ID, null));

        return dealer;
    }


    public void createProfileImageSession(String imageUrl) {

        // Storing ID in pref
        editor.putString(KEY_IMAGE_URL, imageUrl);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session profile image data
     */
    public HashMap<String, String> getDealerProfileImage() {

        HashMap<String, String> profileImage = new HashMap<String, String>();

        // user profile image
        profileImage.put(KEY_ID, pref.getString(KEY_IMAGE_URL, null));

        return profileImage;
    }

    /**
     * Clear session details
     */
    public void logout() {

        // Clearing all data from Shared Preferences
        //editor.clear();

        editor.remove(KEY_ID);
        editor.remove(KEY_IMAGE_URL);
        editor.remove(IS_LOGIN);
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(mContext, WelcomeActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
