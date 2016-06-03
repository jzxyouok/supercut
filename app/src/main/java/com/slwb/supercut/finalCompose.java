package com.slwb.supercut;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bear on 15-11-29.
 */
public class finalCompose {

    stringUtil su=new stringUtil();
    int number=0;
    float starttime =0,endtime=0;
    String src0=null;
    String outsrc=null;//输出地址

    List<String> cutxml=new ArrayList<String>();
    editWithJava ewj=new editWithJava();

    public void setFinalCompose(List<String> cutXml,int num){
        this.number=num;
        this.cutxml=cutXml;
    }

    public void Merge (){

        int j=1;

        for(j=1;j<number*3;j+=3){
            starttime=Float.parseFloat(cutxml.get(j));
            endtime=Float.parseFloat(cutxml.get(j+1));

            if(starttime==0&&endtime==1){
                Log.e(cutxml.get(j-1)+"","没有进行过剪辑操作");
            }else{
                Log.e(cutxml.get(j-1)+"","进行过剪辑操作");

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(cutxml.get(j-1));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int microsecond = Integer.valueOf(time);
                starttime=starttime*microsecond;
                endtime=endtime*microsecond;

                StartTrim(cutxml.get(j - 1), (int)starttime, (int)endtime);
                cutxml.set(j - 1, src0);
                System.out.println("临时文件"+src0+" "+starttime+":"+endtime);
            }

        }
        outsrc=Environment.getExternalStorageDirectory() + su.getNameByTime();

        ewj.merge(getCutSequence(), outsrc);

    }

    public List<String> getCutSequence(){//获取剪辑的序列

        List<String> sequence=new ArrayList<String>();

        int i=1;

        for(i=0;i<3*number;i+=3) {
            sequence.add(cutxml.get(i));
        }
        return sequence;
    }

    public void StartTrim(String src,int start,int end){//剪辑

        src0=Environment.getExternalStorageDirectory() +
                "/剪影/视频池/" + su.getFileName(src) + ".mp4";

        try {
            File file=new File(src0);
            ewj.startTrim(src,file,start,end);
            Log.e("剪辑状态:","失败！");

        } catch (IOException e) {
            Log.e("剪辑状态:","失败！");
            e.printStackTrace();
        }

    }



}
