package com.example.veronika.ball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by veronika on 23/04/2017.
 */

public class GameFinishedActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_finished_activity);
        Intent intent = getIntent();
        String game_status = intent.getStringExtra("STATUS");
        final String level = intent.getStringExtra("LEVEL");
        int stars = intent.getIntExtra("STARS", 0);
        TextView gr = (TextView) findViewById(R.id.game_result);
        if (game_status.equals("WIN")) {
            gr.setText(String.format("You win!\n" +
                    "Your result saved. Collected stars: %d", stars));
        } else if (game_status.equals("LOSE")) {
            gr.setText("You lose...");
        } else {
            throw new RuntimeException("Wrong intent extra");
        }


        final Intent menu_intent = new Intent(this, StartActivity.class);
        final Button bm = (Button) findViewById(R.id.tmn_btn);
        bm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(menu_intent);
            }
        });

        final Intent game_intent = new Intent(this, GameActivity.class);
        final Button bsg = (Button) findViewById(R.id.rst_btn);
        bsg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                game_intent.putExtra("LEVEL", level);
                startActivity(game_intent);
            }
        });
    }
}
