package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.live555.rtsp.RtspVideoView;
import com.longhorn.dvrexplorer.R;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class RtspViewFragment extends Fragment{
    private RtspVideoView rtspVideoView;
    public RtspViewFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_rtspview,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rtspVideoView = view.findViewById(R.id.fm_rtspview_rtspvv);
        rtspVideoView.setRtspUrl("rtsp://192.168.42.1/live");
        super.onViewCreated(view, savedInstanceState);
    }
}
