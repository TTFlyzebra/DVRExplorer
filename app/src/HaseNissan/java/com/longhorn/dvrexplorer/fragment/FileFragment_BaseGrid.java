package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longhorn.dvrexplorer.DVRActivity;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.adapter.FileAdapater;
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

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public abstract class FileFragment_BaseGrid extends Fragment implements SocketResult, FileAdapater.OnItemClickListener, DVRActivity.IonBackPressedListener, View.OnClickListener {
    protected List<DvrFile> mList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button bt_file_up, bt_file_down, bt_file_selectall, bt_file_selectnone, bt_file_cancle, bt_file_del, bt_file_bj;
    private TextView tv_sum_info;
    private ProgressBar file_loading;
    protected boolean isEdit = false;
    protected FileAdapater adapater;
    private int sumItem = 0;
    private int page = 0;
    private int first = 0;
    private int last = 0;
    private int spanCount = 3;
    private ProgressDialog progressDialog;
    private boolean isStop = false;
    private NioSocketTools mNioSocketTools = NioSocketTools.getInstance();

    public FileFragment_BaseGrid() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity(),R.style.setProgressDialog);

        recyclerView = view.findViewById(R.id.file_rv01);
        bt_file_up = view.findViewById(R.id.file_up);
        bt_file_down = view.findViewById(R.id.file_down);
        bt_file_selectall = view.findViewById(R.id.file_selectall);
        bt_file_selectnone = view.findViewById(R.id.file_selectnone);
        bt_file_cancle = view.findViewById(R.id.file_cancle);
        bt_file_del = view.findViewById(R.id.file_del);
        bt_file_bj = view.findViewById(R.id.file_bj);
        tv_sum_info = view.findViewById(R.id.file_sum_info_tv);
        file_loading = view.findViewById(R.id.file_loading);

        adapater = new FileAdapater(getActivity(), mList, recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recyclerView.setAdapter(adapater);

        adapater.setOnItemClickListener(this);

        showButtonView(isEdit);

        bt_file_bj.setOnClickListener(this);
        bt_file_cancle.setOnClickListener(this);
        bt_file_up.setOnClickListener(this);
        bt_file_down.setOnClickListener(this);
        bt_file_selectall.setOnClickListener(this);
        bt_file_selectnone.setOnClickListener(this);
        bt_file_del.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        first = ((GridLayoutManager) (recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                        last = ((GridLayoutManager) (recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                        if (first >= 0) showPageInfo();
                        break;
                    default:
                        adapater.cancleAllTask();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    private void showButtonView(boolean isEdit) {
        if (isEdit) {
            bt_file_selectall.setVisibility(View.VISIBLE);
            bt_file_selectnone.setVisibility(View.VISIBLE);
            bt_file_cancle.setVisibility(View.VISIBLE);
            bt_file_del.setVisibility(View.VISIBLE);
            bt_file_bj.setVisibility(View.GONE);
        } else {
            bt_file_selectall.setVisibility(View.GONE);
            bt_file_selectnone.setVisibility(View.GONE);
            bt_file_cancle.setVisibility(View.GONE);
            bt_file_del.setVisibility(View.GONE);
            bt_file_bj.setVisibility(View.VISIBLE);
        }
    }

    public abstract byte[] getCommandType();

    private void updata() {
        file_loading.setVisibility(View.VISIBLE);
        mNioSocketTools.sendCommand(getCommandType());
    }

    @Override
    public void onStart() {
        super.onStart();
        mNioSocketTools.registerSocketResult(this);
        isStop = false;
    }

    @Override
    public void onResume() {
        FlyLog.d("onResume");
        super.onResume();
        updata();
    }

    @Override
    public void onPause() {
        FlyLog.d("onPause");
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DVRActivity) getActivity()).addIonBackPressedListener(this);
    }

    @Override
    public void onStop() {
        mNioSocketTools.unregisterSocketResult(this);
        adapater.cancleAllTask();
        isStop = true;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        ((DVRActivity) getActivity()).removeIonBackPressedListener(this);
        super.onDestroy();
    }

    private void showPage() {
        first = Math.max(0, (page - 1) * spanCount * 2);
        int endfirst = first;
        if ((first + spanCount * 2) > sumItem) {
            last = sumItem - 1;
            if (sumItem > spanCount * 2) {
                endfirst = sumItem + (spanCount - sumItem % spanCount) % spanCount - spanCount * 2;
            } else {
                first = 1;
            }
        } else {
            last = first + (spanCount * 2 - 1);
        }
        first = endfirst;
        recyclerViewMovePosition(recyclerView, first);
        showPageInfo();
    }

    int scrollY = 0;

    private void recyclerViewMovePosition(RecyclerView recyclerView, int first) {
        View view = recyclerView.getChildAt(0);
        if (view != null) {
            int hight = view.getHeight();
            FlyLog.d("child view height = %d", hight);
            int willScrollY = hight * (first / spanCount) - scrollY;
            recyclerView.scrollBy(0, willScrollY);
            scrollY = willScrollY + scrollY;
        }
    }

    private void showPageInfo() {
        adapater.loadImageView(first, last);
        page = (first + 3) / 6 + 1;
        String text = Math.max(1, first + 1) + "-" + Math.max(1, last + 1) + "(" + sumItem + ")" + "   " + page + "/" + (sumItem + 5) / 6;
        bt_file_up.setEnabled(first >= 3);
        bt_file_down.setEnabled(last < (sumItem - 1));
        if (sumItem == 0) {
            tv_sum_info.setText("0-0(0) 0/0");
        } else {
            tv_sum_info.setText(text);
        }
    }

    public abstract void onItemClick(View view, int pos);

    @Override
    public void onBackPressed() {
        updata();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.file_bj:
                    if (mList != null && !mList.isEmpty()) {
                        isEdit = true;
                        showButtonView(true);
                        for (DvrFile dvrFile : mList) {
                            dvrFile.isShowCheck = true;
                        }
                        adapater.notifyDataSetChanged();
                    }
                    break;
                case R.id.file_cancle:
                    isEdit = false;
                    showButtonView(false);
                    for (DvrFile dvrFile : mList) {
                        dvrFile.isSelect = false;
                        dvrFile.isShowCheck = false;
                    }
                    adapater.notifyDataSetChanged();
                    break;
                case R.id.file_up:
                    if (page > 1) {
                        page--;
                        showPage();
                    }
                    break;
                case R.id.file_down:
                    if ((sumItem + 5) / 6 > page) {
                        page++;
                        showPage();
                    }
                    break;
                case R.id.file_selectall:
                    for (DvrFile dvrFile : mList) {
                        dvrFile.isSelect = true;
                    }
                    adapater.notifyDataSetChanged();
                    break;
                case R.id.file_selectnone:
                    for (DvrFile dvrFile : mList) {
                        dvrFile.isSelect = false;
                    }
                    adapater.notifyDataSetChanged();
                    break;
                case R.id.file_del:
                    FlyDialog.Builder delDialog = new FlyDialog.Builder(getActivity());
                    int delNum = 0;
                    for (DvrFile dvrFile : mList) {
                        if (dvrFile.isSelect) delNum++;
                    }
                    delDialog.setTitle(getString(R.string.alert1))//设置对话框的标题
                            .setMessage(String.format(getString(R.string.file_del_dialog), delNum))//设置对话框的内容
                            .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getString(R.string.file_del_running));
                                    progressDialog.show();
                                    bt_file_del.setEnabled(false);
                                    int len[] = new int[]{0};
                                    byte[] bytes = DataParse.getDelCommandBytes(mList, len);
                                    byte[] command = new byte[len[0]];
                                    System.arraycopy(bytes, 0, command, 0, len[0]);
                                    mNioSocketTools.sendCommand(command);
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            updata();
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
                //拍照
                case 0x1002:
                    mList.clear();
                    byte[] data = msg.getBytes();
                    pos[0] += 2;
                    sumItem = ByteTools.bytes2Int(data, pos[0]);
                    if (page == 0) {
                        page = sumItem > 0 ? 1 : 0;
                    } else {
                        page = Math.min((sumItem - 1) / 6 + 1, page);
                    }
                    last = sumItem > 6 ? 5 : sumItem - 1;
                    pos[0] += 4;
                    for (int i = 0; i < sumItem; i++) {
                        DvrFile dvrFile = DataParse.getDvrFile(data, pos);
                        mList.add(dvrFile);
                    }

                    if (mList.size() < 1) {
                        isEdit = false;
                        showButtonView(isEdit);
                    }

                    if (isEdit) {
                        for (DvrFile dvrFile : mList) {
                            dvrFile.isShowCheck = true;
                        }
                    }
                    showPage();
                    adapater.update();
                    file_loading.setVisibility(View.GONE);
                    break;
                case 0x1021:
                    progressDialog.dismiss();
                    bt_file_del.setEnabled(true);
                    updata();
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }
}
