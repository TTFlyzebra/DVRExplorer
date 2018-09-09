package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.longhorn.dvrexplorer.R;

import java.lang.reflect.Constructor;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class FileFragment extends Fragment{
    public FileFragment(){
    }

    public static FileFragment newInstance() {
        Bundle args = new Bundle();
        FileFragment fragment = new FileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Button file_image,file_video,file_evt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_file,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        file_image = view.findViewById(R.id.file_image);
        file_video = view.findViewById(R.id.file_video);
        file_evt = view.findViewById(R.id.file_evt);

        replaceFragment("FileFragment_PHO1");
        file_image.setEnabled(false);

        file_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replaceFragment("FileFragment_PHO1")){
                    file_image.setEnabled(false);
                    file_video.setEnabled(true);
                    file_evt.setEnabled(true);
                }
            }
        });

        file_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replaceFragment("FileFragment_NOR1")){
                    file_image.setEnabled(true);
                    file_video.setEnabled(false);
                    file_evt.setEnabled(true);
                }
            }
        });

        file_evt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(replaceFragment("FileFragment_EVT1")){
                    file_image.setEnabled(true);
                    file_video.setEnabled(true);
                    file_evt.setEnabled(false);
                }
            }
        });
    }

    public boolean replaceFragment(String fName){
        try {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Class<?> cls = Class.forName("com.longhorn.dvrexplorer.fragment."+fName);
            Constructor<?> cons = cls.getConstructor();
            Fragment fragment = (Fragment) cons.newInstance(); //
            ft.replace(R.id.file_fm01, fragment).commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
