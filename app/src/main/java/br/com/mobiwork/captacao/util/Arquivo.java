package br.com.mobiwork.captacao.util;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class Arquivo {

    Context c ;
    public Arquivo(Context ctx){
        c=ctx;
    }

    public void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public void unzip(String src, String dest){

        final int BUFFER_SIZE = 4096;

        BufferedOutputStream bufferedOutputStream = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(src);
            ZipInputStream zipInputStream
                    = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null){

                String zipEntryName = zipEntry.getName();
                java.io.File file = new java.io.File(dest + zipEntryName);

                if (file.exists()){
                    file.delete();
                }
                if(zipEntry.isDirectory()){
                    file.mkdirs();
                }else{
                    byte buffer[] = new byte[BUFFER_SIZE];
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bufferedOutputStream
                            = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
                    int count;

                    while ((count
                            = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bufferedOutputStream.write(buffer, 0, count);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
            }
            //}
            zipInputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
