package com.example.veronika.ball;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by veronika on 18/04/2017.
 */

abstract class MusicService extends Service {
    private static final String TAG = "Service";
    MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();


        player = MediaPlayer.create(this, getMusicId());
        player.setLooping(true); // зацикливаем
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        player.stop();
    }

    @Override
    public void onStart (Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        player.start();
    }

    int getMusicId() {
        throw new RuntimeException("No music id");
    }
}

class MusicServiceGame extends MusicService {
    @Override
    int getMusicId() {
        return R.raw.tobyfox_waterfall;
    }
}

class MusicServiceMenu extends MusicService {
    @Override
    int getMusicId() {
        return R.raw.epiphany;
    }
}

class MusicServiceSet extends MusicService {

    @Override
    int getMusicId() {
        return R.raw.eric_carmen__all_by_myself;
    }
}
