package com.example.veronika.ball;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by user on 06.04.2017.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        final Intent game_intent = new Intent(this, MapChooseActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        final Button bNewGame = (Button) findViewById(R.id.button_new_game);
        bNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(game_intent);
            }
        });

        final Intent help_intent = new Intent(this, HelpActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        final Button bhelp = (Button) findViewById(R.id.button_help);
        bhelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(help_intent);
            }
        });

        final Intent settings_intent = new Intent(this, SettingsActivity.class);
        final Button bsettings = (Button) findViewById(R.id.button_settings);
        bsettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(settings_intent);
            }
        });

        final Intent credits_intent = new Intent(this, CreditsActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        final Button bcredits = (Button) findViewById(R.id.button_credits);
        bcredits.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(credits_intent);
            }
        });

        final Button bquit = (Button) findViewById(R.id.button_quit);
        bquit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
}

    @Override
    protected void onResume () {
        super.onResume();
        startService(new Intent(this, MusicServiceMenu.class));
    }
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MusicServiceMenu.class));
    }

}
