package com.example.veronika.ball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by user on 06.04.2017.
 */

public class SettingsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_level);
        final Intent menu_intent = new Intent(this, MainActivity.class);
        final Button bOK = (Button) findViewById(R.id.ok_btn);
        bOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(menu_intent);
            }
        });
        final Button bCancel = (Button) findViewById(R.id.cancel_btn);
        bCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(menu_intent);
            }
        });
    }
}
