package com.dhangarmahasabha.innovators.ui.setting;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.ui.registration.ChooseLanguageActivity;
import com.dhangarmahasabha.innovators.ui.registration.UserProfileActivity;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity implements Initilization {

    private static String TAG = UpdateProfileActivity.class.getSimpleName();
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
    private ProgressDialog progressDialog;
    private NetworkUtils networkUtils;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        actionBar = getSupportActionBar();
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bg_button_login)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back_r);
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
        networkUtils = new NetworkUtils(this);
        System.out.println("District:"+prefManager.getDistrict());
        if (networkUtils.isConnectingToInternet()) {
            updateCitySpinnerCtrl(prefManager.getDistrict());
        }else {
            Toast.makeText(UpdateProfileActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();
        }
        progressDialog = DialogUtils.getProgressDialog(this);

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
                if (networkUtils.isConnectingToInternet()) {
                    validation(name,mobile,email,dob,city,pincode,occopation);
                }else {
                    Toast.makeText(UpdateProfileActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void validation(final String name, final String mobile, final String email,final String dob,final String city,final String pincode,final String occupation) {
        // updateCurrentUserData();
        if (name.length()==0) {

            edtName.requestFocus();
            edtName.setError("FIELD CANNOT BE EMPTY");
        }else if (name.length()<=2) {

            edtName.requestFocus();
            edtName.setError("NAME MUST BE GREATER THAN 2 CHARACTER");
        }else if(!name.matches("[a-zA-Z ]+")) {
            edtName.requestFocus();
            edtName.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }else if(mobile.length()==0){
            edtMobile.requestFocus();
            edtMobile.setError("FIELD CANNOT BE EMPTY");
        }else if (mobile.length() < 10 || mobile.length()>10){
            edtMobile.requestFocus();
            edtMobile.setError("Invalid MOBILE NUMBER");
        }else if(email.length()==0){
            edtEmail.requestFocus();
            edtEmail.setError("FIELD CANNOT BE EMPTY");
        }else if (!isValidEmail(email)){
            edtEmail.requestFocus();
            edtEmail.setError("Invalid Email");
        }else if(dob.length()==0){
            tvDob.requestFocus();
            tvDob.setError("FIELD CANNOT BE EMPTY");
        }else if(pincode.length()==0){
            edtPinCode.requestFocus();
            edtPinCode.setError("FIELD CANNOT BE EMPTY");
        }else if (pincode.length() < 6 || pincode.length()>6){
            edtPinCode.requestFocus();
            edtPinCode.setError("Invalid PIN CODE");
        }else if (occupation.length()==0) {

            edtOccupation.requestFocus();
            edtOccupation.setError("FIELD CANNOT BE EMPTY");
        }else if (occupation.length()<=2) {

            edtOccupation.requestFocus();
            edtOccupation.setError("NAME MUST BE GREATER THAN 2 CHARACTER");
        }else if(!occupation.matches("[a-zA-Z ]+")) {
            edtOccupation.requestFocus();
            edtOccupation.setError("ENTER ONLY ALPHABETICAL CHARACTER");
        }else {
            progressDialog.show();
            updateProfile(name,mobile,email,dob,city,pincode,occupation);
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
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
                    Toast.makeText(UpdateProfileActivity.this,"Please Select another date",Toast.LENGTH_SHORT).show();
                    tvDob.setText("");
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }



        }

    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
