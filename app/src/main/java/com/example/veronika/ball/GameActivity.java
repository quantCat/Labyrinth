package com.example.veronika.ball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    SharedPreferences shared_preferences = null;
    TextView tvText;
    public static PositionCheck pc;
    Timer timer;
    StringBuilder sb = new StringBuilder();
    Labyrinth labyrinth;
    Drawer drawer;
    int game_map_resource_id;
    int game_map_saving_id;
    int stars;
    boolean finished = false;
    boolean pause_activity_started = false;
    boolean playing_started = false;
    boolean is_continuing = false;
    boolean high_speed;
    final static int REQUEST_CODE_PAUSE = 1;
    final static int RCODE_TO_MENU = 0;
    final static int RCODE_CONTINUE = 1;
    final static int RCODE_RESTART = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("trace", "GameActivity.onCreate");
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        high_speed = shared_preferences.getBoolean("high_speed", false);

        tvText = (TextView) findViewById(R.id.tvText);

        pc = new PositionCheck(this);
        Intent starting_intent = getIntent();
        game_map_resource_id = starting_intent.getIntExtra("MAP", R.raw.map1);
        game_map_saving_id = starting_intent.getIntExtra("SAVING_ID", -1);
        is_continuing = getIntent().getBooleanExtra("CONTINUE", false);
        initGame();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    void initGame() {
        loadLabyrinth();
        drawer = (Drawer) findViewById(R.id.view);
        drawer.ball.labyrinth = labyrinth;
        drawer.labyrinth = labyrinth;
        drawer.high_speed = high_speed;
        if (is_continuing) {
            try {
                FileInputStream saving = openFileInput(getSavingFileName());
                Scanner sc = new Scanner(saving);
                float x = sc.nextFloat();
                float y = sc.nextFloat();
                stars = sc.nextInt();
                drawer.ball.initPosition(x, y);
            } catch(java.io.FileNotFoundException _e) {
                Toast.makeText(this, "Saving file disappeared", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else {
            stars = 0;
            drawer.ball.initPosition();
        }
    }

    private void loadLabyrinth() {
        labyrinth = new Labyrinth();
        labyrinth.readLabyrinth(this, game_map_resource_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pc.onResume();
        if (!pause_activity_started && !playing_started) {
            startPlaying();
        }
    }

    void startPlaying() {
        Log.i("GameActivity", String.format("startPlaying: saving_id=%d res_id=%d",
                game_map_saving_id, game_map_resource_id));
        timer = new Timer();
        final GameActivity self = this;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                        drawer = (Drawer)findViewById(R.id.view);
                        drawer.coordChange();
                        labyrinth.checkWallTouchAndReact(drawer);
                        if(labyrinth.checkAndReactStarTouch(drawer)) {
                            Toast.makeText(drawer.getContext(), "Star collected", Toast.LENGTH_SHORT).show();
                            stars++;
                        }
                        boolean hole_touched = labyrinth.checkHoleTouch(drawer);
                        if (hole_touched) {
                            finished = true;
                            self.gameFinished(false);
                        }
                        if (drawer.isGameFinished()) {
                            finished = true;
                            self.gameFinished(true);
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, high_speed ? 40: 100);
        startService(new Intent(this, MusicServiceGame.class));
        playing_started = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pc.onPause();
        timer.cancel();
        stopService(new Intent(this, MusicServiceGame.class));
        playing_started = false;
        if (finished) return;

        if (!pause_activity_started) {
            startPauseActivity();
        }
    /*    try {
            FileOutputStream saving = openFileOutput(saving_name, 0);
            PrintWriter pw = new PrintWriter(saving);
            pw.printf(Locale.US, "%g %g %d\n", drawer.ball.getX(), drawer.ball.getY(), stars);
            pw.flush();
            Toast.makeText(this, "Game saved", Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException _e) {
            Toast.makeText(this, "Saving failed", Toast.LENGTH_LONG).show();
        }*/
    }

    public void onBackPressed() {
        if (!pause_activity_started) {
            startPauseActivity();
        }
    }

    void startPauseActivity() {
        Intent pause = new Intent(this, GamePauseActivity.class);
        String saving_name = getSavingFileName();
        pause.putExtra("SAVE FILE", saving_name);
        pause.putExtra("BALLX", drawer.ball.getX());
        pause.putExtra("BALLY", drawer.ball.getY());
        pause.putExtra("STARS", stars);
        startActivityForResult(pause, REQUEST_CODE_PAUSE);
        pause_activity_started = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("GameActivity", String.format("onActivityResult: %d %d", requestCode, resultCode));
        if (requestCode == REQUEST_CODE_PAUSE) {
            pause_activity_started = false;
            switch (resultCode) {
                case RCODE_TO_MENU:
                    finish();
                    break;
                case RCODE_CONTINUE: // continue
                    Log.i("GameActivity", "RCODE_CONTINUE");
                    // This drops ball speed to 0. FIXME: create a special method for this.
                    drawer.ball.initPosition(drawer.ball.getX(), drawer.ball.getY());
                    break;
                case RCODE_RESTART:
                    is_continuing = false;
                    initGame();
                    break;
            }
        }
    }

    protected void gameFinished(boolean success) {

        Log.i("GameActivity", String.format("gameFinished: map: resource_id=%d saving_id=%d stars=%d",
                    game_map_resource_id, game_map_saving_id, stars));

        if (success) {

            int stars_max = 0;
            String resultFileName = getResultFileName();
            File resultFile = new File(getFilesDir().getAbsolutePath() + "/" + resultFileName);

            if (resultFile.canRead()) {
                try {
                    FileInputStream result_input = openFileInput(resultFileName);
                    Scanner sc = new Scanner(result_input);
                    stars_max = sc.nextInt();
                } catch (java.io.FileNotFoundException _e) {
                    Toast.makeText(this, "Result file disappeared", Toast.LENGTH_LONG).show();
                }
                Log.i("GameActivity", String.format("old stars_max: %d", stars_max));
            }

            if (!resultFile.canRead() || stars > stars_max) {
                Log.i("GameActivity", String.format("writing result: stars=%d file=%s full_path=%s",
                            stars, resultFileName, resultFile.getPath()));
                try {
                    //-FileOutputStream new_saving = openFileOutput(resultFileName, 0);
                     Log.i("GameActivity", "result saved");
                    if (resultFile.canRead()) {
                        Toast.makeText(this, "You've beaten your last record! Your result saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Your result saved.", Toast.LENGTH_SHORT).show();
                    }
                    FileOutputStream new_saving = new FileOutputStream(resultFile);
                    PrintWriter pw = new PrintWriter(new_saving);
                    pw.printf(Locale.US, "%d\n", stars);
                    pw.flush();
                } catch (FileNotFoundException _e) {
                    Toast.makeText(this, "Result writing mysteriously failed", Toast.LENGTH_LONG).show();
                }
            }

            File saving_path = new File(getFilesDir().getPath() + "/" + getSavingFileName());
            saving_path.delete();

        }

        Intent game_finished = new Intent(this, GameFinishedActivity.class);
        game_finished.putExtra("STATUS", success ? "WIN" : "LOSE");
        game_finished.putExtra("MAP", game_map_resource_id);
        game_finished.putExtra("STARS", stars);
        startActivity(game_finished);
        finish();
    }

    void showInfo() {
        sb.setLength(0);
        sb.append(String.format("Acc: %.2f %.2f Pos: %.2f %.2f",
                pc.valuesAccel[0], pc.valuesAccel[1], drawer.ball.getX(), drawer.ball.getY()));
        tvText.setText(sb);
    }

    String getSavingFileName() {
        return Integer.toString(game_map_saving_id) + ".save";
    }

    String getResultFileName() {
        return Integer.toString(game_map_saving_id) + ".result";
    }
}
