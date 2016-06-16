package com.dhangarmahasabha.innovators.ui.splash;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.model.Category;
import com.dhangarmahasabha.innovators.ui.registration.ChooseLanguageActivity;
import com.dhangarmahasabha.innovators.ui.registration.MobileVerificationActivity;
import com.dhangarmahasabha.innovators.ui.registration.RegistrationActivity;
import com.dhangarmahasabha.innovators.ui.registration.UserProfileActivity;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;
import com.innovators.localizationactivity.LanguageSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = SplashActivity.class.getSimpleName();

    private static int SPLASH_TIME_OUT = 3000;
    private ProgressBar progressBar;
    private DBHandler dbHandler;
    private PrefManager manager;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
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
                    }else if (manager.isChooseLang()){
                        startActivity(new Intent(SplashActivity.this,ChooseLanguageActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
                        finish();
                    }
                }
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
                        String flash = responseObj.getString("flash");
                        manager.setSplash(flash);

                        JSONArray jsonArray = responseObj.getJSONArray("Category");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setCat_id(Integer.parseInt(c.getString("id")));
                            category.setCat_name(c.getString("name"));
                            category.setLang_status(Integer.parseInt(c.getString("lang")));
                            category.setPriority(Integer.parseInt(c.getString("priority")));

                            if (!dbHandler.getIdCategory(Integer.parseInt(c.getString("id")))){
                                dbHandler.addCategory(category);// Inserting into DB
                                System.out.println("inserting db " + category);
                            } else {
                                dbHandler.updateCategory(category);
                                System.out.println("category is Update");
                            }

                        }
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
//                startActivity(new Intent(SplashActivity.this,RegistrationActivity.class));
//                finish();
                AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("Check your internet");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                // alertDialog.setIcon(R.drawable.logo);
                alertDialog.show();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lang", LanguageSetting.getLanguage(SplashActivity.this));

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
