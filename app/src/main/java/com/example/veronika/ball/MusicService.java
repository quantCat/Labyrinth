package com.example.veronika.ball;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by veronika on 18/04/2017.
 */

abstract class MusicService extends Service {
    private static final String TAG = "Service";
    MediaPlayer player = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("MusicService", String.format("onCreate(): %s", this.getClass().getName()));
        //-Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        player = MediaPlayer.create(this, getMusicId());
        player.setLooping(true); // зацикливаем
    }

    @Override
    public void onDestroy() {
        Log.i("MusicService",  String.format("onDestroy(): %s", this.getClass().getName()));
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart (Intent intent, int startid) {
        // NB onStart() is usually called twice! That's why don't create player here without
        // existence check.
        Log.i("MusicService", String.format("onStart(): %s", this.getClass().getName()));
        //-Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        player.start();
    }

    int getMusicId() {
        throw new RuntimeException("No music id");
    }
}

class MusicServiceGame extends MusicService {
    @Override
    int getMusicId() {
        return R.raw.bonanza;
    }
}

class MusicServiceMenu extends MusicService {
    @Override
    int getMusicId() {
        return R.raw.beta_love;
    }
}
