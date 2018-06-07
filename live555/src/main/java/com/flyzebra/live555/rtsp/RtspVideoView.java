package com.flyzebra.live555.rtsp;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.flyzebra.live555.decoder.MediaDecoder;
import com.flyzebra.live555.utils.MyLog;

/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is RtspVideoView
 */
public class RtspVideoView extends SurfaceView implements SurfaceHolder.Callback, IRtspCallBack {
    private String rtspUrl;
    private RtspClient rtspClient;
    private MediaDecoder mediaDecoder;
    private boolean isPlaying = false;
    private boolean isShow = false;

    public RtspVideoView(Context context) {
        this(context, null);
    }

    public RtspVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RtspVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
        if (isShow) {
            if (isPlaying) {
                stopPlay();
            }
            startPlay();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isShow = true;
        startPlay();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isShow = false;
        stopPlay();
    }

    private void startPlay() {
        if (!TextUtils.isEmpty(rtspUrl)) {
            rtspClient = new RtspClient(this);
            rtspClient.connect(rtspUrl);
            isPlaying = true;
        }
    }

    private void stopPlay() {
        if (mediaDecoder != null) {
            mediaDecoder.stop();
            mediaDecoder = null;
        }
        if (rtspClient != null) {
            rtspClient.close();
            rtspClient = null;
        }
        isPlaying = false;
    }

    @Override
    public void onResult(String resultCode) {
        MyLog.d("rtspclient message:%s", resultCode);
    }

    @Override
    public void onVideo(byte[] videoBytes) {
        if (isPlaying && mediaDecoder != null) mediaDecoder.input(videoBytes);
    }

    @Override
    public void onAudio(byte[] audioBytes) {

    }

    @Override
    public void onRecvRTP(byte[] sps, byte[] pps) {
        if (isPlaying) mediaDecoder = new MediaDecoder(getHolder().getSurface(), sps, pps);
    }

    @Override
    public void onStop() {
        mediaDecoder.stop();
    }
}
