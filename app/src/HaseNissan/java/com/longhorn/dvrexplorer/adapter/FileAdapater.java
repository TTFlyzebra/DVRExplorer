package com.longhorn.dvrexplorer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longhorn.dvrexplorer.R;
import com.longhorn.dvrexplorer.data.DvrFile;
import com.longhorn.dvrexplorer.http.HttpDown;
import com.longhorn.dvrexplorer.module.cache.DoubleBitmapCache;
import com.longhorn.dvrexplorer.utils.BitmapTools;
import com.longhorn.dvrexplorer.utils.FlyLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-3-29-下午3:06.
 */

public class FileAdapater extends RecyclerView.Adapter<FileAdapater.ViewHolder> {
    private static final int smallImageWidth = 128;
    private static final int smallImageHeight = 86;
    private static final int smallImageSize = smallImageWidth * smallImageHeight * 2;
    private List<DvrFile> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private Set<GetDvrVideoBitmatTask> tasks = new HashSet<>();
    private DoubleBitmapCache doubleBitmapCache;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FileAdapater(Context context, List<DvrFile> list, RecyclerView recyclerView) {
        mContext = context;
        mList = list;
        mRecyclerView = recyclerView;
        doubleBitmapCache = DoubleBitmapCache.getInstance(context.getApplicationContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            final DvrFile dvrFile = mList.get(position);
            holder.imageView.setTag(R.id.glideid, position);
            holder.checkBox.setTag(position);
            if (2 == dvrFile.type) {
                Glide.with(mContext).load(dvrFile.getUrl())
                        .placeholder(R.drawable.load_photo_loading).error(R.drawable.load_photo_failed)
                        .into(holder.imageView);
            } else {
                holder.imageView.setTag(dvrFile.getUrl());
//            if (0 == dvrFile.offset) {
//                holder.imageView.setImageResource(0 == dvrFile.type ? R.drawable.load_video_nor : R.drawable.load_video_evt);
//            } else {
                Bitmap bitmap = doubleBitmapCache.get(dvrFile.getUrl());
                if (null != bitmap) {
                    holder.imageView.setImageBitmap(bitmap);
                } else {
                    holder.imageView.setImageResource(0 == dvrFile.type ? R.drawable.load_video_nor : R.drawable.load_video_evt);
                }
//            }
            }
            holder.textView.setText(dvrFile.getTime());
            holder.checkBox.setVisibility(dvrFile.isShowCheck ? View.VISIBLE : View.GONE);
            holder.checkBox.setChecked(dvrFile.isSelect);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag(R.id.glideid);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, pos);
                    }
                }
            });
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = (int) buttonView.getTag();
                    mList.get(pos).isSelect = isChecked;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    public void cancleAllTask() {
        for (GetDvrVideoBitmatTask task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    public void loadImageView(int first, int last) {
        FlyLog.d("loadImageView %d-%d", first, last);
        try {
            if (mList == null || first < 0 || first >= mList.size() || last < 0 || last >= mList.size()) {
                return;
            }
            if (mList.get(first).type == 2) {
                return;
            }
            for (int i = first; i <= last; i++) {
                Bitmap bitmap = doubleBitmapCache.get(mList.get(i).getUrl());
                if (null != bitmap) {
                    ImageView imageView = mRecyclerView.findViewWithTag(mList.get(i));
                    if (null != imageView) {
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    GetDvrVideoBitmatTask task = new GetDvrVideoBitmatTask(mList.get(i));
                    task.execute(mList.get(i).getUrl());
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_photo_iv);
            textView = itemView.findViewById(R.id.item_photo_tv);
            checkBox = itemView.findViewById(R.id.item_photo_ck);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void update() {
        this.notifyDataSetChanged();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int first = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int last = ((GridLayoutManager) (mRecyclerView.getLayoutManager())).findLastVisibleItemPosition();
                if (first >= 0 && last >= first) {
                    loadImageView(first, last);
                }
            }
        }, 0);

    }


    public class GetDvrVideoBitmatTask extends AsyncTask<String, Bitmap, Bitmap> {
        private DvrFile dvrFile;

        GetDvrVideoBitmatTask(DvrFile dvrFile) {
            this.dvrFile = dvrFile;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                final String path = strings[0];
                if (0 != dvrFile.offset) {
                    byte[] bytes = HttpDown.downRange(path, dvrFile.offset, smallImageSize);
                    if (null != bytes && smallImageSize == bytes.length) {
                        bitmap = BitmapTools.rawByteArray2RGBABitmap2(bytes, smallImageWidth, smallImageHeight);
                    }
                }
//                else {
//                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                    retriever.setDataSource(strings[0], new HashMap<String, String>());
//                    bitmap = retriever.getFrameAtTime(0);
//                    if (bitmap != null) {
//                        bitmap = BitmapTools.zoomImg(bitmap, smallImageWidth, smallImageHeight);
//                    }
//                }
                if (bitmap != null) {
                    if (doubleBitmapCache != null) {
                        doubleBitmapCache.put(path, bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = mRecyclerView.findViewWithTag(dvrFile.getUrl());
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.load_photo_failed);
                }
            }
        }

    }
}
