package com.jlinc.android.hyqfsad.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


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

    public static void setValue(String strKey, String strValue, String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));
            Element root = document.getDocumentElement();
            root.getElementsByTagName(strKey).item(0).setTextContent(strValue);
            output(root, xmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void output(Node node, String filename) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            // 设置各种输出属性
            transformer.setOutputProperty("encoding", "utf-8");
            transformer.setOutputProperty("indent", "yes");
            DOMSource source = new DOMSource();
            // 将待转换输出节点赋值给DOM源模型的持有者(holder)
            source.setNode(node);
            StreamResult result = new StreamResult();
            if (filename == null) {
                // 设置标准输出流为transformer的底层输出目标
                result.setOutputStream(System.out);
            } else {
                result.setOutputStream(new FileOutputStream(filename));
            }
            // 执行转换从源模型到控制台输出流
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除元素
     * @param nodeName
     *         节点名称
     */
    public static void deleteNode(String nodeName, String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));
            Element documentElement = document.getDocumentElement(); //根节点

            NodeList elementsByTagName = documentElement.getElementsByTagName(nodeName);
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                documentElement.removeChild(elementsByTagName.item(i));

            }
            output(document, xmlPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyNode(String strKey, String strValue, String xmlPath){
//        deleteNode(strKey,xmlPath);
        setValue(strKey,strValue,xmlPath);
    }
}
