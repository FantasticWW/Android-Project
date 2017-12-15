package com.example.com.videoviewdemo;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        initData();
        setPlayEvent();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baishi);
        videoView.setVideoURI(uri);
        videoView.start();
        handler.sendEmptyMessage(UPDATE);
    }


    private void initView() {
        videoView = findViewById(R.id.vedioview);
        imgPlayControl = findViewById(R.id.img_play_control);
        seekProgress = findViewById(R.id.seek_progress);
        seekVolum = findViewById(R.id.seek_volum);
        imgScreen = findViewById(R.id.img_screen);
        txtCurrTime = findViewById(R.id.txt_current_time);
        txtTotalTime = findViewById(R.id.txt_total_time);
    }

    private void initData() {
        //播放控制条
//        videoView.setMediaController(new MediaController(this));
//        Uri uri = Uri.parse(URL);
////        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baishi);
//        videoView.setVideoURI(uri);
//        videoView.start();
    }

    /**
     * 更新TextView的时间
     *
     * @param textView
     * @param millisecond
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
                int currPosition = videoView.getCurrentPosition();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(UPDATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
