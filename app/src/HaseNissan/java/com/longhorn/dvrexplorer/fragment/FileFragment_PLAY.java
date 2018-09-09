package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.longhorn.dvrexplorer.DVRActivity;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class FileFragment_PLAY extends Fragment implements
        View.OnClickListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener,
        View.OnTouchListener {
    public static final String LIST_KEY = "LIST_KEY";
    public static final String POS_KEY = "POS_KEY";
    private List<DvrFile> mList;
    private int mPos;
    private int mSize;
    private IjkVideoView ijkVideoView;
    private Button bt_return, bt_left, bt_right, bt_play;
    private TextView sktv01, sktv02;
    private TextView seekRight = null;//快进调节
    private TextView seekLeft = null;//快退调节
    private SeekBar seekBar;
    private int videoTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean isPlay = false;
    private String mVideoTimeStr = "00:00";

    public FileFragment_PLAY() {
    }

    Runnable taskSetSeekBar = new Runnable() {
        @Override
        public void run() {
            setSeekBar();
            mHandler.postDelayed(taskSetSeekBar, 1000);
        }
    };

    public static FileFragment_PLAY newInstance(ArrayList<DvrFile> list, int pos) {
        Bundle args = new Bundle();
        args.putInt(POS_KEY, pos);
        args.putParcelableArrayList(LIST_KEY, list);
        FileFragment_PLAY fragment = new FileFragment_PLAY();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mList = args.getParcelableArrayList(LIST_KEY);
        mPos = args.getInt(POS_KEY);
        mSize = mList == null ? 0 : mList.size();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_play, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ijkVideoView = view.findViewById(R.id.file_video_vv);
        bt_return = view.findViewById(R.id.file_pho_return);
        bt_left = view.findViewById(R.id.file_video_left);
        bt_play = view.findViewById(R.id.file_video_play);
        bt_right = view.findViewById(R.id.file_video_right);
        sktv01 = view.findViewById(R.id.file_video_sktv1);
        sktv02 = view.findViewById(R.id.file_video_sktv2);
        seekBar = view.findViewById(R.id.file_video_sk);
        seekRight = view.findViewById(R.id.fragment_video_tv07);
        seekLeft = view.findViewById(R.id.fragment_video_tv08);
        seekRight.getBackground().setAlpha(128);
        seekLeft.getBackground().setAlpha(128);

        seekBar.setAlpha(0.7f);

        ijkVideoView.setOnCompletionListener(this);
        ijkVideoView.setOnErrorListener(this);
        ijkVideoView.setOnInfoListener(this);
        ijkVideoView.setOnPreparedListener(this);
        ijkVideoView.setOnTouchListener(this);

        bt_return.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        bt_right.setOnClickListener(this);


        if (mSize == 0) {
            bt_left.setEnabled(false);
            bt_right.setEnabled(false);
        } else {
            upLeftRightButtonState();
        }
        play();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_pho_return:
                ijkVideoView.stopPlayback();
//                getActivity().onBackPressed();
                try {
                    if(mList.get(0).type==0){
                        ((DVRActivity) getActivity()).replaceFragment("FileFragment_NOR1");
                    }else{
                        ((DVRActivity) getActivity()).replaceFragment("FileFragment_EVT1");
                    }
                }catch (Exception e){
                    FlyLog.e(e.toString());
                }
                break;
            case R.id.file_video_left:
                playLeft();
                break;
            case R.id.file_video_right:
                playRight();
                break;
            case R.id.file_video_play:
                if (mSize != 0) {
                    if (isPlay) {
                        ijkVideoView.pause();
                        isPlay = false;
                        mHandler.removeCallbacks(taskSetSeekBar);
                    } else {
                        ijkVideoView.start();
                        isPlay = true;
                        mHandler.removeCallbacks(taskSetSeekBar);
                        mHandler.post(taskSetSeekBar);
                    }
                    bt_play.setBackgroundResource(isPlay ? R.drawable.file_video_pause : R.drawable.file_video_play);
                }
                break;
        }
    }

    private void playLeft() {
        if (mPos > 0) {
            mPos--;
            upLeftRightButtonState();
            play();
        }
    }

    private void playRight() {
        if (mPos < mSize - 1) {
            mPos++;
            upLeftRightButtonState();
            play();
        }
    }

    private void play() {
        ijkVideoView.stopPlayback();
        ijkVideoView.setVideoPath(mList.get(mPos).getPlayUrl());
        ijkVideoView.start();
        isPlay = true;
    }

    private void upLeftRightButtonState() {
        bt_left.setEnabled(mPos > 0);
        bt_right.setEnabled(mPos < mSize - 1);
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        FlyLog.d("onCompletion");
        seekBar.setProgress(videoTime);
        sktv01.setText(sktv02.getText());
        mHandler.removeCallbacks(taskSetSeekBar);
        isPlay = false;
        bt_play.setBackgroundResource(R.drawable.file_video_play);
        playLeft();
    }

    private void setSeekBar() {
        int playPos = ijkVideoView.getCurrentPosition();
        int min = playPos / 1000 / 60;
        int sec = playPos / 1000 % 60;
        String text = min + ":" + (sec > 9 ? sec : "0" + sec);
        sktv01.setText(text);
        seekBar.setProgress(playPos);
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        FlyLog.d("onError, what=%d,extra=%d", what, extra);
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        FlyLog.d("onInfo, what=%d,extra=%d", what, extra);
        if (what == 3) {
            bt_play.setBackgroundResource(R.drawable.file_video_pause);
        }
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        FlyLog.d("onPrepared");
        videoTime = ijkVideoView.getDuration();
        int max = videoTime / 1000;
        mVideoTimeStr = max % 60 < 10 ? max / 60 + ":0" + max % 60 : max / 60 + ":" + max % 60;
        seekBar.setMax(videoTime);
        int min = videoTime / 1000 / 60;
        int sec = videoTime / 1000 % 60;
        String text = min + ":" + (sec > 9 ? sec : "0" + sec);
        sktv02.setText(text);
        mHandler.removeCallbacks(taskSetSeekBar);
        mHandler.post(taskSetSeekBar);
    }

    private int x, seekto;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.file_video_vv:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        seekRight.setVisibility(View.GONE);
                        seekLeft.setVisibility(View.GONE);
                        ijkVideoView.seekTo(seekto);
                        ijkVideoView.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x1 = (int) event.getX();
                        if (x1 > x) {
                            seekRight.setVisibility(View.VISIBLE);
                            seekLeft.setVisibility(View.GONE);
                        } else {
                            seekRight.setVisibility(View.GONE);
                            seekLeft.setVisibility(View.VISIBLE);
                        }
                        seekto = (int) ((x1 - x) / Math.max(640, v.getWidth()) * videoTime) + ijkVideoView.getCurrentPosition();
                        if (seekto < 0) {
                            seekto = 0;
                        } else if (seekto > videoTime) {
                            seekto = videoTime;
                        }
                        String seektime = null;
                        int num = seekto / 1000;
                        if (num % 60 < 10) {
                            seektime = (num / 60 + ":0" + num % 60);
                        } else {
                            seektime = (num / 60 + ":" + num % 60);
                        }
                        if (x1 > x) {
                            seekRight.setText(Html
                                    .fromHtml("<font color=red>" + seektime
                                            + "</font><font color=green>/"
                                            + mVideoTimeStr
                                            + "</font>"));
                        } else {
                            seekLeft.setText(Html
                                    .fromHtml("<font color=red>" + seektime
                                            + "</font><font color=green>/"
                                            + mVideoTimeStr
                                            + "</font>"));
                        }
                }
                return true;
        }
        return false;
    }
}
