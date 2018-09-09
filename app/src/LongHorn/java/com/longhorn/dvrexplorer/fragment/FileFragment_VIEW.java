package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.module.wifi.DataParse;
import com.longhorn.dvrexplorer.module.wifi.NioSocketTools;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;
import com.longhorn.dvrexplorer.view.FlyDialog;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;


/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public class FileFragment_VIEW extends Fragment implements SocketResult {
    public static final String LIST_KEY = "LIST_KEY";
    public static final String POS_KEY = "POS_KEY";
    private List<DvrFile> mList;
    private int mPos;
    private int mSize;
    private ViewPager viewPager;
    private MyPageAdapter pageAdapter;
    private Button bt_return, bt_left, bt_right, bt_del, bt_share;
    private NioSocketTools mNioSocketTools = NioSocketTools.getInstance();
    private boolean isStop = false;

    public FileFragment_VIEW() {
    }

    public static FileFragment_VIEW newInstance(ArrayList<DvrFile> list, int pos) {
        Bundle args = new Bundle();
        args.putInt(POS_KEY, pos);
        args.putParcelableArrayList(LIST_KEY, list);
        FileFragment_VIEW fragment = new FileFragment_VIEW();
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
        return inflater.inflate(R.layout.fragment_file_pho2, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.file_pho_viewpager);
        bt_return = view.findViewById(R.id.file_pho_return);
        bt_left = view.findViewById(R.id.file_pho_left);
        bt_right = view.findViewById(R.id.file_pho_right);
        bt_del = view.findViewById(R.id.file_pho_del);
        bt_share = view.findViewById(R.id.file_pho_share);

        pageAdapter = new MyPageAdapter();
        viewPager.setAdapter(pageAdapter);

        if (mPos > 0) {
            viewPager.setCurrentItem(mPos);
        }
        if (mSize == 0) {
            bt_left.setEnabled(false);
            bt_right.setEnabled(false);
            bt_del.setEnabled(false);
            bt_share.setEnabled(false);
        } else {
            upLeftRightButtonState();
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case SCROLL_STATE_IDLE:
                        mPos = viewPager.getCurrentItem();
                        upLeftRightButtonState();
                        break;
                }
            }
        });

        bt_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        bt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPos > 0) {
                    mPos--;
                    viewPager.setCurrentItem(mPos);
                    upLeftRightButtonState();
                }
            }
        });

        bt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPos < mSize - 1) {
                    mPos++;
                    viewPager.setCurrentItem(mPos);
                    upLeftRightButtonState();
                }
            }
        });

        bt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                FlyDialog.Builder delDialog = new FlyDialog.Builder(getActivity());
                delDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                        .setMessage(getString(R.string.file_del_dialog_singimg))//设置对话框的内容
                        .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bt_del.setEnabled(false);
                                int len[] = new int[]{0};
                                byte[] bytes = DataParse.getDelCommandBytes(mList.get(mPos), len);
                                byte[] command = new byte[len[0]];
                                System.arraycopy(bytes, 0, command, 0, len[0]);
                                mNioSocketTools.sendCommand(command);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void upLeftRightButtonState() {
        bt_left.setEnabled(mPos > 0);
        bt_right.setEnabled(mPos < mSize - 1);
    }

    @Override
    public void onStart() {
        super.onStart();
        mNioSocketTools.registerSocketResult(this);
        isStop = false;
    }

    @Override
    public void onStop() {
        isStop = true;
        mNioSocketTools.unregisterSocketResult(this);
        super.onStop();
    }

    private class MyPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(getActivity());
            Glide.with(getActivity()).load(mList.get(position).getUrl()).placeholder(R.drawable.load_range)
                    .error(R.drawable.load_range).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    @Override
    public void result(ResultData msg) {
        if (isStop) return;
        try {
            byte recv[] = msg.getBytes();
            int[] pos = {0};
            int command = ByteTools.bytes2ShortInt2(recv, pos[0]);
            switch (command) {
                //删除文件
                case 0x1021:
                    getActivity().onBackPressed();
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

    }

}
