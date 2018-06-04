package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.Global;
import com.longhorn.dvrexplorer.http.FlyOkHttp;
import com.longhorn.dvrexplorer.http.IHttp;
import com.longhorn.dvrexplorer.adapter.VideoAdapater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-2-下午4:14.
 */

public class VideoEvtFragment extends Fragment implements IHttp.HttpResult {

    public VideoEvtFragment(){

    }

    private final String HTTPTAG = "VideoActivity" + Math.random();
    private IHttp iHttp = FlyOkHttp.getInstance();
    private RecyclerView rv01;
    private List<String> rvList = new ArrayList<>();
    private VideoAdapater videoAdapater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nor_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        rv01 = view.findViewById(R.id.ac_video_rv01);
        rv01.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        videoAdapater = new VideoAdapater(getActivity(), rvList, rv01);
        rv01.setAdapter(videoAdapater);
    }

    @Override
    public void onStart() {
        super.onStart();
        iHttp.getString(Global.EVT_PATH, HTTPTAG, this);
    }


    @Override
    public void onStop() {
        iHttp.cancelAll(HTTPTAG);
        videoAdapater.cancleAllTask();
        super.onStop();
    }

    @Override
    public void succeed(Object object) {
        if (object != null) {
            String str = (String) object;
            Pattern pattern = Pattern.compile("[0-9]{8}_[0-9]{6}.[M,m][P,p][4]");
            Matcher matcher = pattern.matcher(str);
            Set<String> set = new HashSet<>();
            while (matcher.find()) {
                String address = Global.EVT_PATH +"/" + matcher.group(0);
                set.add(address);
            }

            List<String> list = new ArrayList<>();

            list.addAll(set);

            if (!list.isEmpty()) {

                Collections.sort(list, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return rhs.compareTo(lhs);
                    }
                });

                rvList.clear();
                rvList.addAll(list);
                videoAdapater.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void failed(Object object) {

    }
}
