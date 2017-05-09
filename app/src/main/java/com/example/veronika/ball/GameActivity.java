package com.example.veronika.ball;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    TextView tvText;
    public static PositionCheck pc;
    Timer timer;
    StringBuilder sb = new StringBuilder();
    Labyrinth labyrinth;
    Drawer drawer;


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
        drawer.ball.initPosition();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void loadLabyrinth() {
        labyrinth = new Labyrinth();
        labyrinth.readLabyrinth(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pc.onResume();

        timer = new Timer();
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
                        boolean hole_touched = labyrinth.checkHoleTouch(drawer);
                        if (hole_touched) {
                            gameFinishedUnsuccessfully();;
                        }
                        if (drawer.isGameFinished()) {
                            gameFinishedSuccessfully();
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
    }

    protected void gameFinishedSuccessfully () {
        super.onPause();
        pc.onPause();
        timer.cancel();
        stopService(new Intent(this, MusicServiceGame.class));
        Intent game_finished = new Intent(this, StartActivity.class);
        game_finished.putExtra("STATUS", "WIN");
        startActivity(game_finished);

        //Toast.makeText(this, "You win!", Toast.LENGTH_LONG);
    }

    protected void gameFinishedUnsuccessfully () {
        super.onPause();
        pc.onPause();
        timer.cancel();
        stopService(new Intent(this, MusicServiceGame.class));
        Intent game_finished = new Intent(this, StartActivity.class);
        game_finished.putExtra("STATUS", "LOSE");
        startActivity(game_finished);

        //Toast.makeText(this, "You win!", Toast.LENGTH_LONG);
    }

    void showInfo() {
        sb.setLength(0);
        sb.append(String.format("Acc: %.2f %.2f Pos: %.2f %.2f",
                pc.valuesAccel[0], pc.valuesAccel[1], drawer.ball.getX(), drawer.ball.getY()));
        tvText.setText(sb);
    }
}