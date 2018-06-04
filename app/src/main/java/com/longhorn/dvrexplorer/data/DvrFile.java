package com.longhorn.dvrexplorer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.longhorn.dvrexplorer.utils.DateTools;

/**
 * Created by FlyZebra on 2018/5/21.
 * Descrip:针对wifi1.5协议解析出的单个文件
 */

public class DvrFile implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeByte(path);
        dest.writeByte(type);
        dest.writeByte(suffix);
        dest.writeByte(reserved);
        dest.writeInt(size);
        dest.writeInt(date);
        dest.writeInt(offset);
        dest.writeByte((byte) (isShowCheck ? 1 : 0));
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }

    /**
     * 文件索引
     */
    public int index;
    /**
     * 0-/NOR; 1-/EVT; 2-/PHO;
     */
    public byte path;
    /**
     * 0-"NOR_"; 1-"EVT_"; 2-"PHO_"; 3-"_D1_";
     */
    public byte type;
    /**
     * 0-".mp4"; 1-".jpg"
     */
    public byte suffix;

    /**
     *
     */
    public byte reserved;
    /**
     * 文件大小
     */
    public int size;
    /**
     * File created time from 1970/1/1-0:0:0 ,unit seconds
     */
    public int date;
    /**
     * Bytes offset of .mp4 file缩略图起始位置
     */
    public int offset;

    public boolean isShowCheck = false;

    public boolean isSelect = false;

    public DvrFile() {
    }

    protected DvrFile(Parcel in) {
        index = in.readInt();
        path = in.readByte();
        type = in.readByte();
        suffix = in.readByte();
        reserved = in.readByte();
        size = in.readInt();
        date = in.readInt();
        offset = in.readInt();
        isShowCheck = in.readByte() != 0;
        isSelect = in.readByte() != 0;
    }

    public static final Creator<DvrFile> CREATOR = new Creator<DvrFile>() {
        @Override
        public DvrFile createFromParcel(Parcel in) {
            return new DvrFile(in);
        }

        @Override
        public DvrFile[] newArray(int size) {
            return new DvrFile[size];
        }
    };


    /**
     * 获取文件的WEB地址
     * @return 返回 http://******样式字符串
     */
    public String getUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(path == 0 ? Global.getPathNor() : (path == 1 ? Global.getPathEvt() : Global.getPathPho()));
        stringBuilder.append(DateTools.date2String(date * 1000L, DateTools.DATE_FORMAT_FILENAME));
        stringBuilder.append(suffix == 0 ? Global.DOWN_MP4 : ".JPG");
        return stringBuilder.toString();
    }

    /**
     * 获取mp4文件的web播放地址(因DVR mp4文件分为A,B A为下载，B为下载，如没有分类，同getUrl方法)
     * @return 返回 http://******.mp4样式字符串
     */
    public String getPlayUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(path == 0 ? Global.getPathNor() : (path == 1 ? Global.getPathEvt() : Global.getPathPho()));
        stringBuilder.append(DateTools.date2String(date * 1000L, DateTools.DATE_FORMAT_FILENAME));
        stringBuilder.append(suffix == 0 ? Global.PLAY_MP4 : ".JPG");
        return stringBuilder.toString();
    }


    /**
     * 获取文件的生成时间
     * @return 返回时间字符串，格式为yyyy-MM-dd HH:mm:ss
     */
    public String getTime() {
        return DateTools.date2String(date * 1000L);
    }


    @Override
    public String toString() {
        return "DvrFile{" +
                "index=" + index +
                ", path=" + path +
                ", type=" + type +
                ", suffix=" + suffix +
                ", reserved=" + reserved +
                ", size=" + size +
                ", date=" + date +
                ", offset=" + offset +
                '}';
    }

}
