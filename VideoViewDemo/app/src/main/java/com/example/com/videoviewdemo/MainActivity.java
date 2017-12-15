package com.example.com.videoviewdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private static String URL = "http://fairee.vicp.net:83/2016rm/0116/baishi160116.mp4";
    private static int UPDATE = 0;
    private VideoView videoView;
    private ImageView imgPlayControl; //暂停
    private SeekBar seekProgress; //进度条
    private SeekBar seekVolum;//音量条
    private ImageView imgScreen; //全屏键
    private TextView txtCurrTime;//当前时间
    private TextView txtTotalTime;//总时间
    private AudioManager audioManager;//音量控制
    private int screenWidth;
    private int screenHeight;
    private RelativeLayout videoLayout;
    private int currPosition;//当前视频播放位置
    private boolean isFullScreen;//是否全屏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setPlayEvent();
    }

    private void initView() {
        videoView = findViewById(R.id.vedioview);
        imgPlayControl = findViewById(R.id.img_play_control);
        seekProgress = findViewById(R.id.seek_progress);
        seekVolum = findViewById(R.id.seek_volum);
        imgScreen = findViewById(R.id.img_screen);
        txtCurrTime = findViewById(R.id.txt_current_time);
        txtTotalTime = findViewById(R.id.txt_total_time);
        videoLayout = findViewById(R.id.vedioview_layout);
    }

    private void initData() {
        //播放控制条
//        videoView.setMediaController(new MediaController(this));
//        Uri uri = Uri.parse(URL);
////        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baishi);
//        videoView.setVideoURI(uri);
//        videoView.start();

        //音量初始化
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolum = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolum.setMax(maxVolum);
        seekVolum.setProgress(currVolum);

        //获取屏幕的宽度
        WindowManager manager = getWindowManager();
//        screenWidth = manager.getDefaultDisplay().getWidth();
//        screenHeight = manager.getDefaultDisplay().getHeight();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        //video初始化
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baishi);
        videoView.setVideoURI(uri);
        videoView.start();
        handler.sendEmptyMessage(UPDATE);
    }

    /**
     * 更新TextView的时间
     *
     * @param textView    文本框
     * @param millisecond 时间
     */
    private void updateFormatTime(TextView textView, int millisecond) {
        int second = millisecond / 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String time = null;
        if (hh != 0) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            time = String.format("%02d:%02d", mm, ss);
        }
        textView.setText(time);
    }

    /**
     * 通过handler来控制时间
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE) {
                //获取视频当前的播放时间
                currPosition = videoView.getCurrentPosition();
                //获取视频的总时间
                int totalDuration = videoView.getDuration();
                //格式化
                updateFormatTime(txtCurrTime, currPosition);
                updateFormatTime(txtTotalTime, totalDuration);
                seekProgress.setMax(totalDuration);
                seekProgress.setProgress(currPosition);
                handler.sendEmptyMessageDelayed(UPDATE, 500);
            }
        }
    };


    private void setPlayEvent() {
        /**
         * 控制视频的播放和暂停
         */
        imgPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    imgPlayControl.setImageResource(R.drawable.play_btn_style);
                    videoView.pause();
                    handler.removeMessages(UPDATE);
                } else {
                    imgPlayControl.setImageResource(R.drawable.pause_btn_sylte);
                    videoView.start();
                    handler.sendEmptyMessage(UPDATE);
                }
            }
        });
        /**
         * 播放进度条控制
         */
        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateFormatTime(txtCurrTime, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(UPDATE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                videoView.seekTo(progress);
                handler.sendEmptyMessage(UPDATE);
            }
        });

        /**
         * 音量条控制
         */
        seekVolum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, UPDATE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 播放完毕监听
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgPlayControl.setImageResource(R.drawable.play_btn_style);
            }
        });
        /**
         * 屏幕切换
         */
        imgScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {//全屏
                    imgScreen.setImageResource(R.drawable.enlarge);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    imgScreen.setImageResource(R.drawable.shrink);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
    }

    /**
     * 设置缩放比例
     */
    private void setVedioViewScale(int width, int height) {
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = videoLayout.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        videoLayout.setLayoutParams(layoutParams1);
    }


    /**
     * 控制屏幕显示的大小
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //横盘
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVedioViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            isFullScreen = true;
        } else {
            //竖屏
            setVedioViewScale(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(250));
            isFullScreen = false;
        }
    }

    /**
     * 音量键控制
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int currVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                currVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                currVolum = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                break;
        }
        seekVolum.setProgress(currVolum);
        return super.onKeyDown(keyCode, event);

    }

    /**
     * dp转化为px
     */
    public int dp2px(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    /**
     * 暂停时控制
     */
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(UPDATE);
        videoView.getCurrentPosition();
        imgPlayControl.setImageResource(R.drawable.play_btn_style);
    }

    /**
     * 重新进入时控制
     */
    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(currPosition);
        seekProgress.setProgress(currPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
