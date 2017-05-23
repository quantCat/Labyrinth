package com.example.veronika.ball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class MapChooseActivity extends Activity {

    int map_ids[];
    boolean has_saving[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_chooser_activity);
        Map<String, String> map_list = getMapList();
        Map<String, Integer> map_id_list = getMapIdList();
        RadioGroup radios = (RadioGroup)findViewById(R.id.maplist);

        String storage_path = getFilesDir().getAbsolutePath();
        map_ids = new int[map_list.size()];
        has_saving = new boolean[map_list.size()];
        int map_index = 0;
        for (String map_name: map_list.keySet()) {
            RadioButton rb = new RadioButton(this);
            rb.setId(map_index);
            String item_name = map_name;
            File saving_path = new File(storage_path + "/" + Integer.toString(map_index) + ".save");
            if (saving_path.canRead()) {
                has_saving[map_index] = true;
                item_name = item_name + " (saved)";
            }

            File saved_result_path = new File(storage_path + "/" + Integer.toString(map_index) + ".result");
            if (saved_result_path.canRead()) {
                int stars = 0;
                try {
                    //-FileInputStream saving = openFileInput(getResultFileName());
                    FileInputStream saving = new FileInputStream(saved_result_path);
                    Scanner sc = new Scanner(saving);
                    stars = sc.nextInt();
                } catch(java.io.FileNotFoundException _e) {
                    Toast.makeText(this, "Saving file disappeared", Toast.LENGTH_LONG).show();
                    finish();
                }
                Log.i("MapChooseActivity", String.format("Map_name=%s map_index=%d stars=%d saved_result_path=%s",
                            map_name, map_index, stars, saved_result_path));
                item_name = item_name + String.format(" <Passed. Stars:%d>", stars);
            }
            rb.setText(item_name);

            map_ids[map_index] = map_id_list.get(map_name);
            radios.addView(rb);
            Log.i("MapChooseActivity", String.format("added map: %s: %s", map_name, item_name));
            ++map_index;
        }
        final MapChooseActivity self = this;
        Button b;
        b = (Button) findViewById(R.id.button_new_game);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startGame(false);
            }
        });
        b = (Button) findViewById(R.id.button_continue_game);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startGame(true);
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This means the button "back" is pressed on screen
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }

        /////

        return super.onMenuItemSelected(featureId, item);
    }

    void startGame(boolean shall_continue) {
        // TODO
        RadioGroup rg = (RadioGroup) findViewById(R.id.maplist);
        int selected = rg.getCheckedRadioButtonId();
        if (shall_continue && !has_saving[selected]) {
            return;
        }
        if (selected >= 0) {
            Intent intent = new Intent(this, GameActivity.class).
                    putExtra("MAP", map_ids[selected]).
                    putExtra("CONTINUE", shall_continue).
                    putExtra("SAVING_ID", selected);
            startActivity(intent);
            finish();
        }
    }

    public Map<String, String> getMapList() {
        Map r = new TreeMap<>();
        r.put("Map0_tutorial", "map0_tutorial");
        r.put("Map1", "map1");
        r.put("Map2", "map2");
        r.put("Map3", "map3");
        r.put("Map4", "map4");
        r.put("Map5", "map5");
        r.put("Map6", "map6");
        r.put("Map7", "map7");
        r.put("Map_tester", "map");
        return r;
    }

    public Map<String, Integer> getMapIdList() {
        Map r = new TreeMap<>();
        r.put("Map0_tutorial", R.raw.map0_tutorial);
        r.put("Map1", R.raw.map1);
        r.put("Map2", R.raw.map2);
        r.put("Map3", R.raw.map3);
        r.put("Map4", R.raw.map4);
        r.put("Map5", R.raw.map5);
        r.put("Map6", R.raw.map6);
        r.put("Map7", R.raw.map7);
        r.put("Map_tester", R.raw.map);
        return r;
    }
}
