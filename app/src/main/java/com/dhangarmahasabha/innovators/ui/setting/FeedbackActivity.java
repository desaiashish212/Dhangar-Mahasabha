package com.dhangarmahasabha.innovators.ui.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.ui.registration.UserProfileActivity;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AD on 05-Jun-16.
 */
public class FeedbackActivity extends AppCompatActivity implements Initilization{
    private EditText edFeedback;
    private Button btSend;
    private ProgressDialog progressDialog;
    private PrefManager prefManager;
    private NetworkUtils networkUtils;
    private ActionBar actionBar;

    private static String TAG = FeedbackActivity.class.getSimpleName();
    public static void start(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        actionBar = getSupportActionBar();
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bg_button_login)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back_r);
        init();
        initListener();
    }

    @Override
    public void init() {
        edFeedback = (EditText) findViewById(R.id.editFeedback);
        btSend = (Button) findViewById(R.id.sendButton);
        progressDialog = DialogUtils.getProgressDialog(this);
        networkUtils = new NetworkUtils(this);
        prefManager = new PrefManager(this);
    }

    @Override
    public void initListener() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = edFeedback.getText().toString().trim();
                if (!TextUtils.isEmpty(feedback)) {
                    if (networkUtils.isConnectingToInternet()) {
                        sendFeedback(feedback);
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(FeedbackActivity.this, "Please write feedback", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void sendFeedback(final String fedbk) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_FEEDBACK, new Response.Listener<String>() {

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
                        progressDialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(FeedbackActivity.this).create();
                        alertDialog.setTitle("Feedback");
                        alertDialog.setMessage("You have successfully submitted feedback");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                       // alertDialog.setIcon(R.drawable.logo);
                        alertDialog.show();

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
                params.put("feedback", fedbk);
                params.put("name", prefManager.getName());
                params.put("mobile", prefManager.getMobileNumber());

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
}
