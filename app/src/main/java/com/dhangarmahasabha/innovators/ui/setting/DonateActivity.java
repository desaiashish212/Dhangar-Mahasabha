package com.dhangarmahasabha.innovators.ui.setting;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.util.NetworkUtils;


public class DonateActivity extends AppCompatActivity {

    private WebView userAgreementWebView;
    private ActionBar actionBar;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutDmActivity.class);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        actionBar = getSupportActionBar();
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bg_button_login)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back_r);
        NetworkUtils networkUtils = new NetworkUtils(this);
        if (networkUtils.isConnectingToInternet()){
            initUserAgreementWebView();
        }else {
            dailogShow();
        }
    }

    private void initUserAgreementWebView() {
        userAgreementWebView = (WebView) findViewById(R.id.user_agreement_webview);
        userAgreementWebView.getSettings().setJavaScriptEnabled(true);
        String policyLink = getResources().getString(R.string.donate);
        userAgreementWebView.loadUrl(policyLink);
    }

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

    private void dailogShow(){
        AlertDialog alertDialog = new AlertDialog.Builder(DonateActivity.this).create();
        alertDialog.setTitle("Network Error");
        alertDialog.setMessage("Check your internet");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        // alertDialog.setIcon(R.drawable.logo);
        alertDialog.show();
    }
}