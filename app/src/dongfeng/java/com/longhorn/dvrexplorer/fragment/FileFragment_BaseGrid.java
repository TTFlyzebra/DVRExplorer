package com.longhorn.dvrexplorer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.adapter.FileAdapater;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.module.wifi.DataParse;
import com.longhorn.dvrexplorer.module.wifi.ResultData;
import com.longhorn.dvrexplorer.module.wifi.SocketResult;
import com.longhorn.dvrexplorer.module.wifi.SocketTools;
import com.longhorn.dvrexplorer.utils.ByteTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by FlyZebra on 2018/5/17.
 * Descrip:
 */

public abstract class FileFragment_BaseGrid extends Fragment implements SocketResult,FileAdapater.OnItemClickListener {
    protected List<DvrFile> mList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button bt_file_up, bt_file_down, bt_file_selectall, bt_file_selectnone, bt_file_cancle, bt_file_del, bt_file_bj;
    private TextView tv_sum_info;
    private boolean isEdit = false;
    private FileAdapater adapater;
    private int sumItem = 0;
    private int page = 0;
    private int first = 0;
    private int last = 0;

    public FileFragment_BaseGrid() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.file_rv01);
        bt_file_up = view.findViewById(R.id.file_up);
        bt_file_down = view.findViewById(R.id.file_down);
        bt_file_selectall = view.findViewById(R.id.file_selectall);
        bt_file_selectnone = view.findViewById(R.id.file_selectnone);
        bt_file_cancle = view.findViewById(R.id.file_cancle);
        bt_file_del = view.findViewById(R.id.file_del);
        bt_file_bj = view.findViewById(R.id.file_bj);
        tv_sum_info = view.findViewById(R.id.file_sum_info_tv);

        adapater = new FileAdapater(getActivity(), mList, recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(adapater);

        adapater.setOnItemClickListener(this);

        showButtonView(isEdit);

        bt_file_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList != null && !mList.isEmpty()) {
                    isEdit = true;
                    showButtonView(true);
                    for (DvrFile dvrFile : mList) {
                        dvrFile.isShowCheck = true;
                    }
                    adapater.notifyDataSetChanged();
                }
            }
        });

        bt_file_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                showButtonView(false);
                for (DvrFile dvrFile : mList) {
                    dvrFile.isSelect = false;
                    dvrFile.isShowCheck = false;
                }
                adapater.notifyDataSetChanged();
            }
        });

        bt_file_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page > 1) {
                    page--;
                    showPage();
                }
            }
        });

        bt_file_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((sumItem + 5) / 6 > page) {
                    page++;
                    showPage();
                }
            }
        });


        bt_file_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DvrFile dvrFile : mList) {
                    dvrFile.isSelect = true;
                }
                adapater.notifyDataSetChanged();
            }
        });

        bt_file_selectnone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DvrFile dvrFile : mList) {
                    dvrFile.isSelect = false;
                }
                adapater.notifyDataSetChanged();
            }
        });

        bt_file_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                int len[] = new int[]{0};
                byte[] bytes = DataParse.getDelCommandBytes(mList, len);
                byte[] command = new byte[len[0]];
                System.arraycopy(bytes, 0, command, 0, len[0]);
                SocketTools.getInstance().sendCommand(command, new SocketResult() {
                    @Override
                    public void result(ResultData msg) {
                        FlyLog.d("length=%d,data=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()));
                        v.setEnabled(true);
                        updata();
                    }
                });
            }
        });

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
        SocketTools.getInstance().sendCommand(getCommandType(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updata();
    }

    @Override
    public void onStop() {
        adapater.cancleAllTask();
        super.onStop();
    }

    @Override
    public void result(ResultData msg) {
        FlyLog.d("length=%d,data=%s,msg=%s", msg.getMark(), ByteTools.bytes2HexString(msg.getBytes()),msg.getMsg());
        if (msg.getMark() > 0) {
            try {
                mList.clear();
                byte[] data = msg.getBytes();
                int[] pos = {8};
                sumItem = ByteTools.bytes2Int(data, pos[0]);
                page = sumItem > 0 ? 1 : 0;
                last = sumItem > 6 ? 5 : sumItem - 1;
                pos[0] += 4;
                for (int i = 0; i < sumItem; i++) {
                    DvrFile dvrFile = DataParse.getDvrFile(data, pos);
                    mList.add(dvrFile);
                }
                if(isEdit) {
                    for (DvrFile dvrFile : mList) {
                        dvrFile.isShowCheck=true;
                    }
                }
                showPage();
                adapater.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showPage() {
        first = Math.max(0, (page - 1) * 6);
        int endfirst = first;
        if ((first + 6) > sumItem) {
            last = sumItem - 1;
            endfirst = sumItem % 3 + (sumItem - 5);
        } else {
            last = first + 5;
        }
        recyclerView.scrollToPosition(first);
        first = endfirst;
        showPageInfo();
    }

    private void showPageInfo() {
        adapater.loadImageView(first, last);
        page = (first + 3) / 6 + 1;
        String text = Math.max(1,first + 1) + "-" + Math.max(1,last + 1) + "(" + sumItem + ")" + "   " + page + "/" + (sumItem + 5) / 6;
        bt_file_up.setEnabled(first >= 3);
        bt_file_down.setEnabled(last < (sumItem - 1));
        if(sumItem==0){
            tv_sum_info.setText("0-0(0) 0/0");
        }else{
            tv_sum_info.setText(text);
        }
    }

    public abstract void onItemClick(View view, int pos);
}
