package com.medcorp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.medcorp.R;

/**
 * Created by med on 16/12/30.
 */

public class SoundPlayer {

    public static void PlayFromRawFile(Context context,int resRawId)
    {
        final MediaPlayer play = MediaPlayer.create(context, resRawId);
        final long currentTime = System.currentTimeMillis();
        play.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume , 0);
        if(play.isPlaying())
        {
            play.stop();
        }
        play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //if ring duration < 3s,play it twice time
                if (System.currentTimeMillis() - currentTime < 3000)
                {
                    play.start();
                }
            }
        });
        play.start();
    }
}
