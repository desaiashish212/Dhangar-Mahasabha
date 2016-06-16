package com.dhangarmahasabha.innovators.ui.setting;

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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.ui.news.Demo;
import com.dhangarmahasabha.innovators.ui.news.NotificationNewsActivity;
import com.dhangarmahasabha.innovators.ui.news.ViewNews;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.Utils;


/**
 * Created by AD on 12-Jan-16.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvUpdateProfile;
    private TextView tvAboutDM;
    private TextView tvShare;
    private TextView tvPrivacy;
    private TextView tvFeedback;
    private TextView tvDonate;
    private TextView tvAboutAs;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        actionBar = getSupportActionBar();
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bg_button_login)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back_r);
        inti();
        listener();
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.update_profile:
                    startActivity(new Intent(SettingsActivity.this,UpdateProfileActivity.class));
                break;
            case R.id.aboutdn_button:
                AboutDmActivity.start(this);
                break;
            case R.id.share_button:
               // startActivity(new Intent(SettingsActivity.this,ShareActivity.class));
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Dhangar Mahasabha social news application for your smartphone. Download it today form https://dhangarmahasabha.net16.net");
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
                try
                { Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.dhangarmahasabha.innovators \n\n";
                    intent.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(intent, "choose one"));
                }
                catch(Exception e)
                { //e.toString();
                    DialogUtils.show(this,e.toString());
                }
                break;
            case R.id.policy_button:
                PrivacyAndPolicyActivity.start(this);
                break;
            case R.id.feedback:
                FeedbackActivity.start(this);
//                Intent intent = new Intent(this, Demo.class);
//                intent.putExtra("id","52");
      //          startActivity(intent);
                break;
            case R.id.donate:
                DonateActivity.start(this);
                break;
            case R.id.aboutus_button:
                startActivity(new Intent(SettingsActivity.this,About_asActivity.class));
                break;
            default:
                break;
        }
    }
    public void inti(){
        tvUpdateProfile = (TextView) findViewById(R.id.update_profile);
        tvAboutDM = (TextView) findViewById(R.id.aboutdn_button);
        tvShare = (TextView) findViewById(R.id.share_button);
        tvPrivacy = (TextView) findViewById(R.id.policy_button);
        tvFeedback = (TextView) findViewById(R.id.feedback);
        tvDonate = (TextView) findViewById(R.id.donate);
        tvAboutAs = (TextView) findViewById(R.id.aboutus_button);
    }
    public void listener(){
        tvUpdateProfile.setOnClickListener(this);
        tvAboutDM.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvDonate.setOnClickListener(this);
        tvAboutAs.setOnClickListener(this);
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

}
