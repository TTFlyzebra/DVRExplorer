package com.longhorn.dvrexplorer.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.flyzebra.flydown.download.DownFileManager;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.ui.PlayVideoActivity;
import com.longhorn.dvrexplorer.utils.FlyLog;

import tcking.github.com.giraffeplayer.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

//OnGestureListener
public class PlayVideoFragment extends Fragment implements OnClickListener,
        OnTouchListener, IMediaPlayer.OnCompletionListener, OnSeekBarChangeListener, IMediaPlayer.OnPreparedListener {
    private IjkVideoView mVideoView;
    private int mPosition;
    private int mVideoTime;
    private String mVideoTimeStr = "00:00";
    private View mProgressBarView;//加载中按钮

    public PlayVideoFragment() {
    }

    private RelativeLayout fragment_video_rl01 = null;//整个控制窗
    private RelativeLayout fragment_video_rl02 = null;//底部控制条
    private RelativeLayout fragment_video_rl03 = null;//顶部控制条
    private TextView fragment_video_tv01 = null;//视频名称
    private TextView fragment_video_tv02 = null;//SEEKBAR
    private TextView fragment_video_tv03 = null;//SEEKBAR
    private TextView fragment_video_tv05 = null;//声音调节
    private TextView fragment_video_tv06 = null;//亮度调节
    private TextView fragment_video_tv07 = null;//快进调节
    private TextView fragment_video_tv08 = null;//快退调节
    private SeekBar fragment_video_sk01 = null;
    private ImageView fragment_video_iv01 = null;//播放暂停
    private ImageView fragment_video_iv02 = null;//返回
    private ImageView fragment_video_iv03 = null;//下载


    private WindowManager.LayoutParams lp = null;
    private AudioManager mAudioManager = null;
    private int seekto = 0;

    private int screenwidth = 0;
    private int screenheight = 0;
    private float x = 0;
    private float y = 0;
    private boolean isVolume = false;
    private boolean isBrightness = false;
    private boolean isProgress = false;

    private final int HANDLER_SEEKBAR = 0;
    private VideoHandler mHandler = new VideoHandler();

    @SuppressLint("HandlerLeak")
    private class VideoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SEEKBAR:
                    mPosition = mVideoView.getCurrentPosition();
                    fragment_video_sk01.setProgress((int) mPosition);
                    int num = (int) (mPosition / 1000);
                    String str = num % 60 < 10 ? num / 60 + ":0" + num % 60 : num / 60 + ":" + num % 60;
                    fragment_video_tv02.setText(str);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenwidth = dm.widthPixels;
        screenheight = dm.heightPixels;
        lp = getActivity().getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playvideo, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragment_video_rl01 = view.findViewById(R.id.fragment_video_rl01);
        fragment_video_rl02 = view.findViewById(R.id.fragment_video_rl02);
        fragment_video_rl03 = view.findViewById(R.id.fragment_video_rl03);
        fragment_video_tv01 = view.findViewById(R.id.fragment_video_tv01);
        fragment_video_tv02 = view.findViewById(R.id.fragment_video_tv02);
        fragment_video_tv03 = view.findViewById(R.id.fragment_video_tv03);
        fragment_video_tv05 = view.findViewById(R.id.fragment_video_tv05);
        fragment_video_tv06 = view.findViewById(R.id.fragment_video_tv06);
        fragment_video_tv07 = view.findViewById(R.id.fragment_video_tv07);
        fragment_video_tv08 = view.findViewById(R.id.fragment_video_tv08);
        fragment_video_sk01 = view.findViewById(R.id.fragment_video_sk01);
        fragment_video_iv01 = view.findViewById(R.id.fragment_video_iv01);
        fragment_video_iv02 = view.findViewById(R.id.fragment_video_iv02);
        fragment_video_iv03 = view.findViewById(R.id.fragment_video_iv03);
        fragment_video_tv01.getBackground().setAlpha(0);
        fragment_video_rl02.getBackground().setAlpha(128);
        fragment_video_rl03.getBackground().setAlpha(128);
        fragment_video_tv05.getBackground().setAlpha(128);
        fragment_video_tv06.getBackground().setAlpha(128);
        fragment_video_tv07.getBackground().setAlpha(128);
        fragment_video_tv08.getBackground().setAlpha(128);


        fragment_video_iv01.setOnClickListener(this);
        fragment_video_iv02.setOnClickListener(this);
        fragment_video_iv03.setOnClickListener(this);
        fragment_video_sk01.setOnSeekBarChangeListener(this);

        mVideoView = view.findViewById(R.id.fragment_video_vv01);
        mVideoView.setOnTouchListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);

        mProgressBarView = view.findViewById(R.id.progressbar);
    }

    @Override
    public void onStart() {
        if (PlayVideoActivity.mPalyUrl != null) {
            if (PlayVideoActivity.mPalyUrl.startsWith("http://")) {
                fragment_video_iv02.setVisibility(View.VISIBLE);
            } else {
                fragment_video_iv02.setVisibility(View.GONE);
            }
        }
        fragment_video_tv01.setText(PlayVideoActivity.mPalyUrl);
        playVideoFile(PlayVideoActivity.mPalyUrl);
        super.onStart();
    }

    @Override
    public void onStop() {
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onStop();
    }

    private void playVideoFile(String playurl) {
        switch (PlayVideoActivity.videoPlayStatus) {
            case PlayVideoActivity.VIDEO_NORMAL:
                mProgressBarView.setVisibility(View.VISIBLE);
                mVideoView.setVideoPath(playurl);
                mVideoView.requestFocus();
                mVideoView.start();
                PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_PLAY;
                break;
            case PlayVideoActivity.VIDEO_PLAY:
                mProgressBarView.setVisibility(View.VISIBLE);
                mVideoView.setVideoPath(playurl);
                mVideoView.requestFocus();
                mVideoView.seekTo(mPosition);
                mVideoView.start();
                PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_PLAY;
                break;
            case PlayVideoActivity.VIDEO_PAUSE:
                mProgressBarView.setVisibility(View.VISIBLE);
                mVideoView.setVideoPath(playurl);
                mVideoView.requestFocus();
                mVideoView.seekTo(mPosition);
                mVideoView.start();
                PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_PAUSE;
                fragment_video_iv01.setImageResource(android.R.drawable.ic_media_play);
                break;
        }
        setSeekBar();
    }

    private void setSeekBar() {
        fragment_video_sk01.setMax(mVideoTime);
        fragment_video_tv03.setText(mVideoTimeStr);
        upSeekBar();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(seekBarTask);
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_video_vv01:
                break;
            //播放暂停
            case R.id.fragment_video_iv01:
                switch (PlayVideoActivity.videoPlayStatus) {
                    case PlayVideoActivity.VIDEO_PLAY:
                        mVideoView.pause();
                        PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_PAUSE;
                        fragment_video_iv01.setImageResource(android.R.drawable.ic_media_play);
                        break;
                    case PlayVideoActivity.VIDEO_PAUSE:
                        mVideoView.start();
                        PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_PLAY;
                        fragment_video_iv01.setImageResource(android.R.drawable.ic_media_pause);
                        break;
                }
                break;
            //返回
            case R.id.fragment_video_iv02:
                getActivity().finish();
                break;
            //下载图片
            case R.id.fragment_video_iv03:
                String url = PlayVideoActivity.mPalyUrl;
                int fir = url.lastIndexOf('/') + 1;
                String name = url.substring(fir);
                DownFileManager.getInstance().addDownUrl(url, name, url);
                Toast.makeText(getActivity(),"下载文件:"+PlayVideoActivity.mPalyUrl,Toast.LENGTH_LONG).show();
                break;
        }

    }


    private Runnable hideCtrlTask = new Runnable() {
        @Override
        public void run() {
            fragment_video_rl01.setVisibility(View.GONE);
        }
    };


    private void showCtrlView() {
        mHandler.removeCallbacks(hideCtrlTask);
        if (fragment_video_rl01.getVisibility() == View.VISIBLE) {
            fragment_video_rl01.setVisibility(View.GONE);
        } else {
            fragment_video_rl01.setVisibility(View.VISIBLE);
            mHandler.postDelayed(hideCtrlTask, 10000);
        }

    }

    private Runnable seekBarTask = new Runnable() {
        @Override
        public void run() {
            mPosition = mVideoView.getCurrentPosition();
            fragment_video_sk01.setProgress((int) mPosition);
            int num = (int) (mPosition / 1000);
            String str = num % 60 < 10 ? num / 60 + ":0" + num % 60 : num / 60 + ":" + num % 60;
            fragment_video_tv02.setText(str);
            mHandler.postDelayed(seekBarTask, 200);
        }
    };


    private void upSeekBar() {
        mHandler.removeCallbacks(seekBarTask);
        mHandler.post(seekBarTask);
    }


    @Override
    public void onCompletion(IMediaPlayer mp) {
        //播放完成后的处理
        mPosition = 0;
        PlayVideoActivity.videoPlayStatus = PlayVideoActivity.VIDEO_NORMAL;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mVideoView.seekTo(seekBar.getProgress());
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mp.start();
        // 获的视频播放时间长度
        fragment_video_iv01.setImageResource(android.R.drawable.ic_media_pause);
        mVideoTime = (int) mp.getDuration();
        int max = mVideoTime / 1000;
        mVideoTimeStr = max % 60 < 10 ? max / 60 + ":0" + max % 60 : max / 60 + ":" + max % 60;
        fragment_video_sk01.setMax(mVideoTime);
        fragment_video_tv03.setText(mVideoTimeStr);
        mp.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                FlyLog.d("onInfo what:" + what + " extra:" + extra);
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mProgressBarView.setVisibility(View.GONE);
                    mHandler.postDelayed(hideCtrlTask, 2000);
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                showCtrlView();
                break;
            case MotionEvent.ACTION_UP:
                if (isVolume || isBrightness || isProgress) {
                    if (isProgress) {
                        mVideoView.seekTo(seekto);
                    }
                    isVolume = false;
                    isBrightness = false;
                    isProgress = false;
                    fragment_video_tv05.setVisibility(View.GONE);
                    fragment_video_tv06.setVisibility(View.GONE);
                    fragment_video_tv07.setVisibility(View.GONE);
                    fragment_video_tv08.setVisibility(View.GONE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x1 = (int) event.getX();
                float y1 = (int) event.getY();
                if (isBrightness) {
                    fragment_video_tv06.setVisibility(View.VISIBLE);
                    float setnum = (y - y1) / screenheight + lp.screenBrightness;
                    if (setnum < 0.0f) {
                        setnum = 0.0f;
                    } else if (setnum > 1.0f) {
                        setnum = 1.0f;
                    }
                    lp.screenBrightness = setnum;
                    getActivity().getWindow().setAttributes(lp);
                    int i = (int) (setnum * 100);
                    fragment_video_tv06.setText(i + "%");
                    x = x1;
                    y = y1;
                } else if (isProgress) {
                    if (x1 > x) {
                        fragment_video_tv07.setVisibility(View.VISIBLE);
                        fragment_video_tv08.setVisibility(View.GONE);
                    } else {
                        fragment_video_tv07.setVisibility(View.GONE);
                        fragment_video_tv08.setVisibility(View.VISIBLE);
                    }
                    seekto = (int) ((int) ((x1 - x) / Math.max(1280, screenwidth) * mVideoTime) + mPosition);
                    if (seekto < 0) {
                        seekto = 0;
                    } else if (seekto > mVideoTime) {
                        seekto = mVideoTime;
                    }
                    String seektime = null;
                    int num = seekto / 1000;
                    if (num % 60 < 10) {
                        seektime = (num / 60 + ":0" + num % 60);
                    } else {
                        seektime = (num / 60 + ":" + num % 60);
                    }
                    if (x1 > x) {
                        fragment_video_tv07.setText(Html
                                .fromHtml("<font color=red>" + seektime
                                        + "</font><font color=green>/"
                                        + mVideoTimeStr
                                        + "</font>"));
                    } else {
                        fragment_video_tv08.setText(Html
                                .fromHtml("<font color=red>" + seektime
                                        + "</font><font color=green>/"
                                        + mVideoTimeStr
                                        + "</font>"));
                    }
                } else if (isVolume) {
                    fragment_video_tv05.setVisibility(View.VISIBLE);
                    if (mAudioManager == null) {
                        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    }
                    int music = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    int musicMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    FlyLog.i("music--->" + music);
                    FlyLog.i("musicMax--->" + musicMax);
                    int setmusic = (int) ((y - y1) * 30 / screenheight) + music;
                    setmusic = Math.max(setmusic, 0);
                    setmusic = Math.min(musicMax, setmusic);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, setmusic, 0);
                    fragment_video_tv05.setText(setmusic * 100 / musicMax + "%");
                    if (setmusic != music) {
                        x = x1;
                        y = y1;
                    }
                } else if (((x1 - x) > 30) || ((x1 - x) < -30)) {
                    x = x1;
                    y = y1;
                    isProgress = true;
                } else if (((y1 - y) > 30) || ((y1 - y) < -30)) {
                    if (x > screenwidth / 2) {
                        isVolume = true;
                    } else {
                        isBrightness = true;
                    }
                    x = x1;
                    y = y1;
                }
                break;
        }
        return true;
    }

}
