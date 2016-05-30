package com.dhangarmahasabha.innovators.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.model.Category;
import com.dhangarmahasabha.innovators.ui.registration.MobileVerificationActivity;
import com.dhangarmahasabha.innovators.ui.registration.RegistrationActivity;
import com.dhangarmahasabha.innovators.ui.registration.UserProfileActivity;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = SplashActivity.class.getSimpleName();

    private static int SPLASH_TIME_OUT = 3000;
    public static final String PREFERENCES_FILE_NAME = "MyAppPreferences";
    private String registrationId;
    public static final String REG_ID = "regId";
    private ProgressBar progressBar;
    private DBHandler dbHandler;
    private PrefManager manager;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
//        SharedPreferences mysettings= getSharedPreferences(PREFERENCES_FILE_NAME, 0);
//        SharedPreferences prefs = getSharedPreferences("UserDetails",
//                Context.MODE_PRIVATE);
//        registrationId = prefs.getString(REG_ID, "");
        progressBar.setVisibility(View.VISIBLE);
        dbHandler = new DBHandler(this);
        manager = new PrefManager(this);
        networkUtils = new NetworkUtils(this);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if (networkUtils.isConnectingToInternet()){
                    category();
                }else {
                    if (manager.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }else if (manager.isWaitingForSms()){
                        startActivity(new Intent(SplashActivity.this,MobileVerificationActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
                        finish();
                    }
                }
//                if (!TextUtils.isEmpty(registrationId)) {
//                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                    i.putExtra("regId", registrationId);
//                    startActivity(i);
//                    finish();
//                }else {
//                    startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
//                    finish();
//                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void category() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_GET_CATEGORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {

                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        JSONArray jsonArray = responseObj.getJSONArray("Category");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setCat_id(Integer.parseInt(c.getString("id")));
                            category.setCat_name(c.getString("name"));
                            category.setLang_status(Integer.parseInt(c.getString("lang")));

                            if (!dbHandler.getIdCategory(Integer.parseInt(c.getString("id")))){
                                dbHandler.addCategory(category);// Inserting into DB
                                System.out.println("inserting db " + category);
                            } else {
                                System.out.println("category is already present");
                            }

                        }
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        if (manager.isLoggedIn()){
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                            finish();
                        }else if (manager.isWaitingForSms()){
                            startActivity(new Intent(SplashActivity.this,MobileVerificationActivity.class));
                            finish();
                        }else {
                            startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
                            finish();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
                finish();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("otp", otp);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
