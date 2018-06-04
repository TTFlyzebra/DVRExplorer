package com.longhorn.dvrexplorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.flydown.FlyDown;
import com.flyzebra.flydown.download.DownFileBean;
import com.flyzebra.flydown.download.DownFileManager;
import com.flyzebra.flydown.download.IDownStatus;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.module.cache.DoubleBitmapCache;
import com.longhorn.dvrexplorer.ui.PlayVideoActivity;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.HashMap;
import java.util.List;


/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class VideoLocAdapater extends RecyclerView.Adapter<VideoLocAdapater.ViewHolder> implements IDownStatus {
    private List<DownFileBean> mList;
    private Context mContext;
    private DoubleBitmapCache doubleBitmapCache;
    private RecyclerView mRecyclerView;

    public VideoLocAdapater(Context context, List<DownFileBean> list, RecyclerView recyclerView) {
        mContext = context;
        mList = list;
        mRecyclerView = recyclerView;
        doubleBitmapCache = DoubleBitmapCache.getInstance(context.getApplicationContext());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videoloc, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DownFileBean bean = mList.get(position);
        holder.iv_url.setTag(bean.getLocalPath());
        String text = "";
        switch (bean.getStatus()) {
            case COMPLETED:
                holder.iv_play.setImageResource(R.drawable.ic_cache_play);
                text = "下载已完成。";
                holder.tv_info.setTextColor(0xff111111);
                break;
            case PAUSED:
                holder.iv_play.setImageResource(R.drawable.ic_cache_pause);
                text = "暂停下载，" + "  已下载" + bean.getPercent() + "%";
                holder.tv_info.setTextColor(0xff7f7f7f);
                break;
            case WAITING:
                holder.iv_play.setImageResource(R.drawable.ic_cache_wait);
                text = "等待下载，" + "  已下载" + bean.getPercent() + "%";
                holder.tv_info.setTextColor(0xff7f7f00);
                break;
            case DOWNLOADING:
                holder.iv_play.setImageResource(R.drawable.ic_cache_down);
                text = "正在下载，" + "  已下载" + bean.getPercent() + "%。\r\n速度" + bean.getDownSpeed() + "KB/S";
                holder.tv_info.setTextColor(0xff00a000);
                break;
            case NON_WIFI_PAUSED:
                holder.iv_play.setImageResource(R.drawable.ic_cache_pause);
                text = "处于非Wifi连接状态已暂停下载。";
                holder.tv_info.setTextColor(0xffff0000);
                break;
            default:
                text = "未知状态，下载文件出错！";
                holder.tv_info.setTextColor(0xffffff00);
                break;
        }

        //设置图像
        switch (bean.getStatus()) {
            case COMPLETED:
                Bitmap bitmap = doubleBitmapCache.get(bean.getUrl());
                if (bitmap == null) {
                    GetUrlVideoBitmatTask task = new GetUrlVideoBitmatTask(bean.getUrl());
                    task.execute(bean.getLocalPath());
                } else {
                    holder.iv_url.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }


        holder.tv_title.setText(bean.getName());
        holder.tv_info.setText(text);


        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bean.getStatus()) {
                    case COMPLETED:
                        Intent intent = new Intent(mContext, PlayVideoActivity.class);
                        String playUrl = FlyDown.getSavePath(bean.getUrl());
                        intent.putExtra(PlayVideoActivity.PLAY_URL, playUrl);
                        mContext.startActivity(intent);
                        break;
                    case PAUSED:
                    case NON_WIFI_PAUSED:
                        DownFileManager.getInstance().startDownUrl(bean.getUrl());
                        break;
                    case DOWNLOADING:
                    case WAITING:
                        DownFileManager.getInstance().pauseDownUrl(bean.getUrl());
                        break;
                }
            }
        });

        holder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownFileManager.getInstance().delDownUrl(bean.getUrl());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_play;
        ImageView iv_url;
        ImageView iv_del;
        TextView tv_info;
        TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            iv_play = itemView.findViewById(R.id.iv_play);
            iv_url = itemView.findViewById(R.id.iv_url);
            iv_del = itemView.findViewById(R.id.iv_del);
            tv_info = itemView.findViewById(R.id.tv_info);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    public class GetUrlVideoBitmatTask extends AsyncTask<String, Bitmap, Bitmap> {
        private String url;

        GetUrlVideoBitmatTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
//                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
//                mmr.setDataSource(strings[0]);
//                mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
//                mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//                bitmap = mmr.getScaledFrameAtTime(0, FFmpegMediaMetadataRetriever.OPTION_CLOSEST, 320, 180); // frame at 2 seconds
//                mmr.release();
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(strings[0], new HashMap<String, String>());
                bitmap = media.getFrameAtTime(0);

                Matrix matrix = new Matrix();
                float scale = 320f / bitmap.getWidth();
                matrix.postScale(scale, scale);
                Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = bm == null ? bitmap : bm;
                final String path = url;
                if (bitmap != null) {
                    publishProgress(bitmap);
                    if (doubleBitmapCache != null) {
                        doubleBitmapCache.put(path, bitmap);
                    }
                }
                FlyLog.d("Get bitmap from http ok, url = %s, bitmap = " + bitmap, strings[0]);
            } catch (Exception e) {
                FlyLog.i("Get bitmap faile url = %s", strings[0]);
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            if (mRecyclerView != null) {
                ImageView imageView = mRecyclerView.findViewWithTag(url);
                if (imageView != null) {
                    imageView.setImageBitmap(values[0]);
                }
            }
        }

    }

}
