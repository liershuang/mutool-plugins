package com.mutool.mock.util;

import cn.hutool.core.util.StrUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/3 18:59<br>
 */
public class XmlUtil extends cn.hutool.core.util.XmlUtil {

    public static boolean hasTag(String xmlContent, String tagName) {
        if(StrUtil.isBlank(xmlContent)){
            return false;
        }
        if(StrUtil.isBlank(tagName)){
            throw new IllegalArgumentException("标签参数为空");
        }
        Document doc = readXML(xmlContent);
        NodeList tagNodeList = doc.getElementsByTagName(tagName);
        return tagNodeList.getLength() > 0;
    }

}
