package com.slwb.supercut;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Shortens/Crops a track
 */
public class editWithJava {

    public static void startTrim(String src, File dst, int startMs, int endMs) throws IOException {


        Movie movie = MovieCreator.build(src);

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

            for (Track t : movie.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);

                    //audioTracks.get(0)
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }

        Long fps=movie.getTimescale();
        System.out.println("timeScale====="+fps);
        movie.setTracks(new LinkedList<Track>());
        for (Track track : videoTracks) {
            printTime(track);
        }

        double startTime = startMs/1000;
        double endTime = endMs/1000;

        boolean timeCorrected = false;

        //
        for (Track track : videoTracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {

                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                startTime = correctTimeToSyncSample(track, startTime, false);//true
                endTime = correctTimeToSyncSample(track, endTime, true);//false
                timeCorrected = true;
            }
        }
        System.out.println("trim startTime-->"+startTime);
        System.out.println("trim endTime-->"+endTime);
        int x = 0;

        //
        for (Track track : videoTracks) {
            long currentSample = 0;
            double currentTime = 0;
            long startSample = -1;
            long endSample = -1;
            x++;
            for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
                TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
                for (int j = 0; j < entry.getCount(); j++) {

                    if (currentTime <= startTime) {

                        startSample = currentSample;
                    }
                    if (currentTime <= endTime) {

                        endSample = currentSample;
                    } else {
                        break;
                    }
                    currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
            }

            System.out.println("trim startSample-->"+startSample);
            System.out.println("trim endSample-->"+endSample);
            movie.addTrack(new CroppedTrack(track, startSample, endSample));

            break;
        }
        for (Track track : audioTracks) {
            long currentSample = 0;
            double currentTime = 0;
            long startSample = -1;
            long endSample = -1;
            x++;

            //
            for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
                TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
                for (int j = 0; j < entry.getCount(); j++) {

                    if (currentTime <= startTime) {
                        startSample = currentSample;
                    }
                    if (currentTime <= endTime) {
                        endSample = currentSample;
                    } else {
                        break;
                    }
                    currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
            }

            System.out.println("trim startSample-->"+startSample);
            System.out.println("trim endSample-->"+endSample);
            movie.addTrack(new CroppedTrack(track, startSample, endSample));
            break;
        }

        Container container = new DefaultMp4Builder().build(movie);

        if (!dst.exists()) {
            dst.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(dst);
        FileChannel fc = fos.getChannel();
        container.writeContainer(fc);

        fc.close();
        fos.close();
    }

    public void appendVideo(String[] videos,String outFile) throws IOException{
        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for(String video:videos)//遍历数组 创建Movie对象
        {
            inMovies[index] = MovieCreator.build(video);
            index++;
        }

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                    //audioTracks.get(0)
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();

        //把每段视频的音频和视频包分别合并成一个大包
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        //生成最后的视频文件
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(String.format(outFile), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }

    protected static long getDuration(Track track) {
        long duration = 0;
        for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
            duration += entry.getCount() * entry.getDelta();
        }
        return duration;
    }

    //
    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {

                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    //
    private static void printTime(Track track) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;

        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {

                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                    System.out.println("currentTime-->"+currentTime);
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }

    }

    //
    public  void merge(List<String> fileList,String outFile ){
        List<Movie> moviesList = new LinkedList<Movie>();

        try
        {
            for (String file : fileList)
            {
                moviesList.add(MovieCreator.build(file));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : moviesList)
        {
            for (Track t : m.getTracks())
            {
                if (t.getHandler().equals("soun"))
                {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide"))
                {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();

        try
        {
            if (audioTracks.size() > 0)
            {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0)
            {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }
        }
        catch (IOException e)
        {
            System.out.println("合成视频,音频轨道失败");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Container out = new DefaultMp4Builder().build(result);
        Log.e("使用序列:",fileList+"");

        try
        {
            FileChannel fc = new RandomAccessFile(outFile, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
            Log.e("合成状态：","成功");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            Log.e("合成状态：","失败");
            e.printStackTrace();
        }

        moviesList.clear();
        fileList.clear();
    }

}