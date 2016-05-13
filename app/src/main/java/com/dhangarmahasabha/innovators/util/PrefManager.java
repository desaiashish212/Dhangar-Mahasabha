package com.dhangarmahasabha.innovators.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by AD on 02-May-16.
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Dhangar";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATE = "state";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_CITY = "city";
    private static final String KEY_PINCODE = "pincode";
    private static final String KEY_OCCUPATION = "occupation";
    private static final String KEY_REG_ID = "reg_id";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public String getDistrict() {
        return pref.getString(KEY_DISTRICT, null);
    }

    public String getName(){
        return pref.getString(KEY_NAME, null);
    }

    public void setRegID(String regID) {
        editor.putString(KEY_REG_ID, regID);
        editor.commit();
    }

    public String getKeyRegId() {
        return pref.getString(KEY_REG_ID, null);
    }

    public void createLogin(String name,String mobile,String state,String district,String regId) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE_NUMBER, mobile);
        editor.putString(KEY_STATE, state);
        editor.putString(KEY_DISTRICT, district);
        editor.putString(KEY_REG_ID, regId);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.commit();
    }

    public void updateProfile(String name,String mobile,String state,String district,String regId,String email,String birth,String city,String pincode,String occupation) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE_NUMBER, mobile);
        editor.putString(KEY_STATE, state);
        editor.putString(KEY_DISTRICT, district);
        editor.putString(KEY_REG_ID, regId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOB, birth);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_OCCUPATION, occupation);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void updateProfile(String name,String mobile,String email,String birth,String city,String pincode,String occupation) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE_NUMBER, mobile);
        // editor.putString(KEY_STATE, state);
//        editor.putString(KEY_DISTRICT, district);
//        editor.putString(KEY_REG_ID, regId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOB, birth);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_OCCUPATION, occupation);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("mobile", pref.getString(KEY_MOBILE_NUMBER, null));
        profile.put("state", pref.getString(KEY_STATE, null));
        profile.put("district", pref.getString(KEY_DISTRICT, null));
        profile.put("redid", pref.getString(KEY_REG_ID, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("dob", pref.getString(KEY_DOB, null));
        profile.put("city", pref.getString(KEY_CITY, null));
        profile.put("pincode", pref.getString(KEY_PINCODE, null));
        profile.put("occupation", pref.getString(KEY_OCCUPATION, null));
        return profile;
    }
}
