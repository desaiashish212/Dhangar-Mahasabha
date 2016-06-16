package com.dhangarmahasabha.innovators.ui.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dhangarmahasabha.innovators.MainActivity;
import com.dhangarmahasabha.innovators.R;

/**
 * Created by AD on 14-Mar-16.
 */
public class WelcomeActivity extends Activity {
    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        start = (Button) findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
            }
        });
    }
}
