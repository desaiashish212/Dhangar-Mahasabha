package com.dhangarmahasabha.innovators.ui.setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.util.Utils;

public class About_asActivity extends AppCompatActivity {
    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_as);
        version = (TextView) findViewById(R.id.version_textview);
        version.setText(getString(R.string.stn_version, Utils.getAppVersionName(this)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
