package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.flydown.download.DownFileBean;
import com.flyzebra.flydown.download.DownFileManager;
import com.flyzebra.flydown.download.IDownListener;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.adapter.VideoLocAdapater;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-下午4:14.
 */

public class VideoLocFragment extends Fragment implements IDownListener {

    public VideoLocFragment(){
    }

    private RecyclerView rv01;
    private List<DownFileBean> rvList = new ArrayList<>();
    private VideoLocAdapater videoLocAdapater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nor_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        rv01 = view.findViewById(R.id.ac_video_rv01);
        rv01.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        videoLocAdapater = new VideoLocAdapater(getActivity(), rvList,rv01);
        rv01.setAdapter(videoLocAdapater);
    }

    @Override
    public void onStart() {
        super.onStart();
        DownFileManager.getInstance().addDownListener(this);
    }

    @Override
    public void upListData(List<DownFileBean> list) {
        if(list!=null){
            rvList.clear();
            rvList.addAll(list);
            videoLocAdapater.notifyDataSetChanged();
        }
    }

    @Override
    public void downInfo(List<DownFileBean> list, int speed) {
        if(list!=null){
            rvList.clear();
            rvList.addAll(list);
            videoLocAdapater.notifyDataSetChanged();
        }
    }
}
