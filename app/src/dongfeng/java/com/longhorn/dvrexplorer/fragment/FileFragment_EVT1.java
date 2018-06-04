package com.longhorn.dvrexplorer.fragment;

import android.os.Bundle;
import android.view.View;

import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.module.wifi.CommandType;
import com.longhorn.dvrexplorer.DVRActivity;

import java.util.ArrayList;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class FileFragment_EVT1 extends FileFragment_BaseGrid {

    public FileFragment_EVT1() {
    }

    public static FileFragment_EVT1 newInstance() {
        Bundle args = new Bundle();
        FileFragment_EVT1 fragment = new FileFragment_EVT1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public byte[] getCommandType() {
        return CommandType.GET_FILE_EVT;
    }

    @Override
    public void onItemClick(View view, int pos) {
        FileFragment_PLAY fragment = FileFragment_PLAY.newInstance((ArrayList<DvrFile>) mList,pos);
        ((DVRActivity)getActivity()).addFragment(this,fragment);
    }

}
