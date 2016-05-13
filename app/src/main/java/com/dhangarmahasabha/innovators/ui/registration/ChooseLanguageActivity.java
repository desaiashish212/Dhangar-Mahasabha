package com.dhangarmahasabha.innovators.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.initilization.Initilization;
import com.dhangarmahasabha.innovators.ui.welcome.WelcomeActivity;
import com.innovators.localizationactivity.LocalizationActivity;

public class ChooseLanguageActivity extends LocalizationActivity implements Initilization {

    private TextView txtMarathi;
    private TextView txtHindi;
    private TextView txtEnglish;

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

    }

    @Override
    public void initListener() {
        txtMarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("ma");
                startActivity(new Intent(ChooseLanguageActivity.this,WelcomeActivity.class));
                finish();
            }
        });

        txtHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("hi");
                startActivity(new Intent(ChooseLanguageActivity.this,WelcomeActivity.class));
                finish();
            }
        });

        txtEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");
                startActivity(new Intent(ChooseLanguageActivity.this,WelcomeActivity.class));
                finish();
            }
        });
    }
}
