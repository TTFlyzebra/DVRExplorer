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
import com.longhorn.dvrexplorer.adapter.PhotoAdapater;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-上午11:48.
 */

public class PhotoFragment extends Fragment implements IHttp.HttpResult{
    private final String HTTPTAG = "PhotoActivity" + Math.random();
    private IHttp iHttp = FlyOkHttp.getInstance();
    private RecyclerView rv01;
    private List<String> rvList = new ArrayList<>();
    private PhotoAdapater photoAdapater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rv01 = view.findViewById(R.id.ac_photo_rv01);
        rv01.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        photoAdapater = new PhotoAdapater(getActivity(), rvList);
        rv01.setAdapter(photoAdapater);
    }

    @Override
    public void onStart() {
        super.onStart();
        iHttp.getString(Global.PHO_PATH, HTTPTAG, this);
    }

    @Override
    public void onStop() {
        iHttp.cancelAll(HTTPTAG);
        super.onStop();
    }

    @Override
    public void succeed(Object object) {
        if (object != null) {
            String str = (String) object;
            Pattern pattern = Pattern.compile("[0-9]{8}_[0-9]{6}.[J,j][P,p][G,g]");
            Matcher matcher = pattern.matcher(str);
            Set<String> set = new HashSet<>();
            while (matcher.find()) {
                String address = Global.PHO_PATH+"/" + matcher.group(0);
                set.add(address);
            }

            List<String> list = new ArrayList<>();

            list.addAll(set);

            if (!list.isEmpty()) {
                rvList.clear();
                rvList.addAll(list);
                photoAdapater.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void failed(Object object) {
        FlyLog.d(""+object);
    }
}
