package com.dhangarmahasabha.innovators.ui.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MobileVerificationActivity extends AppCompatActivity implements Initilization {
    private static String TAG = MobileVerificationActivity.class.getSimpleName();

    private EditText edtOTP;
    private Button btnSubmit;
    private Button btnBack;
    private ImageButton ibResendOtp;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        init();
        initListener();
    }

    @Override
    public void init() {
        edtOTP = (EditText) findViewById(R.id.edt_otp);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnBack = (Button) findViewById(R.id.btn_back);
        ibResendOtp = (ImageButton) findViewById(R.id.imgBtn_resend);
        prefManager = new PrefManager(this);
        Intent intent = getIntent();
        edtOTP.setText(intent.getExtras().getString("otp"));
    }

    @Override
    public void initListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = edtOTP.getText().toString().trim();
                if (!TextUtils.isEmpty(otp)){
                    verifyOtp(otp);
                }else {
                    Toast.makeText(MobileVerificationActivity.this,"Enter OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setIsWaitingForSms(false);
                startActivity(new Intent(MobileVerificationActivity.this,RegistrationActivity.class));
                finish();
            }
        });

        ibResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void verifyOtp(final String otp) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_VERIFY_OTP, new Response.Listener<String>() {

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
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        String name = profileObj.getString("name");
                        String mobile_no = profileObj.getString("mobile_no");
                        String state = profileObj.getString("state");
                        String district = profileObj.getString("district");
                        String gcm_reg_id = profileObj.getString("gcm_reg_id");
                        String email = profileObj.getString("email");
                        String birth = profileObj.getString("birth");
                        String city = profileObj.getString("city");
                        String pincode = profileObj.getString("pincode");
                        String occupation = profileObj.getString("occupation");

                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.updateProfile(name,mobile_no,state,district,gcm_reg_id,email,birth,city,pincode,occupation);
                        pref.setIsWaitingForSms(false);

                        Intent intent = new Intent(MobileVerificationActivity.this, UserProfileActivity.class);
                        startActivity(intent);
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
