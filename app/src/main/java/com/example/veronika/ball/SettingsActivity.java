package com.example.veronika.ball;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.Map;

/**
 * Created by user on 06.04.2017.
 */

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences shared_preferences = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean high_speed = shared_preferences.getBoolean("high_speed", true);
        RadioGroup rg = (RadioGroup) findViewById(R.id.radio_quality);
        rg.check(high_speed ? R.id.higher_option : R.id.lower_option);
        final SettingsActivity self = this;
        final RadioButton rb_low = (RadioButton) findViewById(R.id.lower_option);
        rb_low.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    self.setHighSpeed(false);
                }
            }
        });
        final RadioButton rb_high = (RadioButton) findViewById(R.id.higher_option);
        rb_high.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    self.setHighSpeed(true);
                }
            }
        });
        final Button bClearSavings = (Button) findViewById(R.id.clear_savings_button);
        bClearSavings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClearSavingsButton();
            }
        });
        final Button bClearHighscores = (Button) findViewById(R.id.clear_highscores_button);
        bClearHighscores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClearHighscoresButton();
            }
        });
        final Button bReturn = (Button) findViewById(R.id.return_btn);
        bReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(self);
            }
        });
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    void setHighSpeed(boolean is_high) {
        SharedPreferences.Editor editor = shared_preferences.edit();
        editor.putBoolean("high_speed", is_high);
        editor.apply();
    }

    void onClearSavingsButton() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SettingsActivity.this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete all your savings?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Map<String, Integer> maplist = MapList.getMapIdList();
                        for (int i = 0; i < maplist.size(); ++i) {
                            File saving_path = new File(getFilesDir().getAbsolutePath() + "/" + Integer.toString(i) + ".save");
                            if (saving_path.canRead()) {
                                saving_path.delete();
                            }
                        }
                        Toast.makeText(SettingsActivity.this, "All savings have been deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void onClearHighscoresButton() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SettingsActivity.this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete all your highscores?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Map<String, Integer> maplist = MapList.getMapIdList();
                        for (int i = 0; i < maplist.size(); ++i) {
                            File saving_path = new File(getFilesDir().getAbsolutePath() + "/" + Integer.toString(i) + ".result");
                            if (saving_path.canRead()) {
                                saving_path.delete();
                            }
                        }
                        Toast.makeText(SettingsActivity.this, "All highscores have been deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
