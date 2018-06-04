package com.longhorn.dvrexplorer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.flydown.download.DownFileManager;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.module.cache.DoubleBitmapCache;
import com.longhorn.dvrexplorer.ui.PlayVideoActivity;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class VideoAdapater extends RecyclerView.Adapter<VideoAdapater.ViewHolder> implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private Context mContext;
    private Set<GetUrlVideoBitmatTask> tasks = new HashSet<>();
    private DoubleBitmapCache doubleBitmapCache;
    private static final Executor executor = Executors.newFixedThreadPool(1);

    public VideoAdapater(Context context, List<String> list, RecyclerView recyclerView) {
        mContext = context;
        mList = list;
        mRecyclerView = recyclerView;
        doubleBitmapCache = DoubleBitmapCache.getInstance(context.getApplicationContext());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case 0:
                        int first = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                        int last = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findLastVisibleItemPosition();
                        GetBitmap(first, last);
                        break;
                    default:
                        cancleAllTask();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    public void cancleAllTask() {
        for (GetUrlVideoBitmatTask task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    private void GetBitmap(int first, int last) {
        for (int i = first; i <= last; i++) {
            Bitmap bitmap = doubleBitmapCache.get(mList.get(i));
            if (bitmap != null) {
                ImageView imageView = mRecyclerView.findViewWithTag(mList.get(i));
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                GetUrlVideoBitmatTask task = new GetUrlVideoBitmatTask(mList.get(i));
                task.execute(mList.get(i));
                tasks.add(task);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.iv01.setTag("1" + mList.get(position));
        Bitmap bitmap = doubleBitmapCache.get(mList.get(position));
        if (bitmap != null) {
            holder.iv01.setImageBitmap(bitmap);
        } else {
            holder.iv01.setImageResource(R.drawable.img_index);
            GetUrlVideoBitmatTask task = new GetUrlVideoBitmatTask(mList.get(position));
            task.execute(mList.get(position));
            tasks.add(task);
        }

        holder.iv01.setOnClickListener(this);

        String url = mList.get(position);
        String str = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
        String time = "时间：" + str.substring(0, 4) + "年" + str.substring(4, 6) + "月" + str.substring(6, 8) + "日" + str.substring(9, 11) + ":" + str.substring(11, 13) + ":" + str.substring(13);

        holder.tv01.setText(time);
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onClick(View v) {
        String path = (String) v.getTag();
        String url = path.substring(1);
        switch (v.getId()) {
            case R.id.itme_iv01:
            case R.id.item_ib01:
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.putExtra(PlayVideoActivity.PLAY_URL, url);
                mContext.startActivity(intent);
//                GiraffePlayerActivity.configPlayer((Activity) mContext).play(url);
                break;
            case R.id.item_ib02:
                int fir = url.lastIndexOf('/') + 1;
                String name = url.substring(fir);
                DownFileManager.getInstance().addDownUrl(url, name, url);
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv01;
        TextView tv01;

        ViewHolder(View itemView) {
            super(itemView);
            iv01 = itemView.findViewById(R.id.itme_iv01);
            tv01 = itemView.findViewById(R.id.item_tv01);
        }
    }

    @SuppressLint("StaticFieldLeak")
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
            FlyLog.d("bitmap=" + bitmap);
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            ImageView imageView = mRecyclerView.findViewWithTag("1" + url);
            if (imageView != null) {
                imageView.setImageBitmap(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
        }

    }

}
