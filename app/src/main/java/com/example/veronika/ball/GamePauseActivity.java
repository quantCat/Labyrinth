package com.example.veronika.ball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by veronika on 17/05/2017.
 */

public class GamePauseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pause_activity);

        final Button button_game_save = (Button) findViewById(R.id.button_save);
        button_game_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String saving_name = getIntent().getStringExtra("SAVE FILE");
                    float bx = getIntent().getFloatExtra("BALLX", 0);
                    float by = getIntent().getFloatExtra("BALLY", 0);
                    int stars = getIntent().getIntExtra("STARS", 0);
                    FileOutputStream saving = openFileOutput(saving_name, 0);
                    PrintWriter pw = new PrintWriter(saving);
                    pw.printf(Locale.US, "%g %g %d\n", bx, by, stars);
                    pw.flush();
                    Toast.makeText(GamePauseActivity.this, "Game saved", Toast.LENGTH_SHORT).show();
                } catch (java.io.FileNotFoundException _e) {
                    Toast.makeText(GamePauseActivity.this, "Saving failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button button_continue = (Button) findViewById(R.id.button_continue);
        final Activity self = this;
        button_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(self);
            }
        });

        final Intent game_intent = new Intent(this, GameActivity.class);
        final Button bsg = (Button) findViewById(R.id.button_restart_p);
        bsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(game_intent);
            }
        });

        final Button bm = (Button) findViewById(R.id.button_to_menu_p);
        bm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(self);
            }
        });
    }
}
