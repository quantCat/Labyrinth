package com.example.veronika.ball;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by user on 06.04.2017.
 */

public class SettingsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        RadioGroup radioLevel = (RadioGroup)findViewById(R.id.radio_level);
        RadioGroup radioQuality = (RadioGroup)findViewById(R.id.radio_quality);
        final Intent menu_intent = new Intent(this, StartActivity.class);
        final Button bOK = (Button) findViewById(R.id.ok_btn);
        bOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            openFileOutput("settings", MODE_PRIVATE)));
                    bw.write(String.format("%d\n%d\n"));
                    bw.close();
                    Log.d("File", "File've written");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(menu_intent);
            }
        });
        final Button bCancel = (Button) findViewById(R.id.cancel_btn);
        bCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(menu_intent);
            }
        });
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
