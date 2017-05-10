package com.example.veronika.ball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by user on 06.04.2017.
 */

public class CreditsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_activity);

        final Intent menu_intent = new Intent(this, StartActivity.class);
        final Button bm = (Button) findViewById(R.id.back_btn);
        final Activity self = this;
        bm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(self);
            }
        });
    }

}
