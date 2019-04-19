package com.jlinc.android.hyqfsad.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Created by yan on 2015-06-27.
 */
public class XmlUtils {

    public static String getValue(String strKey,String xmlPath) {

        File xmlFlie = new File(xmlPath);
        InputStream inStream = null;
        String strRent = "";
        try {
            inStream = new FileInputStream(xmlFlie);
            XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
            //获取XmlPullParser的实例
            XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
            xmlPullParser.setInput(inStream, "utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {//判断文件是否是文件的结尾，END_DOCUMENT文件结尾常量
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT://文件开始，START_DOCUMENT文件开始开始常量
                        break;
                    case XmlPullParser.START_TAG://元素标签开始，START_TAG标签开始常量
                        String name = xmlPullParser.getName();
                        if ((strKey.equals(name))) {
                            strRent = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG://元素标签结束，END_TAG结束常量
                        break;
                    default:
                        break;
                }
                //获取当前元素标签的类型
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strRent;

    }
    

}
