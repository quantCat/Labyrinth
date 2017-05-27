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

        final Intent game_intent = new Intent(this, GameActivity.class);
        final String saving_name = getIntent().getStringExtra("SAVE FILE");
        game_intent.putExtra("SAVE FILE", saving_name);


        final Button button_game_save = (Button) findViewById(R.id.button_save);
        button_game_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
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
                setResult(GameActivity.RCODE_CONTINUE);
                finish();
            }
        });
        final Button button_restart = (Button) findViewById(R.id.button_restart_p);
        button_restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(GameActivity.RCODE_RESTART);
                finish();
            }
        });

        final Button button_menu = (Button) findViewById(R.id.button_to_menu_p);
        button_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(GameActivity.RCODE_TO_MENU);
                finish();
            }
        });
    }
}
