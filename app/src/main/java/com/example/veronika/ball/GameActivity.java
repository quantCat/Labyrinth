package com.example.veronika.ball;

import android.content.Intent;
import android.media.AudioManager;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    TextView tvText;
    public static PositionCheck pc;
    Timer timer;
    StringBuilder sb = new StringBuilder();
    Labyrinth labyrinth;
    Drawer drawer;
    int game_map_id;
    int stars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("trace", "GameActivity.onCreate");
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvText = (TextView) findViewById(R.id.tvText);

        pc = new PositionCheck(this);
        loadLabyrinth();
        drawer = (Drawer) findViewById(R.id.view);
        drawer.ball.labyrinth = labyrinth;
        drawer.labyrinth = labyrinth;
        if (getIntent().getBooleanExtra("CONTINUE", false)) {
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
            drawer.ball.initPosition();
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void loadLabyrinth() {
        labyrinth = new Labyrinth();
        Intent intent = getIntent();
        game_map_id = intent.getIntExtra("MAP", R.raw.map1);
        labyrinth.readLabyrinth(this, game_map_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pc.onResume();

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
                        labyrinth.checkAndReactStarTouch(drawer);
                        boolean hole_touched = labyrinth.checkHoleTouch(drawer);
                        if (hole_touched) {
                            self.gameFinished(false);
                        }
                        if (drawer.isGameFinished()) {
                            self.gameFinished(true);
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 100);
        startService(new Intent(this, MusicServiceGame.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        pc.onPause();
        timer.cancel();
        stopService(new Intent(this, MusicServiceGame.class));
        String saving_name = getSavingFileName();
        try {
            FileOutputStream saving = openFileOutput(saving_name, 0);
            PrintWriter pw = new PrintWriter(saving);
            pw.printf(Locale.US, "%g %g %d\n", drawer.ball.getX(), drawer.ball.getY(), stars);
            pw.flush();
            Toast.makeText(this, "Game saved", Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException _e) {
            Toast.makeText(this, "Saving failed", Toast.LENGTH_LONG).show();
        }
    }

    protected void gameFinished(boolean success) {
        Intent game_finished = new Intent(this, GameFinishedActivity.class);
        game_finished.putExtra("STATUS", success ? "WIN" : "LOSE");
        game_finished.putExtra("MAP", game_map_id);
        game_finished.putExtra("STARS", stars);
        File saving_path = new File(getFilesDir().getPath() + "/" + getSavingFileName());
        saving_path.delete();
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
        return Integer.toString(getIntent().getIntExtra("SAVING_ID", 0)) + ".save";
    }
}
