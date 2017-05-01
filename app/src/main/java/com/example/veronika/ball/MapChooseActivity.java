package com.example.veronika.ball;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Map;

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
    }

    public Map<String, String> getMapList() {
        Map r = new HashMap<>();
        r.put("Map", "map");
        return r;
    }
}
