package com.android.imran.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {

    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    SeekBar mSeekBar;
    Button btnPlay, btnFF, btnFB, btnNext, btnPrev;

    Uri uri;
    Thread updateSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnFF = (Button) findViewById(R.id.btnFF);
        btnFB = (Button) findViewById(R.id.btnFW);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);

        if (mp != null) {
            mp.stop();
            mp.release();
        }

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPostion = 0;

                while (currentPostion < totalDuration) {
                    try {
                        sleep(500);
                        currentPostion = mp.getCurrentPosition();
                        mSeekBar.setProgress(currentPostion);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnFF.setOnClickListener(this);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        position = bundle.getInt("pos", 0);

        uri = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), uri);
        mp.start();
        mSeekBar.setMax(mp.getDuration());
        updateSeekBar.start();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnPlay:
                if (mp.isPlaying()) {
                    btnPlay.setText(">");
                    mp.pause();
                } else {
                    btnPlay.setText("||");
                    mp.start();
                }
                break;

            case R.id.btnFF:
                mp.seekTo(mp.getCurrentPosition() + 5000);
                break;
            case R.id.btnFW:
                mp.seekTo(mp.getCurrentPosition() - 5000);
                break;
            case R.id.btnNext:
                mp.stop();
                mp.release();
                position = (position + 1) % mySongs.size();//update postion ;
                uri = Uri.parse(mySongs.get(position + 1).toString());
                mp = MediaPlayer.create(getApplicationContext(), uri);
                mSeekBar.setMax(mp.getDuration());
                mp.start();
                break;
            case R.id.btnPrev:
                mp.stop();
                mp.release();
                // position = (position + 1) % mySongs.size();//update postion ;
                position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
//                if (position - 1 > 0) {
//                    position = mySongs.size() - 1;
//                } else {
//                    position = position - 1;
//                }
                uri = Uri.parse(mySongs.get(position + 1).toString());
                mp = MediaPlayer.create(getApplicationContext(), uri);
                mSeekBar.setMax(mp.getDuration());
                mp.start();
        }
    }
}
