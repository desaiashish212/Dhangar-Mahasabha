package com.dhangarmahasabha.innovators.ui.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements Initilization{

    private static String TAG = RegistrationActivity.class.getSimpleName();

    private EditText edtUserName;
    private EditText edtMobileNo;
    private Spinner spState;
    private Spinner spDistrict;
    private Button btnRegister;
    private ProgressBar progressBar;
    private AQuery aq;
    private GoogleCloudMessaging gcmObj;
    PrefManager prefManager;
    private String regId = "";
    private static final String REG_ID = "regId";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        initListener();
    }

    @Override
    public void init() {
        edtUserName = (EditText) findViewById(R.id.edt_user_name);
        edtMobileNo = (EditText) findViewById(R.id.edt_mobile_no);
        spState = (Spinner) findViewById(R.id.stateSpinner);
        spDistrict = (Spinner) findViewById(R.id.districtSpinner);
        btnRegister = (Button) findViewById(R.id.btn_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        aq = new AQuery(this);
        prefManager = new PrefManager(this);
        progressDialog = DialogUtils.getProgressDialog(this);

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,getResources().getStringArray(R.array.state_array));
        spState.setAdapter(stateAdapter);

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String selectedState = spState.getSelectedItem().toString();
                updateCitySpinnerCtrl(selectedState);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void initListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtUserName.getText().toString().trim();
                String mobileNo = edtMobileNo.getText().toString().trim();
                String state = spState.getSelectedItem().toString();
                String district = spDistrict.getSelectedItem().toString();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(mobileNo) && !TextUtils.isEmpty(state) && !TextUtils.isEmpty(district)){
                    if (checkPlayServices()) {
                        progressDialog.show();
                        // Register Device in GCM Server
                        registerInBackground(userName, mobileNo,state,district);
                    }
                }else {
                    Toast.makeText(RegistrationActivity.this,"Fill all field",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void updateCitySpinnerCtrl(String state) {
        String url = Config.DISTRICT_URL+ state;
        aq.progress(R.id.progressBar1).ajax(url, JSONObject.class, this,"jsonCallback");
    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
        if (json != null) {
            String[] cities = null;
            Gson gson = new GsonBuilder().create();
            try {
                String jsonResponse = json.getJSONArray("Cities").toString();
                cities = gson.fromJson(jsonResponse, String[].class);
            } catch (JSONException e) {
                Toast.makeText(aq.getContext(), "Error in parsing JSON", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(aq.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item, cities);
            spDistrict.setAdapter(adapter);
        }
        else {
            if(status.getCode() == 500){
                Toast.makeText(aq.getContext(),"Server is busy or down. Try again!",Toast.LENGTH_SHORT).show();
            }
            else if(status.getCode() == 404){
                Toast.makeText(aq.getContext(),"Resource not found!",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(aq.getContext(),"Unexpected Error occured",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    // AsyncTask to register Device in GCM Server
    private void registerInBackground(final String name, final  String mobile, final String state,final String disrict) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(getApplicationContext());
                    }
                    regId = gcmObj
                            .register(Config.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    //storeRegIdinSharedPref(applicationContext, regId, user,phone,email,dob,state,city,pincode);

                    PrefManager prefManager = new PrefManager(getApplicationContext());
                    prefManager.createLogin(name,mobile,state,disrict,regId);
                    requestForSMS(name,mobile,state,disrict,regId);

                    Toast.makeText(
                            getApplicationContext(),
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);


    }

    private void requestForSMS(final String name, final String mobile, final String state,final String district,final String regId) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.REGISTRATION_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");
                    String otp = responseObj.getString("otp");

                    if (!error) {
                        prefManager.setIsWaitingForSms(true);
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegistrationActivity.this,MobileVerificationActivity.class);
                        intent.putExtra("otp",otp);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                    }

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                     progressDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("mobile", mobile);
                params.put("state", state);
                params.put("district", district);
                params.put("regid", regId);

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}
