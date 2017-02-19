package com.at.remind.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.at.remind.R;

/**
 * Created by user on 16-10-25.
 */

public class SoundPlayer {
    private static AudioManager mAudioManager;
    public static void playSouned(Context context,int id) {
        MediaPlayer mp = MediaPlayer.create(context,id);
        mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        final int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        String v=SP.getString(context,SP.AT_REMIND_VOLUME);
        int volume=8;
        if(v!=null&&!"".equals(v)){
            volume=Integer.valueOf(v);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,current,0);
            }
        });
    }

    /**
     * 播放声音提醒用户
     * @param context
     */
    public static void playSouned(Context context) {
        //得到用户设置的声音的uri
        String s=SP.getString(context,SP.AT_REMIND_SOUND_URI);
        L.i("atremind","s= "+s);
        Uri uri=null;
        //创建一个MediaPlayer通过uri
        MediaPlayer mp = null;
        if(s==null||"".equals(s.trim())){
            mp=MediaPlayer.create(context, R.raw.at_remind_2);
        }else{
            try {
                uri=Uri.parse(s);
                mp=MediaPlayer.create(context,uri);
            }catch (Exception e){
                mp=MediaPlayer.create(context, R.raw.at_remind_2);
            }
        }
        //得到系统的声音管理器服务
        mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //获取系统当前音乐流的声音大小，并且记录下来，用于后面恢复
        final int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        //设置播放生硬的流为音乐流
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //得到用户设置的提示音的大小
        String v=SP.getString(context,SP.AT_REMIND_VOLUME);
        int volume=8;
        if(v!=null&&!"".equals(v)){
            volume=Integer.valueOf(v);
        }
        //设置音乐流播放提示音为用户设置的提示音的大小
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
        //判断当前是否有声音在播放，如果在播放就停止
        if (mp.isPlaying()) {
            mp.stop();
        }
        //播放提示音提醒用户
        mp.start();
        //设置声音播放玩的监听
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //当提示音播放完成过后，回复音乐流最初的声音大小
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,current,0);
            }
        });
    }

}
