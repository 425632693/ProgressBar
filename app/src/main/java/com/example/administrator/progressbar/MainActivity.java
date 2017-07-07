package com.example.administrator.progressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.progressbar.view.HorizontalProgressbar;
import com.example.administrator.progressbar.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_UPDATE = 101;
    private HorizontalProgressbar mHProgress;

    private RoundProgressBar mRProgress;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            int progress = mHProgress.getProgress();
            mHProgress.setProgress(++progress);
            //圆形进度条
            mRProgress.setProgress(++progress);

            if(progress>=100){
                mHandler.removeMessages(MSG_UPDATE);
            }
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHProgress = (HorizontalProgressbar) findViewById(R.id.id_progressbar);
        //圆形进度条
        mRProgress = (RoundProgressBar) findViewById(R.id.id_roundProgress);

        mHandler.sendEmptyMessage(MSG_UPDATE);

    }
}
