package com.flyzebra.flydown.download;

/**
 * Created by FlyZebra on 2016/9/11.
 */
public class DownFileBean {
    /**
     * 影片下载地址
     */
    private String url;
    /**
     * 影片的名称
     */
    private String name;
    /**
     * 下载的状态
     */
    private int status;
    /**
     * 下载的百分比
     */
    private int percent;
    /**
     * 影片图片的URL地址
     */
    private String imgUrl;
    /**
     * 下载的文件的存放地址
     */
    private String filePath;
    /**
     * M3U8转换成本地播放文件后的文件保存位置
     */
    private String localPath;
    /**
     * m3u8文件已经下载到第几个子ts文件,-1表示未开始
     */
    private int tsNum;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 已下载文件大小
     */
    private long downSize;
    /**
     * 当前下载速度
     */
    private long downSpeed;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getTsNum() {
        return tsNum;
    }

    public void setTsNum(int tsNum) {
        this.tsNum = tsNum;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownSize() {
        return downSize;
    }

    public void setDownSize(long downSize) {
        this.downSize = downSize;
    }

    public long getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(long downSpeed) {
        this.downSpeed = downSpeed;
    }

    @Override
    public String toString() {
        return "DownFileBean{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", percent=" + percent +
                ", imgUrl='" + imgUrl + '\'' +
                ", filePath='" + filePath + '\'' +
                ", localPath='" + localPath + '\'' +
                ", tsNum=" + tsNum +
                ", fileSize=" + fileSize +
                ", downSize=" + downSize +
                ", downSpeed=" + downSpeed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownFileBean that = (DownFileBean) o;

        if (status != that.status) return false;
        if (percent != that.percent) return false;
        if (tsNum != that.tsNum) return false;
        if (fileSize != that.fileSize) return false;
        if (downSize != that.downSize) return false;
        if (downSpeed != that.downSpeed) return false;
        if (!url.equals(that.url)) return false;
        if (!name.equals(that.name)) return false;
        if (!imgUrl.equals(that.imgUrl)) return false;
        if (!filePath.equals(that.filePath)) return false;
        return localPath.equals(that.localPath);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + status;
        result = 31 * result + percent;
        result = 31 * result + imgUrl.hashCode();
        result = 31 * result + filePath.hashCode();
        result = 31 * result + localPath.hashCode();
        result = 31 * result + tsNum;
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + (int) (downSize ^ (downSize >>> 32));
        result = 31 * result + (int) (downSpeed ^ (downSpeed >>> 32));
        return result;
    }
}
