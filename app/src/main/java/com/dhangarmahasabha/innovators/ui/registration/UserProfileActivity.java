package com.dhangarmahasabha.innovators.ui.registration;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.PrefManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements Initilization {

    private static String TAG = UserProfileActivity.class.getSimpleName();
    private EditText edtName;
    private EditText edtMobile;
    private EditText edtEmail;
    private TextView tvDob;
    private ImageButton calender;
    private Spinner spCity;
    private EditText edtPinCode;
    private EditText edtOccupation;
    private Button btnUpdateProfile;
    private AQuery aq;
    private PrefManager prefManager;
    static final int DATE_PICKER_ID = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        init();
        initListener();
        initSetText();
    }

    @Override
    public void init() {
        edtName = (EditText) findViewById(R.id.edt_name);
        edtMobile = (EditText) findViewById(R.id.edt_mobile_no);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        tvDob = (TextView) findViewById(R.id.tvDob);
        calender = (ImageButton) findViewById(R.id.imgBtn_calender);
        spCity = (Spinner) findViewById(R.id.citySpinner);
        edtPinCode = (EditText) findViewById(R.id.edt_pincode);
        edtOccupation = (EditText) findViewById(R.id.edt_occupation);
        btnUpdateProfile = (Button) findViewById(R.id.btn_udate_profile);
        aq = new AQuery(this);
        prefManager = new PrefManager(this);
        System.out.println("District:"+prefManager.getDistrict());
        updateCitySpinnerCtrl(prefManager.getDistrict());

    }

    @Override
    public void initListener() {

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = setText(edtName.getText().toString().trim());
                String mobile = setText(edtMobile.getText().toString().trim());
                String email = setText(edtEmail.getText().toString().trim());
                String dob = setText(tvDob.getText().toString().trim());
                String city = setText(spCity.getSelectedItem().toString());
                String pincode = setText(edtPinCode.getText().toString().trim());
                String occopation = setText(edtOccupation.getText().toString().trim());
                updateProfile(name,mobile,email,dob,city,pincode,occopation);
            }
        });


    }
    public void initSetText(){
        HashMap<String, String> profile = prefManager.getUserDetails();
        edtName.setText(setText(profile.get("name")));
        edtMobile.setText(setText(profile.get("mobile")));
        edtEmail.setText(setText(profile.get("email")));
        tvDob.setText(setText(profile.get("dob")));
        edtPinCode.setText(setText(profile.get("pincode")));
        edtOccupation.setText(setText(profile.get("occupation")));
        System.out.println(profile.get("name")+profile.get("mobile")+setText(profile.get("mobile")));

    }

    private String setText(String txt){
        if (!isTextEmpty(txt)){
            return txt;
        }else
            return "";

    }

    private boolean isTextEmpty(String txt){
        return TextUtils.isEmpty(txt);
    }

    public void updateCitySpinnerCtrl(String district) {
        String url = Config.CITY_URL+ district;
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
            spCity.setAdapter(adapter);
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

    private void updateProfile(final String name, final String mobile, final String email,final String dob,final String city,final String pincode,final String occupation) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.UPDATE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        prefManager.updateProfile(name,mobile,email,dob,city,pincode,occupation);
                        startActivity(new Intent(UserProfileActivity.this,ChooseLanguageActivity.class));
                        finish();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                    }

                    //progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                  //  progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
               // progressBar.setVisibility(View.GONE);
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
                params.put("email", email);
                params.put("dob", dob);
                params.put("city", city);
                params.put("pincode", pincode);
                params.put("occupation", occupation);

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


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, 1992, 05,19);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            int year  = selectedYear;
            int month = selectedMonth;
            int day   = selectedDay;

            // Show selected date

            String Selected_Date=day+"/"+(month+1)+"/"+year;
            DateFormat f_Selected=new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date_Selected=f_Selected.parse(Selected_Date);
                tvDob.setText(Selected_Date);
                Calendar birth_date=Calendar.getInstance();
                birth_date.setTime(date_Selected);

                Calendar cal_current=Calendar.getInstance();

                int compare_date=cal_current.getTime().compareTo(birth_date.getTime());
                if(compare_date==1)
                {
                    tvDob.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
                }

                else
                {
                    Toast.makeText(UserProfileActivity.this,"Please Select another date",Toast.LENGTH_SHORT).show();
                    tvDob.setText("");
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }



        }

    };

}
