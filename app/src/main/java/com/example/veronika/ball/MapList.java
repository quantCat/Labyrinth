package com.example.veronika.ball;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by veronika on 27/05/2017.
 */

final class MapList {
    public static Map<String, String> getMapList() {
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

    public static Map<String, Integer> getMapIdList() {
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
