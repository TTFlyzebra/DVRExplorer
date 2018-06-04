package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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
        IMediaPlayer.OnErrorListener {
    public static final String LIST_KEY = "LIST_KEY";
    public static final String POS_KEY = "POS_KEY";
    private List<DvrFile> mList;
    private int mPos;
    private int mSize;
    private IjkVideoView ijkVideoView;
    private Button bt_return, bt_left, bt_right, bt_play;
    private TextView sktv01,sktv02;
    private SeekBar seekBar;
    private int videoTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean isPlay = false;

    public FileFragment_PLAY() {
    }

    Runnable taskSetSeekBar = new Runnable() {
        @Override
        public void run() {
            setSeekBar();
            mHandler.postDelayed(taskSetSeekBar,1000);
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

        seekBar.setAlpha(0.7f);

        ijkVideoView.setOnCompletionListener(this);
        ijkVideoView.setOnErrorListener(this);
        ijkVideoView.setOnInfoListener(this);
        ijkVideoView.setOnPreparedListener(this);

        bt_return.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        bt_right.setOnClickListener(this);


        if(mSize==0){
            bt_left.setEnabled(false);
            bt_right.setEnabled(false);
        }else{
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
                getActivity().onBackPressed();
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
                    } else {
                        ijkVideoView.start();
                        isPlay = true;
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

    private void play(){
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
        int min = playPos/1000/60;
        int sec = playPos/1000%60;
        String text = min+":"+(sec>9?sec:"0"+sec);
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
        seekBar.setMax(videoTime);
        int min = videoTime/1000/60;
        int sec = videoTime/1000%60;
        String text = min+":"+(sec>9?sec:"0"+sec);
        sktv02.setText(text);
        mHandler.post(taskSetSeekBar);
    }

}
