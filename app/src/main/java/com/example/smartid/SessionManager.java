package com.example.smartid;

import android.content.Context;
import android.content.Intent; // Import Intent for the logout navigation
import android.content.SharedPreferences;

public class SessionManager {

    // Shared Preferences file name
    private static final String PREF_NAME = "SmartIDSession";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_TOKEN = "userToken";
    private static final String KEY_USER_RFID = "userRfid";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    // SharedPreferences instance and editor
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        // Use MODE_PRIVATE so only this app can access the preferences
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Call this when the user successfully logs in.
     * Stores the user's token, RFID, name, and email.
     */
    public void createLoginSession(String token, String rfid, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_TOKEN, token);
        editor.putString(KEY_USER_RFID, rfid);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit(); // Save the changes
    }

    /**
     * Call this to retrieve the saved RFID of the logged-in user.
     * @return User RFID string, or null if the user is not logged in.
     */
    public String getUserRfid() {
        return pref.getString(KEY_USER_RFID, null); // Returns null if not found
    }

    /**
     * Call this to retrieve the saved name of the logged-in user.
     * @return User name string, or null if not logged in.
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    /**
     * Call this to retrieve the saved email of the logged-in user.
     * @return User email string, or null if not logged in.
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Call this to retrieve the saved JWT token.
     * You might need this later if you want to make authenticated API calls.
     * @return User token string, or null if not logged in.
     */
    public String getUserToken() {
        return pref.getString(KEY_USER_TOKEN, null);
    }


    /**
     * Quick check for login status.
     * @return true if user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Call this when the user clicks the logout button.
     * Clears all session data and redirects to the Login Activity.
     */
    public void logoutUser() {
        // Clear all data from SharedPreferences
        editor.clear();
        editor.commit();

        // After logout, redirect user to the Login Activity
        Intent i = new Intent(context, Login.class); // Make sure Login.class is your login activity name

        // Close all other Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start Login Activity
        context.startActivity(i);
    }
}