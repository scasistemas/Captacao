package br.com.mobiwork.captacao.util;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by LuisGustavo on 29/05/2015.
 */
public class FolderConfig {



    public static boolean verConfig(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao" ;  // Juares
                file = new File(dirMercadorName);
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            return false;
                        }
                    }
        }
        return true;
    }

    public static String  getExternalStorageDirectorySD(Context ctx) {

        ctx.getExternalFilesDir(null);
        String files=System.getenv("SECONDARY_STORAGE") + "/Android/data/br.com.mobiwork.captacao/files/";
        File file = new File(files);
        if(file.exists()){
            return  System.getenv("SECONDARY_STORAGE") + "/Android/data/br.com.mobiwork.captacao/files/";
        }else{
            files=getExternalStorageDirectoryCopia();
             file = new File(files);
            if(file.exists()){
                return getExternalStorageDirectoryCopia();
            }else {
                return "Pasta nao Localizada";
            }
        }
    }


    public static String getExternalStorageDirectory(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
    public static String getExternalStorageDirectoryCopia(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//external_sd/captacaobk" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//external_sd/captacaobk" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//external_sd/captacaobk" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }



    public static String getExternalStorageDirectoryBackupSegDiario(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao/backup/diario" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdirs();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao/backup" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdirs();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao/backup" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdirs();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
    public static String getExternalStorageDirectoryBackupSeg(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao/backup" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdirs();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao/backup" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdirs();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao/backup" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdirs();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
    public static String getExternalStorageDirectoryTempTxt(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao/imptemp" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdirs();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao/backup" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdirs();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao/backup" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdirs();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }

    public static String getExternalStorageDirectoryVs(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//captacao/vs" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//captacao/vs" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//captacao/vs" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path=null;
            String secStore="";
             secStore = System.getenv("SECONDARY_STORAGE");

            if(secStore!=null){
                 path = new File(secStore);
            }
            if(path==null){
                secStore = FolderConfig.getExternalStorageDirectoryCopia();
                path = new File(secStore);
            }
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "Nao esta disponivel";
        }

    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


}
