package com.longhorn.dvrexplorer.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.fragment.PlayVideoFragment;


public class PlayVideoActivity extends Activity {
    public static final int VIDEO_NORMAL = 0;
    public static final int VIDEO_PLAY = 1;
    public static final int VIDEO_PAUSE = 2;
    public static int videoPlayStatus = 0;
    public final static String PLAY_URL = "PLAY_URL";
    public Intent intent = null;

    public static String mPalyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);

        intent = getIntent();
        mPalyUrl = intent.getStringExtra(PLAY_URL);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PlayVideoFragment fragment_video = new PlayVideoFragment();
        ft.replace(R.id.playvideo_fl01, fragment_video);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        videoPlayStatus = VIDEO_NORMAL;
        super.onBackPressed();
    }

}
