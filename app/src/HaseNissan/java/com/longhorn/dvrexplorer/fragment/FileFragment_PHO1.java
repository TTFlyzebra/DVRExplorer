package com.longhorn.dvrexplorer.fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.module.wifi.CommandType;

import java.util.ArrayList;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class FileFragment_PHO1 extends FileFragment_BaseGrid {

    public FileFragment_PHO1() {
    }

    public static FileFragment_PHO1 newInstance() {
        Bundle args = new Bundle();
        FileFragment_PHO1 fragment = new FileFragment_PHO1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public byte[] getCommandType() {
        return CommandType.GET_FILE_PHO;
    }

    @Override
    public void onItemClick(View view, int pos) {
        if(isEdit){
            mList.get(pos).isSelect = !mList.get(pos).isSelect;
            adapater.notifyDataSetChanged();
        }else {
            FileFragment_VIEW fragment = FileFragment_VIEW.newInstance((ArrayList<DvrFile>) mList, pos);
//            ((DVRActivity) getActivity()).addFragment(this, fragment);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.ac_dvr_fm01, fragment).commit();
        }
    }

}
