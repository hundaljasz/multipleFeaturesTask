package com.example.multiplefeaturestask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button camera, PA,OAP;
    ImageView imageView;

    private MediaPlayer mediaPlayer,MPO;
    private Button playButton;
    private Button pauseButton;
    private SeekBar seekBar;
    private boolean isAudioPlaying = false;
    private String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3";

    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = findViewById(R.id.camera);
        imageView = findViewById(R.id.imageView);
        OAP = findViewById(R.id.OAP);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        seekBar = findViewById(R.id.seek_bar);

        Uri uri = Uri.parse(audioUrl);
        MPO = new MediaPlayer();
        MPO.setAudioStreamType(AudioManager.STREAM_MUSIC);

        camera.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        });

        OAP.setOnClickListener(view -> {
            if (MPO.isPlaying()) {
                MPO.pause();
                playButton.setText("Play");
            } else {
                try {
                    MPO.setDataSource(MainActivity.this,uri);
                    MPO.prepare();
                    MPO.start();
                    playButton.setText("Pause");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        playButton.setOnClickListener(view -> {

            mediaPlayer = MediaPlayer.create(this, R.raw.audio_file);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    updateSeekBar();
                }
            });
            if (!isAudioPlaying) {
                mediaPlayer.start();
                isAudioPlaying = true;
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
            }
        });

        pauseButton.setOnClickListener(view -> {
            if (isAudioPlaying) {
                mediaPlayer.pause();
                isAudioPlaying = false;
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }
        });
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (isAudioPlaying) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            seekBar.postDelayed(runnable, TimeUnit.SECONDS.toMillis(1));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        MPO.release();
        MPO = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}