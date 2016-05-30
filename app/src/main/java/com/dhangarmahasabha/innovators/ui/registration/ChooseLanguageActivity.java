package com.dhangarmahasabha.innovators.ui.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.ui.welcome.WelcomeActivity;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;
import com.innovators.localizationactivity.LocalizationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChooseLanguageActivity extends LocalizationActivity implements Initilization {
    private static String TAG = ChooseLanguageActivity.class.getSimpleName();
    private TextView txtMarathi;
    private TextView txtHindi;
    private TextView txtEnglish;
    private int status = 1;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        init();
        initListener();
    }

    @Override
    public void init() {
        txtMarathi = (TextView) findViewById(R.id.txt_marathi_lan);
        txtHindi = (TextView) findViewById(R.id.txt_hindi_lan);
        txtEnglish = (TextView) findViewById(R.id.txt_english_lan);
        progressDialog = DialogUtils.getProgressDialog(this);

    }

    @Override
    public void initListener() {
        txtMarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                status = 1;
                updateLanguage(status);
            }
        });

        txtHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                status = 2;
                updateLanguage(status);
            }
        });

        txtEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                status = 2;
                updateLanguage(status);
            }
        });
    }

    private void updateLanguage(final int statusLang) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_LANG_UPDATE, new Response.Listener<String>() {

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
                        // parsing the user profile information
                        if (status==1){
                            setLanguage("ma");
                        }else  if (status==2){
                            setLanguage("hi");
                        }else if (status==3){
                            setLanguage("en");
                        }
                        progressDialog.dismiss();
                        startActivity(new Intent(ChooseLanguageActivity.this, WelcomeActivity.class));
                        finish();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {
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
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", String.valueOf(statusLang));

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

    }
}
