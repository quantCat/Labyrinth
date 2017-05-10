package com.example.veronika.ball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Map;
import java.util.TreeMap;

public class MapChooseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_chooser_activity);
        Map<String, String> map_list = getMapList();
        RadioGroup radios = (RadioGroup)findViewById(R.id.maplist);

        for (String i: map_list.keySet()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(i);
            radios.addView(rb);
            Log.i("MapChooseActivity", String.format("added map: %s", i));
        }
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        RadioGroup radios = (RadioGroup)findViewById(R.id.maplist);
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
        Map<String, Integer> mapIDList = getMapIdList();
        RadioButton rb = (RadioButton) findViewById(id);
        int mapId = mapIDList.get(rb.getText());
        Intent game_intent = new Intent(this, GameActivity.class);
        game_intent.putExtra("MAP", mapId);
        startActivity(game_intent);

        return super.onMenuItemSelected(featureId, item);
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
