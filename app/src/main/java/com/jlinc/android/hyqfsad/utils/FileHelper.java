package com.jlinc.android.hyqfsad.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileHelper {
    public static final String TAG = "FileHelper";
    public static String SDPATH = Environment.getExternalStorageDirectory().getPath() + "/";


    /**
     * 判断SDCard是否存在？是否可以进行读写
     */
    public static boolean SDCardState() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {//表示SDCard存在并且可以读写
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SDCard文件路径
     */
    public static String SDCardPath() {
        String SDPATH = Environment.getExternalStorageDirectory().getPath() + "/";
        return SDPATH;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName
     *         要创建的目录名
     * @return 创建得到的目录
     */
    public static void createSDDir(String... dirName) {
        for (String str:dirName){
            if (!FileHelper.isFileExist(str)){
                File dir = new File(SDPATH + str);
                dir.mkdir();
            }
        }

    }


    /**
     * 判断文件是否已经存在
     * @param fileName
     *         要检查的文件名
     * @return boolean, true表示存在，false表示不存在
     */

    public static boolean isFileExist(String fileName) {

        File file = new File(SDPATH + fileName);
        return file.exists();

    }

    /**
     * 删除单个文件
     */

    /** 删除单个文件
     * @param filePathName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePathName) {
        File file = new File(SDPATH+filePathName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e(TAG, "Copy_Delete.deleteSingleFile: 删除单个文件" + filePathName + "成功！");
                return true;
            } else {
                Log.e(TAG, "删除单个文件" + filePathName + "失败！");
                return false;
            }
        } else {
            Log.e(TAG, "删除单个文件失败：" + filePathName + "不存在！");
            return false;
        }
    }

    /**
     * copy assets中的xml文件到sd卡对应目录
     */

    public static void copyAssetsXmlTo(Context context, String str){
//        for (String str:xmlStrs){
            File file = new File(FileHelper.SDCardPath() + str);
            if (!FileHelper.isFileExist(str) || file.length() == 0) {
                String[] sstr = str.split("/");
                try {
                    InputStream is = context.getAssets().open(sstr[sstr.length-1]);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                        // buffer字节
                        fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                    }
                    fos.flush();// 刷新缓冲区
                    is.close();
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//        }
    }
}