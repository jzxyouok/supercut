package org.FFmpeg;

/**
 * Created by bear on 16-5-17.
 */
public class FFmpegNative {
    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("swscale-4");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("ffmpeg_codec");//自定义的c文件
    }
    //
    //public  native String getStringFromNative();
}
