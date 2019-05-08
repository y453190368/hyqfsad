package com.jlinc.android.hyqfsad.utils;

public class ConstantUtils {


    /**
     * 文件夹
     */
    public static String QUEUINGFILE = "hyqfsad";
    public static String QUEUINGFILE_CONFIG = "hyqfsad/config";//配置文件
    public static String QUEUINGFILE_CONFIG_BACKUP = "hyqfsad/cfg_backup";//xml文件备份
    public static String QUEUINGFILE_DEFH5 = "hyqfsad/def_H5";//默认h5页面
    public static String QUEUINGFILE_APK = "hyqfsad/apk";//apk位置
    public static String QUEUINGFILE_APKNAME = "HYQFSADisplay.apk";//apk名字
    public static String QUEUINGFILE_CONFIG_XML = "hyqfsad/config/config.xml";//xml文件
    public static String QUEUINGFILE_CONFIG_BACKUP_XML = "hyqfsad/cfg_backup/cfg_backup.xml";//xml文件备份

    public static String[] QUEUINGFILES = {QUEUINGFILE,QUEUINGFILE_CONFIG,QUEUINGFILE_APK,QUEUINGFILE_DEFH5,QUEUINGFILE_CONFIG_BACKUP};


    /**
     * 常量
     */
    public static String CONFIG_WEBURL = "webUrl";//xml中weburl

    public static String CONFIG_DOWNLOADURL = "downLoadUrl";//xml中下载链接

    public static String CONFIG_ISSERVICE = "isService";//服务地址是否有效

    public static String CONFIG_SERVICENAME = "serviceName";//服务地址是否有效

    public static String CONFIG_PORT = "port";//端口号

    public static String CONFIG_COPYCONFIG = "copyConfig";//xml中copyConfig用于是否覆盖本地config.xml

    public static String CONFIG_ADMINCMD = "adminCmd";//xml中adminCmd用于执行退出
}
